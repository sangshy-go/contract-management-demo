package com.bank.contract.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.bank.contract.entity.ApprovalRecord;
import com.bank.contract.entity.Contract;
import com.bank.contract.entity.OperationLog;
import com.bank.contract.mapper.ApprovalRecordMapper;
import com.bank.contract.mapper.ContractMapper;
import com.bank.contract.mapper.OperationLogMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 合同服务类
 *
 * ⚠️ 预留问题和优化点：
 * 1. getContractList - N+1查询问题：循环查审批记录
 * 2. createContract - 参数校验缺失，可注入风险
 * 3. approve - 并发安全问题，无锁机制
 */
@Service
@RequiredArgsConstructor
public class ContractService {

    private final ContractMapper contractMapper;
    private final ApprovalRecordMapper approvalRecordMapper;
    private final OperationLogMapper operationLogMapper;

    /**
     * 分页查询合同列表
     * ⚠️ 问题：N+1 查询，每个合同都单独查询审批记录
     */
    public Page<Contract> getContractList(Integer pageNum, Integer pageSize,
                                          String contractNo, String customerName, String status) {
        Page<Contract> page = new Page<>(pageNum, pageSize);

        QueryWrapper<Contract> wrapper = new QueryWrapper<>();
        if (contractNo != null && !contractNo.isEmpty()) {
            wrapper.eq("contract_no", contractNo);
        }
        if (customerName != null && !customerName.isEmpty()) {
            wrapper.like("customer_name", customerName);
        }
        if (status != null && !status.isEmpty()) {
            wrapper.eq("status", status);
        }
        wrapper.orderByDesc("create_time");

        Page<Contract> result = contractMapper.selectPage(page, wrapper);

        // ⚠️ N+1 问题：对每个合同单独查询审批记录
        for (Contract contract : result.getRecords()) {
            QueryWrapper<ApprovalRecord> approvalWrapper = new QueryWrapper<>();
            approvalWrapper.eq("contract_id", contract.getId());
            List<ApprovalRecord> approvals = approvalRecordMapper.selectList(approvalWrapper);
            contract.setApprovalRecords(approvals);
        }

        return result;
    }

    /**
     * 获取合同详情
     */
    public Contract getContractById(Long id) {
        Contract contract = contractMapper.selectById(id);

        if (contract != null) {
            // 查询审批记录
            QueryWrapper<ApprovalRecord> approvalWrapper = new QueryWrapper<>();
            approvalWrapper.eq("contract_id", id);
            contract.setApprovalRecords(approvalRecordMapper.selectList(approvalWrapper));
        }

        return contract;
    }

    /**
     * 创建合同
     * ⚠️ 问题：参数校验缺失，contractNo可能重复，amount可能为负
     */
    @Transactional
    public Contract createContract(Contract contract) {
        // ⚠️ Bug风险：没有校验必填字段
        // 应该校验：contractNo, contractName, amount 等必填

        // ⚠️ 优化点：合同编号可以自动生成
        contract.setStatus("DRAFT");
        contract.setCreateTime(LocalDateTime.now());
        contract.setUpdateTime(LocalDateTime.now());

        contractMapper.insert(contract);

        // 记录操作日志
        saveOperationLog(contract.getId(), "系统管理员", "CREATE",
                         "创建合同: " + contract.getContractName(), "127.0.0.1");

        return contract;
    }

    /**
     * 更新合同
     */
    @Transactional
    public Contract updateContract(Contract contract) {
        Contract existing = contractMapper.selectById(contract.getId());
        if (existing == null) {
            throw new RuntimeException("合同不存在");
        }

        // ⚠️ 优化点：可以使用 BeanUtils.copyProperties，只更新非空字段

        contract.setUpdateTime(LocalDateTime.now());
        contractMapper.updateById(contract);

        saveOperationLog(contract.getId(), "系统管理员", "UPDATE",
                         "更新合同", "127.0.0.1");

        return contract;
    }

    /**
     * 审批合同
     * ⚠️ 问题：并发安全问题，多个审批人同时审批可能覆盖结果
     */
    @Transactional
    public Contract approve(Long contractId, String approver, Integer level, String result, String comment) {
        Contract contract = contractMapper.selectById(contractId);

        // ⚠️ Bug风险：如果contract为null会NPE
        if (contract.getStatus().equals("ARCHIVED")) {
            throw new RuntimeException("已归档的合同不能审批");
        }

        // ⚠️ 并发问题：没有乐观锁或悲观锁，可能被覆盖
        ApprovalRecord record = new ApprovalRecord();
        record.setContractId(contractId);
        record.setApprover(approver);
        record.setApproveLevel(level);
        record.setApproveResult(result);
        record.setApproveComment(comment);
        record.setApproveTime(LocalDateTime.now());
        approvalRecordMapper.insert(record);

        // 更新合同状态
        if ("PASS".equals(result)) {
            contract.setStatus("APPROVED");
        } else if ("REJECT".equals(result)) {
            contract.setStatus("REJECTED");
        }
        contract.setUpdateTime(LocalDateTime.now());
        contractMapper.updateById(contract);

        saveOperationLog(contractId, approver, "APPROVE",
                         "审批结果: " + result, "127.0.0.1");

        return contract;
    }

    /**
     * 归档合同
     * ⚠️ 问题：并发安全问题
     */
    @Transactional
    public Contract archive(Long contractId) {
        Contract contract = contractMapper.selectById(contractId);

        // ⚠️ Bug: 如果状态不是APPROVED，不能归档（但没有校验）

        contract.setStatus("ARCHIVED");
        contract.setUpdateTime(LocalDateTime.now());
        contractMapper.updateById(contract);

        saveOperationLog(contractId, "系统管理员", "ARCHIVE",
                         "归档合同", "127.0.0.1");

        return contract;
    }

    /**
     * 删除合同
     */
    @Transactional
    public void deleteContract(Long id) {
        contractMapper.deleteById(id);

        // 删除关联的审批记录
        QueryWrapper<ApprovalRecord> approvalWrapper = new QueryWrapper<>();
        approvalWrapper.eq("contract_id", id);
        approvalRecordMapper.delete(approvalWrapper);

        saveOperationLog(id, "系统管理员", "DELETE",
                         "删除合同", "127.0.0.1");
    }

    /**
     * 保存操作日志
     */
    private void saveOperationLog(Long contractId, String operator, String operation,
                                   String detail, String ipAddress) {
        OperationLog log = new OperationLog();
        log.setContractId(contractId);
        log.setOperator(operator);
        log.setOperation(operation);
        log.setDetail(detail);
        log.setIpAddress(ipAddress);
        log.setCreateTime(LocalDateTime.now());
        operationLogMapper.insert(log);
    }
}

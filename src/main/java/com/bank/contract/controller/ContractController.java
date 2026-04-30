package com.bank.contract.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.bank.contract.entity.Contract;
import com.bank.contract.entity.Result;
import com.bank.contract.service.ContractService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * 合同管理控制器
 */
@RestController
@RequestMapping("/api/contracts")
@RequiredArgsConstructor
public class ContractController {

    private final ContractService contractService;

    /**
     * 分页查询合同列表
     */
    @GetMapping
    public Result<Page<Contract>> list(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) String contractNo,
            @RequestParam(required = false) String customerName,
            @RequestParam(required = false) String status) {

        Page<Contract> page = contractService.getContractList(pageNum, pageSize,
                                                             contractNo, customerName, status);
        return Result.success(page);
    }

    /**
     * 获取合同详情
     */
    @GetMapping("/{id}")
    public Result<Contract> getById(@PathVariable Long id) {
        Contract contract = contractService.getContractById(id);
        if (contract == null) {
            return Result.error(404, "合同不存在");
        }
        return Result.success(contract);
    }

    /**
     * 创建合同
     */
    @PostMapping
    public Result<Contract> create(@RequestBody Contract contract) {
        try {
            Contract result = contractService.createContract(contract);
            return Result.success("合同创建成功", result);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 更新合同
     */
    @PutMapping("/{id}")
    public Result<Contract> update(@PathVariable Long id, @RequestBody Contract contract) {
        contract.setId(id);
        try {
            Contract result = contractService.updateContract(contract);
            return Result.success("合同更新成功", result);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 审批合同
     */
    @PostMapping("/{id}/approve")
    public Result<Contract> approve(
            @PathVariable Long id,
            @RequestParam String approver,
            @RequestParam Integer level,
            @RequestParam String result,
            @RequestParam(required = false) String comment) {
        try {
            Contract contract = contractService.approve(id, approver, level, result, comment);
            return Result.success("审批完成", contract);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 归档合同
     */
    @PostMapping("/{id}/archive")
    public Result<Contract> archive(@PathVariable Long id) {
        try {
            Contract contract = contractService.archive(id);
            return Result.success("归档成功", contract);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 删除合同
     */
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        try {
            contractService.deleteContract(id);
            return Result.success("删除成功", null);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
}

# Bug 修复方案

## Bug 1: NPE 空指针异常

**位置**: `ContractService.java:136`

**问题代码**:
```java
if (contract.getStatus().equals("ARCHIVED")) {
```

**问题**: 如果 `contract` 为 null，调用 `getStatus()` 会抛出 NullPointerException

**修复方案**:
```java
if (contract == null) {
    throw new RuntimeException("合同不存在");
}
if ("ARCHIVED".equals(contract.getStatus())) {  // 使用常量在前，避免 NPE
    throw new RuntimeException("已归档的合同不能审批");
}
```

---

## Bug 2: 缺少参数校验

**位置**: `ContractService.java:88-104`

**问题**:
- 未校验必填字段（contractNo, contractName, amount, customerName, customerId）
- contractNo 可能重复
- amount 可能为负数

**修复方案**:

添加校验逻辑：
```java
@Transactional
public Contract createContract(Contract contract) {
    // 1. 校验必填字段
    if (contract.getContractNo() == null || contract.getContractNo().isEmpty()) {
        throw new IllegalArgumentException("合同编号不能为空");
    }
    if (contract.getContractName() == null || contract.getContractName().isEmpty()) {
        throw new IllegalArgumentException("合同名称不能为空");
    }
    if (contract.getAmount() == null || contract.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
        throw new IllegalArgumentException("合同金额必须大于0");
    }
    if (contract.getCustomerName() == null || contract.getCustomerName().isEmpty()) {
        throw new IllegalArgumentException("客户名称不能为空");
    }

    // 2. 校验合同编号唯一性
    QueryWrapper<Contract> checkWrapper = new QueryWrapper<>();
    checkWrapper.eq("contract_no", contract.getContractNo());
    if (contractMapper.selectCount(checkWrapper) > 0) {
        throw new IllegalArgumentException("合同编号已存在");
    }

    // ... 后续逻辑
}
```

---

## Bug 3: 审批并发安全问题

**位置**: `ContractService.java:132-163`

**问题**:
- 多个审批人同时审批同一合同，可能覆盖彼此的结果
- 没有乐观锁或悲观锁机制

**修复方案** (乐观锁方式):

1. 在 Contract 实体添加版本号字段：
```java
@TableField(exist = false)
private Integer version;
```

2. 在数据库添加 version 字段：
```sql
ALTER TABLE contracts ADD COLUMN version INT DEFAULT 0;
```

3. 修改审批方法：
```java
@Transactional
public Contract approve(Long contractId, String approver, Integer level, String result, String comment) {
    Contract contract = contractMapper.selectById(contractId);
    if (contract == null) {
        throw new RuntimeException("合同不存在");
    }

    // 乐观锁检查
    if ("ARCHIVED".equals(contract.getStatus())) {
        throw new RuntimeException("已归档的合同不能审批");
    }

    // 使用乐观锁更新
    Contract updateContract = new Contract();
    updateContract.setId(contractId);
    if ("PASS".equals(result)) {
        updateContract.setStatus("APPROVED");
    } else if ("REJECT".equals(result)) {
        updateContract.setStatus("REJECTED");
    }
    updateContract.setUpdateTime(LocalDateTime.now());

    // 乐观锁：版本号 + 1
    updateContract.setVersion(contract.getVersion() + 1);

    // WHERE version = 当前版本号
    UpdateWrapper<Contract> updateWrapper = new UpdateWrapper<>();
    updateWrapper.eq("id", contractId)
                 .eq("version", contract.getVersion());

    int rows = contractMapper.update(updateContract, updateWrapper);
    if (rows == 0) {
        throw new RuntimeException("并发冲突，请重试");
    }

    // 保存审批记录
    ApprovalRecord record = new ApprovalRecord();
    // ... 保存逻辑

    return contractMapper.selectById(contractId);
}
```

---

## Bug 4: 归档未校验状态

**位置**: `ContractService.java:170-183`

**问题**: 任何状态的合同都可以归档，未检查是否已审批通过

**修复方案**:
```java
@Transactional
public Contract archive(Long contractId) {
    Contract contract = contractMapper.selectById(contractId);
    if (contract == null) {
        throw new RuntimeException("合同不存在");
    }

    // 校验：只有已审批通过才能归档
    if (!"APPROVED".equals(contract.getStatus())) {
        throw new RuntimeException("只有已审批通过的合同才能归档");
    }

    contract.setStatus("ARCHIVED");
    contract.setUpdateTime(LocalDateTime.now());
    contractMapper.updateById(contract);

    saveOperationLog(contractId, "系统管理员", "ARCHIVE", "归档合同", "127.0.0.1");

    return contract;
}
```

---

## Bug 5: 审批接口参数问题

**位置**: `ContractController.java:78-91`

**问题**: 审批接口使用 `@RequestParam`，但前端 POST 请求通常用 JSON Body 传参，不方便

**修复方案**: 改为接收 JSON Body

```java
/**
 * 审批合同
 */
@PostMapping("/{id}/approve")
public Result<Contract> approve(
        @PathVariable Long id,
        @RequestBody ApproveRequest request) {
    try {
        Contract contract = contractService.approve(
            id,
            request.getApprover(),
            request.getLevel(),
            request.getResult(),
            request.getComment()
        );
        return Result.success("审批完成", contract);
    } catch (Exception e) {
        return Result.error(e.getMessage());
    }
}
```

添加请求 DTO：
```java
@Data
public class ApproveRequest {
    @NotBlank(message = "审批人不能为空")
    private String approver;

    @NotNull(message = "审批级别不能为空")
    private Integer level;

    @NotBlank(message = "审批结果不能为空")
    private String result;

    private String comment;
}
```

---

## Bug 6: N+1 查询性能问题

**位置**: `ContractService.java:57-62`

**问题**: 每个合同都单独查询审批记录，100 个合同需要 101 次查询

**修复方案**: 使用批量查询或 JOIN

```java
public Page<Contract> getContractList(Integer pageNum, Integer pageSize,
                                      String contractNo, String customerName, String status) {
    // 1. 查询合同列表
    Page<Contract> page = new Page<>(pageNum, pageSize);
    QueryWrapper<Contract> wrapper = new QueryWrapper<>();
    // ... 原有条件

    Page<Contract> result = contractMapper.selectPage(page, wrapper);

    if (result.getRecords().isEmpty()) {
        return result;
    }

    // 2. 批量查询所有合同的审批记录
    List<Long> contractIds = result.getRecords().stream()
            .map(Contract::getId)
            .collect(Collectors.toList());

    QueryWrapper<ApprovalRecord> approvalWrapper = new QueryWrapper<>();
    approvalWrapper.in("contract_id", contractIds);
    List<ApprovalRecord> allApprovals = approvalRecordMapper.selectList(approvalWrapper);

    // 3. 按合同ID分组，填充到对应合同
    Map<Long, List<ApprovalRecord>> approvalMap = allApprovals.stream()
            .collect(Collectors.groupingBy(ApprovalRecord::getContractId));

    for (Contract contract : result.getRecords()) {
        contract.setApprovalRecords(approvalMap.getOrDefault(contract.getId(), new ArrayList<>()));
    }

    return result;
}
```

---

## 修复优先级建议

| 优先级 | Bug | 原因 |
|--------|-----|------|
| P0 | Bug 1 (NPE) | 会导致服务崩溃 |
| P0 | Bug 2 (参数校验) | 数据完整性风险 |
| P1 | Bug 3 (并发) | 数据一致性风险 |
| P1 | Bug 4 (归档校验) | 业务流程漏洞 |
| P2 | Bug 5 (接口设计) | 使用不便 |
| P2 | Bug 6 (性能) | 影响体验 |

---

*文档版本: 1.0.0*
*生成时间: 2026-04-30*

# 🏦 银行合同管理系统

一个用于 **Claude 编程能力演示** 的 Spring Boot 项目。

---

## 📁 项目结构

```
contract-management/
├── pom.xml                          # Maven 配置
├── src/main/java/com/bank/contract/
│   ├── ContractManagementApplication.java   # 启动类
│   ├── entity/                      # 实体类
│   │   ├── Contract.java            # 合同实体
│   │   ├── ApprovalRecord.java      # 审批记录实体
│   │   ├── Attachment.java          # 附件实体
│   │   └── OperationLog.java        # 操作日志实体
│   ├── mapper/                      # MyBatis Mapper
│   ├── service/
│   │   └── ContractService.java     # ⚠️ 核心服务类（预留问题）
│   ├── controller/
│   │   ├── ContractController.java  # REST API
│   │   └── PageController.java      # 页面控制器
│   └── config/
│       └── WebConfig.java           # Web配置
└── src/main/resources/
    ├── application.yml              # 应用配置
    ├── schema.sql                   # 数据库表结构
    └── data.sql                     # 初始数据
```

---

## 🚀 快速启动

### 1. 编译运行

```bash
cd ~/IdeaProjects/contract-management
mvn spring-boot:run
```

### 2. 访问地址

| 服务 | 地址 |
|------|------|
| 首页 | http://localhost:8080/ |
| API接口 | http://localhost:8080/api/contracts |
| H2控制台 | http://localhost:8080/h2-console |

**H2 控制台登录信息：**
- JDBC URL: `jdbc:h2:mem:contractdb`
- 用户名: `sa`
- 密码: (空)

---

## 🎯 演示能力清单

### 一、🐛 Bug 修复演示

| 场景 | 文件位置 | 问题描述 | 演示提示词 |
|------|---------|---------|-----------|
| **空指针异常** | `ContractService.approve()` | contract可能为null | "这个方法有空指针风险，帮我修复" |
| **参数校验缺失** | `ContractService.createContract()` | 必填字段未校验 | "检查下新建合同的参数校验" |
| **并发安全问题** | `ContractService.archive()` | 无锁机制，多人同时操作会覆盖 | "这个归档方法有并发问题，帮我加锁" |
| **边界条件** | `ContractService.getContractById()` | 未处理id不存在的情况 | "帮我完善这个查询方法" |

### 二、⚡ 代码优化演示

| 场景 | 文件位置 | 问题描述 | 演示提示词 |
|------|---------|---------|-----------|
| **N+1查询** | `ContractService.getContractList()` | 循环查审批记录 | "优化这个查询方法，减少数据库查询次数" |
| **重复代码** | 多个Service方法 | 状态转换逻辑重复 | "帮我抽取重复的状态转换逻辑" |
| **SQL优化** | `ContractService` | 查询效率低 | "优化数据库查询性能" |
| **代码重构** | `ContractService.updateContract()` | 使用BeanUtils替代 | "优化这个更新方法" |

### 三、📄 文档生成演示

| 场景 | 文件位置 | 演示提示词 |
|------|---------|-----------|
| **接口文档** | `ContractController` | "为所有接口生成Swagger/OpenAPI文档注解" |
| **README优化** | 项目根目录 | "帮我完善这个项目的README" |
| **Javadoc注释** | `ContractService` | "为这个服务类补充完整的Javadoc注释" |

### 四、🧪 测试用例生成

| 场景 | 文件位置 | 演示提示词 |
|------|---------|-----------|
| **单元测试** | `ContractService` | "为createContract方法生成单元测试" |
| **边界测试** | `ContractService` | "帮我补充边界条件和异常场景的测试" |

### 五、🎨 前端优化演示

| 场景 | 文件位置 | 演示提示词 |
|------|---------|-----------|
| **加载状态** | `index.html` | "添加加载中的loading动画" |
| **表单验证** | `contract_form.html` | "增强表单验证和错误提示" |
| **响应式布局** | 所有页面 | "优化页面响应式设计" |
| **用户体验** | `contract_detail.html` | "改善审批交互体验" |

---

## 📝 预留的 Bug 和优化点详解

### Bug 1: 空指针异常风险

```java
// ContractService.java 第87行
public Contract approve(...) {
    Contract contract = contractMapper.selectById(contractId);
    // ⚠️ 没有判断 contract 是否为 null
    if (contract.getStatus().equals("ARCHIVED")) {  // 可能NPE
        ...
    }
}
```

**演示提示词：** "这个approve方法有空指针风险，帮我修复"

---

### Bug 2: 并发安全问题

```java
// ContractService.java 第109行
public Contract archive(Long contractId) {
    Contract contract = contractMapper.selectById(contractId);
    // ⚠️ 读取后没有加锁，直接更新
    contract.setStatus("ARCHIVED");
    contractMapper.updateById(contract);
}
```

**演示提示词：** "archive方法有并发问题，多人同时操作会覆盖数据，帮我加乐观锁"

---

### Bug 3: N+1 查询问题

```java
// ContractService.java 第28行
public Page<Contract> getContractList(...) {
    Page<Contract> result = contractMapper.selectPage(page, wrapper);

    // ⚠️ N+1: 对每个合同单独查询审批记录
    for (Contract contract : result.getRecords()) {
        List<ApprovalRecord> approvals = approvalRecordMapper.selectList(...);
        contract.setApprovalRecords(approvals);
    }
}
```

**演示提示词：** "优化这个查询方法，使用JOIN一次查出所有数据"

---

### Bug 4: 参数校验缺失

```java
// ContractService.java 第50行
public Contract createContract(Contract contract) {
    // ⚠️ 没有校验必填字段
    // 应该校验：contractNo, contractName, amount 等
    contractMapper.insert(contract);
}
```

**演示提示词：** "帮我完善新建合同的参数校验，使用@Valid注解"

---

## 💡 推荐的演示顺序

1. **5分钟** - 启动项目，展示基本功能
2. **10分钟** - 演示 Bug 修复（空指针、并发问题）
3. **10分钟** - 演示代码优化（N+1查询、重复代码）
4. **5分钟** - 演示文档生成（接口文档）
5. **5分钟** - 演示测试用例生成

---

## 🔧 技术栈

| 技术 | 版本 |
|------|------|
| Java | 17 |
| Spring Boot | 3.2.0 |
| MyBatis-Plus | 3.5.5 |
| H2 Database | (嵌入式) |
| Thymeleaf | (服务端渲染) |
| Lombok | (可选) |

---

## 📞 演示注意事项

1. **演示前先跑通流程**，确保项目能正常启动
2. **每个Bug只演示一次**，避免重复
3. **准备好转场话术**，从一个能力自然过渡到另一个
4. **可以边演示边提问**，展示 Claude 的理解和解释能力

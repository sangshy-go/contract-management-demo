# 银行合同管理系统 - 架构设计文档

## 1. 系统概述

### 1.1 项目简介

| 项目 | 说明 |
|------|------|
| 项目名称 | 银行合同管理系统 |
| 项目类型 | Spring Boot Web 应用 |
| 核心功能 | 合同的全生命周期管理（创建、审批、归档） |
| 适用场景 | 银行业务系统内部的合同管理模块 |

### 1.2 技术栈

| 层级 | 技术选型 | 版本 |
|------|----------|------|
| 框架 | Spring Boot | 2.7.18 |
| ORM | MyBatis-Plus | 3.5.3.1 |
| 数据库 | H2 (内存数据库) | - |
| 模板引擎 | Thymeleaf | - |
| 验证 | Spring Validation | - |
| 构建工具 | Maven | 3.x |
| Java 版本 | JDK 11 | - |

---

## 2. 系统架构

### 2.1 分层架构

```
┌─────────────────────────────────────────────┐
│              Controller 层                   │
│    (ContractController / PageController)    │
└────────────────────┬────────────────────────┘
                     │
┌────────────────────▼────────────────────────┐
│               Service 层                     │
│            (ContractService)                 │
└────────────────────┬────────────────────────┘
                     │
┌────────────────────▼────────────────────────┐
│              Mapper 层                       │
│    (ContractMapper / ApprovalRecordMapper)  │
└────────────────────┬────────────────────────┘
                     │
┌────────────────────▼────────────────────────┐
│              Entity 层                      │
│   (Contract / ApprovalRecord / OperationLog) │
└─────────────────────────────────────────────┘
```

### 2.2 目录结构

```
src/main/java/com/bank/contract/
├── ContractManagementApplication.java    # 启动类
├── config/
│   └── WebConfig.java                     # Web 配置
├── controller/
│   ├── ContractController.java           # 合同 REST API
│   └── PageController.java               # 页面控制器
├── entity/
│   ├── Contract.java                     # 合同实体
│   ├── ApprovalRecord.java               # 审批记录实体
│   ├── Attachment.java                  # 附件实体
│   ├── OperationLog.java                 # 操作日志实体
│   └── Result.java                       # 统一响应
├── mapper/
│   ├── ContractMapper.java
│   ├── ApprovalRecordMapper.java
│   ├── AttachmentMapper.java
│   └── OperationLogMapper.java
└── service/
    └── ContractService.java              # 合同业务逻辑

src/main/resources/
├── application.yml                       # 应用配置
├── schema.sql                            # 建表 SQL
├── data.sql                              # 初始数据
└── templates/                            # Thymeleaf 模板
    ├── index.html
    ├── contract_form.html
    ├── contract_detail.html
    └── layout.html
```

---

## 3. 数据模型

### 3.1 核心实体

#### 3.1.1 合同 (Contract)

| 字段 | 类型 | 说明 | 必填 |
|------|------|------|------|
| id | Long | 主键 | 是 |
| contractNo | String | 合同编号 | 是 |
| contractName | String | 合同名称 | 是 |
| contractType | String | 合同类型 | 是 |
| amount | BigDecimal | 合同金额 | 是 |
| customerName | String | 客户名称 | 是 |
| customerId | String | 客户证件号 | 是 |
| signDate | LocalDate | 签署日期 | 否 |
| startDate | LocalDate | 生效日期 | 否 |
| endDate | LocalDate | 到期日期 | 否 |
| status | String | 状态 | 是 |
| riskLevel | String | 风险等级 | 否 |
| department | String | 所属部门 | 否 |
| manager | String | 客户经理 | 否 |
| remark | String | 备注 | 否 |
| createTime | LocalDateTime | 创建时间 | 是 |
| updateTime | LocalDateTime | 更新时间 | 是 |

**枚举值说明**：
- `contractType`: LOAN(贷款) / CREDIT(授信) / SUPPLY_CHAIN(供应链) / PROJECT(项目)
- `status`: DRAFT(草稿) / PENDING(待审批) / APPROVED(已审批) / REJECTED(已拒绝) / ARCHIVED(已归档)
- `riskLevel`: LOW(低) / MEDIUM(中) / HIGH(高)

#### 3.1.2 审批记录 (ApprovalRecord)

| 字段 | 类型 | 说明 |
|------|------|------|
| id | Long | 主键 |
| contractId | Long | 合同ID |
| approver | String | 审批人 |
| approveLevel | Integer | 审批级别 |
| approveResult | String | 审批结果 (PASS/REJECT) |
| approveComment | String | 审批意见 |
| approveTime | LocalDateTime | 审批时间 |

#### 3.1.3 操作日志 (OperationLog)

| 字段 | 类型 | 说明 |
|------|------|------|
| id | Long | 主键 |
| contractId | Long | 合同ID |
| operator | String | 操作人 |
| operation | String | 操作类型 |
| detail | String | 操作详情 |
| ipAddress | String | IP地址 |
| createTime | LocalDateTime | 创建时间 |

#### 3.1.4 附件 (Attachment)

| 字段 | 类型 | 说明 |
|------|------|------|
| id | Long | 主键 |
| contractId | Long | 合同ID |
| fileName | String | 文件名 |
| filePath | String | 文件路径 |
| fileSize | Long | 文件大小 |
| fileType | String | 文件类型 |
| uploadTime | LocalDateTime | 上传时间 |

---

## 4. 模块设计

### 4.1 Controller 层

**ContractController** - 合同 REST API

| 模块 | 功能 |
|------|------|
| 列表查询 | 分页查询合同，支持合同编号、客户名称、状态筛选 |
| 详情查询 | 根据 ID 获取合同详情（含审批记录） |
| 创建 | 创建新合同，状态默认为 DRAFT |
| 更新 | 更新合同信息 |
| 审批 | 提交审批，更新合同状态 |
| 归档 | 归档已审批通过的合同 |
| 删除 | 删除合同（同时删除关联的审批记录） |

### 4.2 Service 层

**ContractService** - 合同业务逻辑

| 方法 | 职责 | 事务 |
|------|------|------|
| getContractList | 分页查询 + N+1 查询审批记录 | 否 |
| getContractById | 单条查询 + 关联审批记录 | 否 |
| createContract | 创建合同 + 记录日志 | 是 |
| updateContract | 更新合同 + 记录日志 | 是 |
| approve | 审批操作 + 更新状态 + 记录日志 | 是 |
| archive | 归档操作 + 记录日志 | 是 |
| deleteContract | 删除合同 + 删除关联 + 记录日志 | 是 |

### 4.3 Mapper 层

基于 MyBatis-Plus，使用通用 Mapper 继承 `BaseMapper<T>`：

- `ContractMapper` - 合同 CRUD
- `ApprovalRecordMapper` - 审批记录 CRUD
- `OperationLogMapper` - 操作日志 CRUD
- `AttachmentMapper` - 附件 CRUD

---

## 5. 接口设计

### 5.1 统一响应格式

```json
{
  "code": 200,
  "message": "操作成功",
  "data": {}
}
```

| 字段 | 类型 | 说明 |
|------|------|------|
| code | Integer | 状态码 (200=成功, 404=未找到, 500=错误) |
| message | String | 提示信息 |
| data | Object | 响应数据 |

### 5.2 REST API 列表

| 方法 | 路径 | 功能 |
|------|------|------|
| GET | /api/contracts | 分页查询合同列表 |
| GET | /api/contracts/{id} | 获取合同详情 |
| POST | /api/contracts | 创建合同 |
| PUT | /api/contracts/{id} | 更新合同 |
| POST | /api/contracts/{id}/approve | 审批合同 |
| POST | /api/contracts/{id}/archive | 归档合同 |
| DELETE | /api/contracts/{id} | 删除合同 |

---

## 6. 配置文件

### 6.1 application.yml

```yaml
server:
  port: 8080

spring:
  datasource:
    url: jdbc:h2:mem:contractdb
    driver-class-name: org.h2.Driver
    username: sa
    password:
  h2:
    console:
      enabled: true
      path: /h2-console
  sql:
    init:
      mode: always
      schema-locations: classpath:schema.sql
      data-locations: classpath:data.sql
  thymeleaf:
    cache: false

mybatis-plus:
  configuration:
    map-underscore-to-camel-case: true
    log-impl: org.apache.ibatis.logging.stdout.StdOutImpl
```

---

## 7. 已知问题和优化点

| 序号 | 问题描述 | 风险等级 | 优化建议 |
|------|----------|----------|----------|
| 1 | getContractList 存在 N+1 查询问题 | 中 | 使用 JOIN 或批量查询 |
| 2 | createContract 缺少参数校验 | 高 | 添加 @Valid 注解和校验规则 |
| 3 | approve 方法存在并发安全问题 | 高 | 添加乐观锁或悲观锁 |
| 4 | 归档操作未校验合同状态 | 中 | 归档前检查是否已审批通过 |
| 5 | 缺少登录鉴权 | 高 | 接入 Spring Security 或 JWT |

---

## 8. 部署说明

### 8.1 启动方式

```bash
# Maven 启动
mvn spring-boot:run

# 打包后启动
mvn clean package
java -jar target/contract-management-1.0.0.jar
```

### 8.2 访问地址

| 服务 | 地址 |
|------|------|
| Web 应用 | http://localhost:8080 |
| H2 Console | http://localhost:8080/h2-console |

---

*文档版本: 1.0.0*
*生成时间: 2026-04-30*

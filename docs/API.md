# 银行合同管理系统 - 接口说明文档

## 1. 接口概览

### 1.1 Base URL

```
http://localhost:8080/api/contracts
```

### 1.2 统一响应格式

```json
{
  "code": 200,
  "message": "操作成功",
  "data": {}
}
```

**响应字段说明**：

| 字段 | 类型 | 必填 | 说明 |
|------|------|------|------|
| code | Integer | 是 | 状态码 |
| message | String | 是 | 提示信息 |
| data | Object | 否 | 响应数据 |

**状态码说明**：

| code | 说明 |
|------|------|
| 200 | 成功 |
| 404 | 资源不存在 |
| 500 | 服务器错误 |

---

## 2. 合同管理接口

### 2.1 分页查询合同列表

**接口地址**: `GET /api/contracts`

**请求参数**：

| 参数 | 类型 | 必填 | 默认值 | 说明 |
|------|------|------|--------|------|
| pageNum | Integer | 否 | 1 | 页码，从 1 开始 |
| pageSize | Integer | 否 | 10 | 每页数量 |
| contractNo | String | 否 | - | 合同编号（精确匹配） |
| customerName | String | 否 | - | 客户名称（模糊匹配） |
| status | String | 否 | - | 合同状态 |

**请求示例**：

```bash
GET /api/contracts?pageNum=1&pageSize=10&customerName=张&status=DRAFT
```

**响应示例**：

```json
{
  "code": 200,
  "message": "操作成功",
  "data": {
    "records": [
      {
        "id": 1,
        "contractNo": "CONTRACT-2026-001",
        "contractName": "贷款合同",
        "contractType": "LOAN",
        "amount": 100000.00,
        "customerName": "张三",
        "customerId": "110101199001011234",
        "signDate": "2026-01-15",
        "startDate": "2026-01-15",
        "endDate": "2027-01-14",
        "status": "DRAFT",
        "riskLevel": "MEDIUM",
        "department": "信贷部",
        "manager": "李经理",
        "remark": "测试合同",
        "createTime": "2026-04-30T10:00:00",
        "updateTime": "2026-04-30T10:00:00",
        "approvalRecords": []
      }
    ],
    "total": 100,
    "size": 10,
    "current": 1,
    "pages": 10
  }
}
```

---

### 2.2 获取合同详情

**接口地址**: `GET /api/contracts/{id}`

**路径参数**：

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| id | Long | 是 | 合同 ID |

**请求示例**：

```bash
GET /api/contracts/1
```

**响应示例**：

```json
{
  "code": 200,
  "message": "操作成功",
  "data": {
    "id": 1,
    "contractNo": "CONTRACT-2026-001",
    "contractName": "贷款合同",
    "contractType": "LOAN",
    "amount": 100000.00,
    "customerName": "张三",
    "customerId": "110101199001011234",
    "signDate": "2026-01-15",
    "startDate": "2026-01-15",
    "endDate": "2027-01-14",
    "status": "DRAFT",
    "riskLevel": "MEDIUM",
    "department": "信贷部",
    "manager": "李经理",
    "remark": "测试合同",
    "createTime": "2026-04-30T10:00:00",
    "updateTime": "2026-04-30T10:00:00",
    "approvalRecords": [
      {
        "id": 1,
        "contractId": 1,
        "approver": "审批人A",
        "approveLevel": 1,
        "approveResult": "PASS",
        "approveComment": "同意",
        "approveTime": "2026-04-30T11:00:00"
      }
    ]
  }
}
```

**错误响应**（合同不存在）：

```json
{
  "code": 404,
  "message": "合同不存在",
  "data": null
}
```

---

### 2.3 创建合同

**接口地址**: `POST /api/contracts`

**请求体**：

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| contractNo | String | 是 | 合同编号 |
| contractName | String | 是 | 合同名称 |
| contractType | String | 是 | 合同类型 |
| amount | BigDecimal | 是 | 合同金额 |
| customerName | String | 是 | 客户名称 |
| customerId | String | 是 | 客户证件号 |
| signDate | LocalDate | 否 | 签署日期 |
| startDate | LocalDate | 否 | 生效日期 |
| endDate | LocalDate | 否 | 到期日期 |
| riskLevel | String | 否 | 风险等级 |
| department | String | 否 | 所属部门 |
| manager | String | 否 | 客户经理 |
| remark | String | 否 | 备注 |

**contractType 枚举**：
- `LOAN` - 贷款
- `CREDIT` - 授信
- `SUPPLY_CHAIN` - 供应链
- `PROJECT` - 项目

**riskLevel 枚举**：
- `LOW` - 低
- `MEDIUM` - 中
- `HIGH` - 高

**请求示例**：

```bash
POST /api/contracts
Content-Type: application/json

{
  "contractNo": "CONTRACT-2026-002",
  "contractName": "授信合同",
  "contractType": "CREDIT",
  "amount": 500000.00,
  "customerName": "李四",
  "customerId": "110101199002025678",
  "signDate": "2026-02-01",
  "startDate": "2026-02-01",
  "endDate": "2027-01-31",
  "riskLevel": "HIGH",
  "department": "信贷部",
  "manager": "王经理",
  "remark": "大额授信"
}
```

**响应示例**：

```json
{
  "code": 200,
  "message": "合同创建成功",
  "data": {
    "id": 2,
    "contractNo": "CONTRACT-2026-002",
    "contractName": "授信合同",
    "contractType": "CREDIT",
    "amount": 500000.00,
    "customerName": "李四",
    "customerId": "110101199002025678",
    "signDate": "2026-02-01",
    "startDate": "2026-02-01",
    "endDate": "2027-01-31",
    "status": "DRAFT",
    "riskLevel": "HIGH",
    "department": "信贷部",
    "manager": "王经理",
    "remark": "大额授信",
    "createTime": "2026-04-30T12:00:00",
    "updateTime": "2026-04-30T12:00:00"
  }
}
```

---

### 2.4 更新合同

**接口地址**: `PUT /api/contracts/{id}`

**路径参数**：

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| id | Long | 是 | 合同 ID |

**请求体**（所有字段均可更新）：

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| contractNo | String | 否 | 合同编号 |
| contractName | String | 否 | 合同名称 |
| contractType | String | 否 | 合同类型 |
| amount | BigDecimal | 否 | 合同金额 |
| customerName | String | 否 | 客户名称 |
| customerId | String | 否 | 客户证件号 |
| signDate | LocalDate | 否 | 签署日期 |
| startDate | LocalDate | 否 | 生效日期 |
| endDate | LocalDate | 否 | 到期日期 |
| riskLevel | String | 否 | 风险等级 |
| department | String | 否 | 所属部门 |
| manager | String | 否 | 客户经理 |
| remark | String | 否 | 备注 |

**请求示例**：

```bash
PUT /api/contracts/2
Content-Type: application/json

{
  "contractName": "授信合同（修订版）",
  "amount": 600000.00,
  "remark": "额度调整"
}
```

**响应示例**：

```json
{
  "code": 200,
  "message": "合同更新成功",
  "data": {
    "id": 2,
    "contractNo": "CONTRACT-2026-002",
    "contractName": "授信合同（修订版）",
    "amount": 600000.00,
    "status": "DRAFT",
    "updateTime": "2026-04-30T12:30:00"
  }
}
```

---

### 2.5 审批合同

**接口地址**: `POST /api/contracts/{id}/approve`

**路径参数**：

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| id | Long | 是 | 合同 ID |

**请求参数**：

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| approver | String | 是 | 审批人 |
| level | Integer | 是 | 审批级别 |
| result | String | 是 | 审批结果 |
| comment | String | 否 | 审批意见 |

**result 枚举**：
- `PASS` - 通过
- `REJECT` - 拒绝

**请求示例**：

```bash
POST /api/contracts/2/approve?approver=审批经理A&level=1&result=PASS&comment=符合授信条件
```

**响应示例**：

```json
{
  "code": 200,
  "message": "审批完成",
  "data": {
    "id": 2,
    "contractNo": "CONTRACT-2026-002",
    "contractName": "授信合同（修订版）",
    "status": "APPROVED",
    "updateTime": "2026-04-30T13:00:00"
  }
}
```

**错误响应**（已归档的合同不能审批）：

```json
{
  "code": 500,
  "message": "已归档的合同不能审批",
  "data": null
}
```

---

### 2.6 归档合同

**接口地址**: `POST /api/contracts/{id}/archive`

**路径参数**：

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| id | Long | 是 | 合同 ID |

**请求示例**：

```bash
POST /api/contracts/2/archive
```

**响应示例**：

```json
{
  "code": 200,
  "message": "归档成功",
  "data": {
    "id": 2,
    "contractNo": "CONTRACT-2026-002",
    "contractName": "授信合同（修订版）",
    "status": "ARCHIVED",
    "updateTime": "2026-04-30T14:00:00"
  }
}
```

---

### 2.7 删除合同

**接口地址**: `DELETE /api/contracts/{id}`

**路径参数**：

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| id | Long | 是 | 合同 ID |

**请求示例**：

```bash
DELETE /api/contracts/2
```

**响应示例**：

```json
{
  "code": 200,
  "message": "删除成功",
  "data": null
}
```

---

## 3. 状态流转

```
DRAFT (草稿)
    │
    ├───[审批]──▶ APPROVED (已审批)
    │                │
    │                └──[归档]──▶ ARCHIVED (已归档)
    │
    └───[审批]──▶ REJECTED (已拒绝)
```

---

## 4. 错误码汇总

| code | message | 说明 |
|------|---------|------|
| 200 | 操作成功 | 请求成功 |
| 404 | 合同不存在 | 资源未找到 |
| 500 | 已归档的合同不能审批 | 业务逻辑错误 |
| 500 | 合同不存在 | 更新/删除时合同不存在 |
| 500 | * | 其他服务器错误 |

---

*文档版本: 1.0.0*
*生成时间: 2026-04-30*

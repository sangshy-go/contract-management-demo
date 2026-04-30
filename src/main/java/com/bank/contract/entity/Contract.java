package com.bank.contract.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 合同实体类
 * ⚠️ 预留问题：部分字段可优化，如金额格式化、状态转换等
 */
@Data
@TableName("contracts")
public class Contract {

    @TableId(type = IdType.AUTO)
    private Long id;

    /** 合同编号 */
    private String contractNo;

    /** 合同名称 */
    private String contractName;

    /** 合同类型：LOAN-贷款、CREDIT-授信、SUPPLY_CHAIN-供应链、PROJECT-项目 */
    private String contractType;

    /** 合同金额 */
    private BigDecimal amount;

    /** 客户名称 */
    private String customerName;

    /** 客户证件号 */
    private String customerId;

    /** 签署日期 */
    private LocalDate signDate;

    /** 生效日期 */
    private LocalDate startDate;

    /** 到期日期 */
    private LocalDate endDate;

    /** 状态：DRAFT-草稿、PENDING-待审批、APPROVED-已审批、REJECTED-已拒绝、ARCHIVED-已归档 */
    private String status;

    /** 风险等级：LOW-低、MEDIUM-中、HIGH-高 */
    private String riskLevel;

    /** 所属部门 */
    private String department;

    /** 客户经理 */
    private String manager;

    /** 备注 */
    private String remark;

    /** 创建时间 */
    private LocalDateTime createTime;

    /** 更新时间 */
    private LocalDateTime updateTime;

    /** 审批记录（非数据库字段） */
    @TableField(exist = false)
    private List<ApprovalRecord> approvalRecords;

    /** 附件列表（非数据库字段） */
    @TableField(exist = false)
    private List<Attachment> attachments;
}

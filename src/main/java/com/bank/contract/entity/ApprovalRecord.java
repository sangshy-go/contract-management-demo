package com.bank.contract.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.time.LocalDateTime;

/**
 * 审批记录实体类
 */
@Data
@TableName("approval_records")
public class ApprovalRecord {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long contractId;

    /** 审批人 */
    private String approver;

    /** 审批级别 */
    private Integer approveLevel;

    /** 审批结果：PASS-通过、REJECT-拒绝 */
    private String approveResult;

    /** 审批意见 */
    private String approveComment;

    /** 审批时间 */
    private LocalDateTime approveTime;
}

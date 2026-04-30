package com.bank.contract.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.time.LocalDateTime;

/**
 * 操作日志实体类
 */
@Data
@TableName("operation_logs")
public class OperationLog {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long contractId;

    /** 操作人 */
    private String operator;

    /** 操作类型 */
    private String operation;

    /** 操作详情 */
    private String detail;

    /** IP地址 */
    private String ipAddress;

    /** 创建时间 */
    private LocalDateTime createTime;
}

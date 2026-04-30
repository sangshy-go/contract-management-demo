package com.bank.contract.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.bank.contract.entity.ApprovalRecord;
import org.apache.ibatis.annotations.Mapper;

/**
 * 审批记录Mapper
 */
@Mapper
public interface ApprovalRecordMapper extends BaseMapper<ApprovalRecord> {
}

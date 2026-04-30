package com.bank.contract.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.bank.contract.entity.OperationLog;
import org.apache.ibatis.annotations.Mapper;

/**
 * 操作日志Mapper
 */
@Mapper
public interface OperationLogMapper extends BaseMapper<OperationLog> {
}

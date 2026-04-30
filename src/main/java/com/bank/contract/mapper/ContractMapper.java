package com.bank.contract.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.bank.contract.entity.Contract;
import org.apache.ibatis.annotations.Mapper;

/**
 * 合同Mapper
 */
@Mapper
public interface ContractMapper extends BaseMapper<Contract> {
}

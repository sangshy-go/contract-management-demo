package com.bank.contract.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.bank.contract.entity.Attachment;
import org.apache.ibatis.annotations.Mapper;

/**
 * 附件Mapper
 */
@Mapper
public interface AttachmentMapper extends BaseMapper<Attachment> {
}

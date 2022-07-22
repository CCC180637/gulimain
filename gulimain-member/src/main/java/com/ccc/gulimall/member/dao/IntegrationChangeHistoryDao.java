package com.ccc.gulimall.member.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ccc.gulimall.member.entity.IntegrationChangeHistoryEntity;
import org.apache.ibatis.annotations.Mapper;

/**
 * 积分变化历史记录
 * 
 * @author ccc
 * @email ccc@gmail.com
 * @date 2022-06-05 20:42:16
 */
@Mapper
public interface IntegrationChangeHistoryDao extends BaseMapper<IntegrationChangeHistoryEntity> {
	
}

package com.ccc.gulimall.member.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ccc.gulimall.member.entity.GrowthChangeHistoryEntity;
import org.apache.ibatis.annotations.Mapper;

/**
 * 成长值变化历史记录
 * 
 * @author ccc
 * @email ccc@gmail.com
 * @date 2022-06-05 20:42:16
 */
@Mapper
public interface GrowthChangeHistoryDao extends BaseMapper<GrowthChangeHistoryEntity> {
	
}

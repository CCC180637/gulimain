package com.ccc.gulimall.member.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ccc.gulimall.member.entity.MemberEntity;
import com.ccc.gulimall.member.entity.MemberLevelEntity;
import org.apache.ibatis.annotations.Mapper;

/**
 * 会员
 * 
 * @author ccc
 * @email ccc@gmail.com
 * @date 2022-06-05 20:42:16
 */
@Mapper
public interface MemberDao extends BaseMapper<MemberEntity> {


}

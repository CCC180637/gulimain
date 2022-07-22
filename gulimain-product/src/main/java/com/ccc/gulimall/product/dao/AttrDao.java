package com.ccc.gulimall.product.dao;

import com.ccc.gulimall.product.entity.AttrEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * ??Ʒ?
 * 
 * @author ccc
 * @email ccc@gmail.com
 * @date 2022-06-08 15:50:32
 */
@Mapper
public interface AttrDao extends BaseMapper<AttrEntity> {

    List<Long> selectSerachAttrIds(@Param("attrIds") List<Long> attrIds);
}

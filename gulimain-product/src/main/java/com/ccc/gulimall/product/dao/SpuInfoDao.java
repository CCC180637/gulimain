package com.ccc.gulimall.product.dao;

import com.ccc.gulimall.product.entity.SpuInfoEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * spu??Ϣ
 * 
 * @author ccc
 * @email ccc@gmail.com
 * @date 2022-06-08 15:50:32
 */
@Mapper
public interface SpuInfoDao extends BaseMapper<SpuInfoEntity> {

    void updataStatus(@Param("spuId") Long spuId, @Param("code") int code);
}

package com.ccc.gulimall.ware.dao;

import com.ccc.gulimall.ware.entity.WareSkuEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 商品库存
 * 
 * @author ccc
 * @email ccc@gmail.com
 * @date 2022-06-06 11:07:56
 */
@Mapper
public interface WareSkuDao extends BaseMapper<WareSkuEntity> {

     void addStock(@Param("skuId") Long skuId, @Param("wareId")Long wareId, @Param("skuNum")Integer skuNum);

    Long getSkuStock(@Param("skuId") Long skuId);
}

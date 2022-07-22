package com.ccc.gulimall.order.dao;

import com.ccc.gulimall.order.entity.OrderEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 订单
 * 
 * @author ccc
 * @email ccc@gmail.com
 * @date 2022-06-06 10:58:30
 */
@Mapper
public interface OrderDao extends BaseMapper<OrderEntity> {
	
}

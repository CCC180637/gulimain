package com.ccc.gulimall.order.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.ccc.common.utils.PageUtils;
import com.ccc.gulimall.order.entity.MqMessageEntity;

import java.util.Map;

/**
 * 
 *
 * @author ccc
 * @email ccc@gmail.com
 * @date 2022-06-06 10:58:30
 */
public interface MqMessageService extends IService<MqMessageEntity> {

    PageUtils queryPage(Map<String, Object> params);
}


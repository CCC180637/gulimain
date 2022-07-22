package com.ccc.gulimall.coupon.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.ccc.common.to.SkuReductionTo;
import com.ccc.common.utils.PageUtils;
import com.ccc.common.utils.R;
import com.ccc.gulimall.coupon.entity.SkuFullReductionEntity;

import java.util.Map;

/**
 * ??Ʒ??????Ϣ
 *
 * @author ccc
 * @email ccc@gmail.com
 * @date 2022-06-07 16:38:36
 */
public interface SkuFullReductionService extends IService<SkuFullReductionEntity> {

    PageUtils queryPage(Map<String, Object> params);

    void  saveSkuReduction(SkuReductionTo skuReductionTo);
}


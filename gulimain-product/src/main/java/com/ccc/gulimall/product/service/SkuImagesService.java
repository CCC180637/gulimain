package com.ccc.gulimall.product.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.ccc.common.utils.PageUtils;
import com.ccc.gulimall.product.entity.SkuImagesEntity;
import com.ccc.gulimall.product.vo.SkuItemVo;

import java.util.List;
import java.util.Map;

/**
 * skuͼƬ
 *
 * @author ccc
 * @email ccc@gmail.com
 * @date 2022-06-08 15:50:32
 */
public interface SkuImagesService extends IService<SkuImagesEntity> {

    PageUtils queryPage(Map<String, Object> params);

    List<SkuImagesEntity> getIamgesBySkuId(Long skuId);
}


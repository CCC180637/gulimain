package com.ccc.gulimall.product.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.ccc.common.utils.PageUtils;
import com.ccc.gulimall.product.entity.AttrGroupEntity;
import com.ccc.gulimall.product.vo.AttrGroupWithAttrsVo;
import com.ccc.gulimall.product.vo.SkuItemVo;
import com.ccc.gulimall.product.vo.SpuItemAttrGroupVo;

import java.util.List;
import java.util.Map;

/**
 * ???Է??
 *
 * @author ccc
 * @email ccc@gmail.com
 * @date 2022-06-08 15:50:32
 */
public interface AttrGroupService extends IService<AttrGroupEntity> {

    PageUtils queryPage(Map<String, Object> params);

    PageUtils queryPage(Map<String, Object> params, Long categoryId);

    List<AttrGroupWithAttrsVo> getAttrGroupWithByCatelogId(Long catelogId);

    List<SpuItemAttrGroupVo> getAttrGroupWithBySpuId(Long spuId, Long catalogId);
}


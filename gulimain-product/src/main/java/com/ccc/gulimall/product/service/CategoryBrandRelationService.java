package com.ccc.gulimall.product.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.ccc.common.utils.PageUtils;
import com.ccc.gulimall.product.entity.BrandEntity;
import com.ccc.gulimall.product.entity.CategoryBrandRelationEntity;
import com.ccc.gulimall.product.vo.BrandVo;

import java.util.List;
import java.util.Map;

/**
 * Ʒ?Ʒ???????
 *
 * @author ccc
 * @email ccc@gmail.com
 * @date 2022-06-08 15:50:32
 */
public interface CategoryBrandRelationService extends IService<CategoryBrandRelationEntity> {

    PageUtils queryPage(Map<String, Object> params);

    void saveDetail(CategoryBrandRelationEntity categoryBrandRelation);

    void updateBrand(Long brandId, String name);

    void updateDetail(Long catId, String name);

    List<BrandEntity> getBrandsByCatId(Long catId);
}


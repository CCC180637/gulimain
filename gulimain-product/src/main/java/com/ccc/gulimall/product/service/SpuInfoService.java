package com.ccc.gulimall.product.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.ccc.common.utils.PageUtils;
import com.ccc.gulimall.product.entity.SpuInfoEntity;
import com.ccc.gulimall.product.vo.SpuSaveVo;

import java.util.Map;

/**
 * spu??Ϣ
 *
 * @author ccc
 * @email ccc@gmail.com
 * @date 2022-06-08 15:50:32
 */
public interface SpuInfoService extends IService<SpuInfoEntity> {

    PageUtils queryPage(Map<String, Object> params);


    void saveSpuInfo(SpuSaveVo spuSaveVo);

    void saveBaseSpuInfo(SpuInfoEntity infoEntity);

    PageUtils queryPageCondition(Map<String, Object> params);


    //商品上架
    void up(Long spuId);
}


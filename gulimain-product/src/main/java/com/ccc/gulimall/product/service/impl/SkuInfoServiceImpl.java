package com.ccc.gulimall.product.service.impl;

import com.ccc.gulimall.product.entity.*;
import com.ccc.gulimall.product.service.*;
import com.ccc.gulimall.product.vo.SkuItemSaleAttrVo;
import com.ccc.gulimall.product.vo.SkuItemVo;
import com.ccc.gulimall.product.vo.SpuItemAttrGroupVo;
import com.ccc.gulimall.product.vo.SpuSaveVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ccc.common.utils.PageUtils;
import com.ccc.common.utils.Query;

import com.ccc.gulimall.product.dao.SkuInfoDao;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;


@Service("skuInfoService")
public class SkuInfoServiceImpl extends ServiceImpl<SkuInfoDao, SkuInfoEntity> implements SkuInfoService {

    @Autowired
    SkuImagesService imagesService;

    @Autowired
    SpuInfoDescService spuInfoDescService;

    @Autowired
    AttrGroupService attrGroupService;

    @Autowired
    SkuSaleAttrValueService skuSaleAttrValueService;



    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<SkuInfoEntity> page = this.page(
                new Query<SkuInfoEntity>().getPage(params),
                new QueryWrapper<SkuInfoEntity>()
        );

        return new PageUtils(page);

    }

    @Override
    public void saveSpuInfo(SkuInfoEntity skuInfoEntity) {
        this.baseMapper.insert(skuInfoEntity);
    }

    @Override
    public PageUtils queryPageCondition(Map<String, Object> params) {
        QueryWrapper<SkuInfoEntity> queryWrapper = new QueryWrapper<>();
        /**
         * key:
         * catelogId: 0
         * brandId: 0
         * min: 0
         * max: 0
         */
        String key = (String) params.get("key");
        if (!StringUtils.isEmpty(key)){
            queryWrapper.and((wapper)->{
                wapper.eq("sku_id",key).or().like("sku_name",key);
            });
        }
        String catelogId = (String) params.get("catelogId");
        if (!StringUtils.isEmpty(catelogId) && !"0".equalsIgnoreCase(catelogId)){
            queryWrapper.eq("catalog_id",catelogId);
        }
        String brandId = (String) params.get("brandId");
        if (!StringUtils.isEmpty(brandId)&& !"0".equalsIgnoreCase(brandId)){
            queryWrapper.eq("brand_id",brandId);
        }
        String min = (String) params.get("min");
        if (!StringUtils.isEmpty(min)){
            queryWrapper.ge("price",min);
        }
        String max = (String) params.get("max");
        if (!StringUtils.isEmpty(max) ){

            try {
                BigDecimal bigDecimal = new BigDecimal(max);
                if (bigDecimal.compareTo(new BigDecimal("0"))==1){
                    queryWrapper.le("price",max);
                }
            }catch (Exception e){
                e.printStackTrace();
            }

        }

        IPage<SkuInfoEntity> page = this.page(new Query<SkuInfoEntity>().getPage(params), queryWrapper);

        return new PageUtils(page);
    }

    @Override
    public List<SkuInfoEntity> getSkuBySpuId(Long spuId) {
        List<SkuInfoEntity> entities = this.list(new QueryWrapper<SkuInfoEntity>().eq("spu_id", spuId));
        return entities;
    }


    @Override
    public SkuItemVo item(Long skuId) {
        SkuItemVo skuItemVo = new SkuItemVo();

        //1、sku基本信息获取 ，pms_sku_info
        SkuInfoEntity info = getById(skuId);
        skuItemVo.setInfo(info);
        Long catalogId = info.getCatalogId();
        Long spuId =info.getSpuId();
        //2、sku的基本图片信息 ， pms_sku_images
       List<SkuImagesEntity> imagesEntities= imagesService.getIamgesBySkuId(skuId);
        skuItemVo.setImages(imagesEntities);

        //3、获取spu的销售属性组合
      List<SkuItemSaleAttrVo> saleAttrVos= skuSaleAttrValueService.getSaleAttrsBySpuId(spuId);
      skuItemVo.setSaleAttr(saleAttrVos);


        //4、获取spu的介绍   pms_spu_info_desc

        SpuInfoDescEntity spuInfoDescEntity = spuInfoDescService.getById(spuId);
        skuItemVo.setDesp(spuInfoDescEntity);

        //5、获取spu的规格参数信息
     List<SpuItemAttrGroupVo> groupVoList=attrGroupService.getAttrGroupWithBySpuId(spuId,catalogId);
     skuItemVo.setGroupAttrs(groupVoList);

        return skuItemVo;
    }
}
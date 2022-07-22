package com.ccc.gulimall.coupon.service.impl;

import com.alibaba.nacos.api.config.filter.IFilterConfig;
import com.ccc.common.to.MemberPrice;
import com.ccc.common.to.SkuReductionTo;
import com.ccc.common.utils.R;
import com.ccc.gulimall.coupon.entity.MemberPriceEntity;
import com.ccc.gulimall.coupon.entity.SkuLadderEntity;
import com.ccc.gulimall.coupon.service.MemberPriceService;
import com.ccc.gulimall.coupon.service.SkuLadderService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ccc.common.utils.PageUtils;
import com.ccc.common.utils.Query;

import com.ccc.gulimall.coupon.dao.SkuFullReductionDao;
import com.ccc.gulimall.coupon.entity.SkuFullReductionEntity;
import com.ccc.gulimall.coupon.service.SkuFullReductionService;


@Service("skuFullReductionService")
public class SkuFullReductionServiceImpl extends ServiceImpl<SkuFullReductionDao, SkuFullReductionEntity> implements SkuFullReductionService {

    @Autowired
    SkuLadderService skuLadderService;

    @Autowired
    MemberPriceService memberPriceService;
    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<SkuFullReductionEntity> page = this.page(
                new Query<SkuFullReductionEntity>().getPage(params),
                new QueryWrapper<SkuFullReductionEntity>()
        );

        return new PageUtils(page);
    }

    @Override
    public void saveSkuReduction(SkuReductionTo skuReductionTo) {
        //1、sku的优惠、满减信息等
        //sms_sku_ladder
        SkuLadderEntity skuLadderEntity = new SkuLadderEntity();
        skuLadderEntity.setSkuId(skuReductionTo.getSkuId());
        skuLadderEntity.setDiscount(skuReductionTo.getDiscount());
        skuLadderEntity.setFullCount(skuReductionTo.getFullCount());
        skuLadderEntity.setAddOther(skuReductionTo.getCountStatus());
        if (skuReductionTo.getFullCount()>0){
            skuLadderService.save(skuLadderEntity);
        }

        skuLadderService.save(skuLadderEntity);

        //2、sms_sku_full_reduction
        SkuFullReductionEntity reductionEntity = new SkuFullReductionEntity();
        BeanUtils.copyProperties(skuReductionTo,reductionEntity);
        if (reductionEntity.getFullPrice().compareTo(new BigDecimal("0"))==1){
            this.save(reductionEntity);
        }


        //3.sms_member_price
        List<MemberPrice> memberPrice = skuReductionTo.getMemberPrice();
        if (memberPrice!=null && memberPrice.size()>0){
            List<MemberPriceEntity> collect = memberPrice.stream().map(item -> {
                MemberPriceEntity priceEntity = new MemberPriceEntity();
                priceEntity.setSkuId(skuReductionTo.getSkuId());
                priceEntity.setAddOther(1);
                priceEntity.setMemberPrice(item.getPrice());
                priceEntity.setMemberLevelId(item.getId());
                priceEntity.setMemberLevelName(item.getName());
                return priceEntity;
            }).filter(item->{
                return item.getMemberPrice().compareTo(new BigDecimal("0"))==1;
            }).collect(Collectors.toList());
            memberPriceService.saveBatch(collect);
        }

    }

}
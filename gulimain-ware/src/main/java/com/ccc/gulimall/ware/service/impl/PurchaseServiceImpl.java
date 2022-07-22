package com.ccc.gulimall.ware.service.impl;

import com.ccc.common.constant.WareConstant;
import com.ccc.gulimall.ware.entity.PurchaseDetailEntity;
import com.ccc.gulimall.ware.service.PurchaseDetailService;
import com.ccc.gulimall.ware.service.WareSkuService;
import com.ccc.gulimall.ware.vo.MergeVo;
import com.ccc.gulimall.ware.vo.PurchaseDoneVo;
import com.ccc.gulimall.ware.vo.PurchaseItemDoneVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ccc.common.utils.PageUtils;
import com.ccc.common.utils.Query;

import com.ccc.gulimall.ware.dao.PurchaseDao;
import com.ccc.gulimall.ware.entity.PurchaseEntity;
import com.ccc.gulimall.ware.service.PurchaseService;
import org.springframework.transaction.annotation.Transactional;


@Service("purchaseService")
public class PurchaseServiceImpl extends ServiceImpl<PurchaseDao, PurchaseEntity> implements PurchaseService {

    @Autowired
    PurchaseDetailService purchaseDetailService;


    @Autowired
    WareSkuService wareSkuService;



    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<PurchaseEntity> page = this.page(
                new Query<PurchaseEntity>().getPage(params),
                new QueryWrapper<PurchaseEntity>()
        );

        return new PageUtils(page);
    }

    @Override
    public PageUtils queryPageUnreceive(Map<String, Object> params) {
        IPage<PurchaseEntity> page = this.page(
                new Query<PurchaseEntity>().getPage(params),
                new QueryWrapper<PurchaseEntity>().eq("status",0).or().eq("status",1)
        );

        return new PageUtils(page);

    }

    @Transactional
    @Override
    public void mergePurchase(MergeVo mergeVo) {
        Long purchaseId = mergeVo.getPurchaseId();
        if (purchaseId==null){
            PurchaseEntity purchaseEntity = new PurchaseEntity();
            purchaseEntity.setStatus(WareConstant.PurchaseStatusEnum.CREATED.getCode());
            purchaseEntity.setCreateTime(new Date());
            purchaseEntity.setUpdateTime(new Date());
            this.save(purchaseEntity);
            purchaseId = purchaseEntity.getId();
        }

        //TODO 确认采购单的状态的0 或 1 才可以合并

        List<Long> items = mergeVo.getItems();
        Long finalPurchaseId = purchaseId;
        List<PurchaseDetailEntity> collect = items.stream().map(i -> {
            PurchaseDetailEntity detailEntity = new PurchaseDetailEntity();
            detailEntity.setId(i);
            detailEntity.setPurchaseId(finalPurchaseId);
            detailEntity.setStatus(WareConstant.PurchaseDetailStatusEnum.ASSIGNED.getCode());
            return detailEntity;
        }).collect(Collectors.toList());

        //根据集合里面的id进行批量修改
        purchaseDetailService.updateBatchById(collect);

        PurchaseEntity entity = new PurchaseEntity();
        entity.setId(purchaseId);
        entity.setUpdateTime(new Date());
        this.updateById(entity);
    }


    @Transactional
    @Override
    public void received(List<Long> ids) {
        //1、确认当前采购单是否是新建或者已分配
        List<PurchaseEntity> collect = ids.stream().map(id -> {
            PurchaseEntity byId = this.getById(id);
            return byId;
        }).filter(item -> {
            if (item.getStatus() == WareConstant.PurchaseStatusEnum.CREATED.getCode() || item.getStatus() == WareConstant.PurchaseStatusEnum.ASSIGNED.getCode()){
                return true;
            }
            return false;
        }).map(item->{
            item.setStatus(WareConstant.PurchaseStatusEnum.RECEIVE.getCode());
            item.setUpdateTime(new Date());
            return item;
        }).collect(Collectors.toList());

        //2、改变采购单状态
        this.updateBatchById(collect);

        //3、改变采购项的状态
        collect.forEach((item)->{
           List<PurchaseDetailEntity> detailEntities= purchaseDetailService.listDatailByPurchaseId(item.getId());
            List<PurchaseDetailEntity> collect1 = detailEntities.stream().map(i -> {
                PurchaseDetailEntity entity = new PurchaseDetailEntity();
                entity.setId(i.getId());
                entity.setStatus(WareConstant.PurchaseDetailStatusEnum.RECEIVE.getCode());
                return entity;
            }).collect(Collectors.toList());
            purchaseDetailService.updateBatchById(collect1);

        });

    }

    @Transactional
    @Override
    public void done(PurchaseDoneVo doneVo) {

        Long id = doneVo.getId();

        //2、改变采购项转态
        Boolean flag=true;
        List<PurchaseItemDoneVo> items = doneVo.getItems();
        ArrayList<PurchaseDetailEntity> updates = new ArrayList<>();
        for (PurchaseItemDoneVo item:items) {
            PurchaseDetailEntity detailEntity = new PurchaseDetailEntity();
            if (item.getStatus()==WareConstant.PurchaseDetailStatusEnum.HASERROR.getCode()){
                flag=false;
                detailEntity.setStatus(item.getStatus());
            }else{
                detailEntity.setStatus(WareConstant.PurchaseDetailStatusEnum.FINISH.getCode());
                //3、将成功采购的的进行入库
                PurchaseDetailEntity entity = purchaseDetailService.getById(item.getItemId());
                wareSkuService.addStock(entity.getSkuId(),entity.getWareId(),entity.getSkuNum());
            }
            detailEntity.setId(item.getItemId());
            updates.add(detailEntity);
        }
        purchaseDetailService.updateBatchById(updates);


        //1、改变采购单转态
        PurchaseEntity purchaseEntity = new PurchaseEntity();
        purchaseEntity.setId(id);
        purchaseEntity.setStatus(flag?WareConstant.PurchaseDetailStatusEnum.FINISH.getCode():WareConstant.PurchaseDetailStatusEnum.HASERROR.getCode());
        purchaseEntity.setUpdateTime(new Date());
        this.updateById(purchaseEntity);


    }
}
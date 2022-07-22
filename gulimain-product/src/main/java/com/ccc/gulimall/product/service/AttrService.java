package com.ccc.gulimall.product.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.ccc.common.utils.PageUtils;
import com.ccc.gulimall.product.entity.AttrEntity;
import com.ccc.gulimall.product.vo.AttrGroupRelationVo;
import com.ccc.gulimall.product.vo.AttrRespVo;
import com.ccc.gulimall.product.vo.AttrVo;

import java.util.List;
import java.util.Map;

/**
 * ??Ʒ?
 *
 * @author ccc
 * @email ccc@gmail.com
 * @date 2022-06-08 15:50:32
 */
public interface AttrService extends IService<AttrEntity> {

    PageUtils queryPage(Map<String, Object> params);

    void saveAttr(AttrVo attr);

    PageUtils queryBeseAttrPage(Map<String, Object> params, Long catelogId,String attrType);

    AttrRespVo getAtteInfo(Long attrId);

    void updateAttr(AttrRespVo attrRespVo);

    List<AttrEntity> getRelationAttr(Long attrGroupId);

    void deleteRelation(AttrGroupRelationVo[] vos);

    PageUtils getNoRelationAttr(Map<String ,Object> params ,Long attrGroupId);


    //在指定的所有属性集合里面，挑出检索属性
    List<Long> selectSerachAttrIds(List<Long> attrIds);
}


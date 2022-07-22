package com.ccc.gulimall.product.app;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import com.ccc.gulimall.product.entity.ProductAttrValueEntity;
import com.ccc.gulimall.product.service.ProductAttrValueService;
import com.ccc.gulimall.product.vo.AttrRespVo;
import com.ccc.gulimall.product.vo.AttrVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.ccc.gulimall.product.service.AttrService;
import com.ccc.common.utils.PageUtils;
import com.ccc.common.utils.R;



/**
 * ??Ʒ?
 *
 * @author ccc
 * @email ccc@gmail.com
 * @date 2022-06-08 15:50:32
 */
@RestController
@RequestMapping("product/attr")
public class AttrController {
    @Autowired
    private AttrService attrService;

    @Autowired
    ProductAttrValueService productAttrValueService;

    //http://localhost:88/api/product/attr/base/list/0?t=1655629142119&page=1&limit=10&key=


    //base/listforspu/13
    @RequestMapping("/base/listforspu/{spuId}")
    public R baseAttrlistValueforspu(@PathVariable("spuId") Long spuId){
     List<ProductAttrValueEntity> entitis=   productAttrValueService.baseAttrlistforspu(spuId);

        return R.ok().put("data",entitis);
    }


    /**
     * 列表
     */
    @GetMapping("/{attrType}/list/{catelogId}")
    public R baseList(@RequestParam  Map<String, Object> params,
                      @PathVariable("catelogId") Long catelogId,
                    @PathVariable("attrType")  String attrType){
        PageUtils page=  attrService.queryBeseAttrPage(params,catelogId,attrType);
       return R.ok().put("page",page);
    }


    @RequestMapping("/list")
    //@RequiresPermissions("product:attr:list")
    public R list(@RequestParam Map<String, Object> params){
        PageUtils page = attrService.queryPage(params);

        return R.ok().put("page", page);
    }


    /**
     * 信息
     */
    @RequestMapping("/info/{attrId}")
  //  @RequiresPermissions("product:attr:info")
    public R info(@PathVariable("attrId") Long attrId){
	//	AttrEntity attr = attrService.getById(attrId);
      AttrRespVo respVo=attrService.getAtteInfo(attrId);

        return R.ok().put("attr", respVo);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
    //@RequiresPermissions("product:attr:save")
   // public R save(@RequestBody AttrEntity attr){
	public R save(@RequestBody AttrVo attr){

		attrService.saveAttr(attr);

        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    //@RequiresPermissions("product:attr:update")
    public R update(@RequestBody AttrRespVo attrRespVo){
		//attrService.updateById(attr);
        attrService.updateAttr(attrRespVo);
        return R.ok();
    }

    @RequestMapping("/update/{spuId}")
    public R updateSpuAttr(@PathVariable("spuId")Long spuId,
                           @RequestBody List<ProductAttrValueEntity> entityList){
        productAttrValueService.updateSpuAttr(spuId,entityList);
        return R.ok();
    }


    /**
     * 删除
     */
    @RequestMapping("/delete")
    //@RequiresPermissions("product:attr:delete")
    public R delete(@RequestBody Long[] attrIds){
		attrService.removeByIds(Arrays.asList(attrIds));

        return R.ok();
    }

}

package com.ccc.gulimall.product.app;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import com.ccc.gulimall.product.entity.AttrEntity;
import com.ccc.gulimall.product.service.AttrAttrgroupRelationService;
import com.ccc.gulimall.product.service.AttrService;
import com.ccc.gulimall.product.service.CategoryService;
import com.ccc.gulimall.product.vo.AttrGroupRelationVo;
import com.ccc.gulimall.product.vo.AttrGroupWithAttrsVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.ccc.gulimall.product.entity.AttrGroupEntity;
import com.ccc.gulimall.product.service.AttrGroupService;
import com.ccc.common.utils.PageUtils;
import com.ccc.common.utils.R;



/**
 * ???Է??
 *
 * @author ccc
 * @email ccc@gmail.com
 * @date 2022-06-08 15:50:32
 */
@RestController
@RequestMapping("product/attrgroup")
public class AttrGroupController {
    @Autowired
    private AttrGroupService attrGroupService;

    @Autowired
    private CategoryService categoryService;
    @Autowired
    AttrService attrService;

    @Autowired
    AttrAttrgroupRelationService relationService;


    @RequestMapping("/attr/relation")
    public R addRelation(@RequestBody List<AttrGroupRelationVo> vos){

        relationService.saveBatchs(vos);
        return R.ok();
    }

    @RequestMapping("/{attrGroupId}/noattr/relation")
    public R getNoAttrRelation(@PathVariable("attrGroupId") Long attrGroupId,
                               @RequestParam Map<String,Object>params ){

       PageUtils page= attrService.getNoRelationAttr(params,attrGroupId);


        return R.ok().put("page",page);

    }


    //获取关联分类
    @GetMapping("/{attrGroupId}/attr/relation")
    public R attrRelation(@PathVariable("attrGroupId") Long attrGroupId){
      List<AttrEntity> attrEntityList=  attrService.getRelationAttr(attrGroupId);
        return R.ok().put("data",attrEntityList);
    }


    /**
     * 列表
     */
    @RequestMapping("/list/{categoryId}")
    //@RequiresPermissions("product:attrgroup:list")
    public R list(@RequestParam Map<String, Object> params,@PathVariable("categoryId")Long categoryId){
        //PageUtils page = attrGroupService.queryPage(params);

        PageUtils page =  attrGroupService.queryPage(params,categoryId);

        return R.ok().put("page", page);
    }


    /**
     * 信息
     */
    @RequestMapping("/info/{attrGroupId}")
  //  @RequiresPermissions("product:attrgroup:info")
    public R info(@PathVariable("attrGroupId") Long attrGroupId){
		AttrGroupEntity attrGroup = attrGroupService.getById(attrGroupId);

		Long catelogId = attrGroup.getCatelogId();
        Long[] path=categoryService.findCatelogPath(catelogId);

        attrGroup.setCatelogPath(path);
        return R.ok().put("attrGroup", attrGroup);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
    //@RequiresPermissions("product:attrgroup:save")
    public R save(@RequestBody AttrGroupEntity attrGroup){
		attrGroupService.save(attrGroup);

        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    //@RequiresPermissions("product:attrgroup:update")
    public R update(@RequestBody AttrGroupEntity attrGroup){
		attrGroupService.updateById(attrGroup);

        return R.ok();
    }

    //移除关联分类
    @RequestMapping("/attr/relation/delete")
    public R deleteRelation(@RequestBody AttrGroupRelationVo [] vos){
        attrService.deleteRelation(vos);
        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    //@RequiresPermissions("product:attrgroup:delete")
    public R delete(@RequestBody Long[] attrGroupIds){
		attrGroupService.removeByIds(Arrays.asList(attrGroupIds));

        return R.ok();
    }


    @GetMapping("/{catelogId}/withattr")
    public R getAttrGroupWithAttrs(@PathVariable("catelogId") Long catelogId){
        //1、查出当前分类下的所有属性分组
        //2、 查出当前属性分组的所有属性
        List<AttrGroupWithAttrsVo> vos=attrGroupService.getAttrGroupWithByCatelogId(catelogId);
        return R.ok().put("data", vos);
    }

}

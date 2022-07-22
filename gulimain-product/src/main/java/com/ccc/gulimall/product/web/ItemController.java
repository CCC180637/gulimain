package com.ccc.gulimall.product.web;


import com.ccc.gulimall.product.service.SkuInfoService;
import com.ccc.gulimall.product.vo.SkuItemVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Controller
public class ItemController {
    @Autowired
    SkuInfoService skuInfoService;


    @GetMapping("/{skuId}.html")
    public String SkuItem(@PathVariable("skuId") Long skuId, Model model) {

       SkuItemVo skuItemVo= skuInfoService.item(skuId);

       //放到请求域中，渲染页面
       model.addAttribute("item",skuItemVo);

        System.out.println("准备查询:" + skuId + "详情");

        return "item";
    }

}

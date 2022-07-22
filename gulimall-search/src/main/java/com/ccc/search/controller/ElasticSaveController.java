package com.ccc.search.controller;


import com.ccc.common.excrption.BizCodeEnume;
import com.ccc.common.to.es.SkuEsModel;
import com.ccc.common.utils.R;
import com.ccc.search.service.ProductSaveService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.List;

@Slf4j
@RequestMapping("/search/save")
@RestController
public class ElasticSaveController {

    @Autowired
    ProductSaveService productSaveService;


    //上架商品

    @PostMapping("/product")
    public R productStatusUp(@RequestBody List<SkuEsModel> skuEsModels) {

        Boolean b = false;
        try {
            b = productSaveService.productStatusUp(skuEsModels);
        } catch (Exception e) {
            log.error("ElasticSaveController商品上架错误", e);
            return R.error(BizCodeEnume.PRODUCT_UP_EXCRPTION.getCode(), BizCodeEnume.PRODUCT_UP_EXCRPTION.getMsg());
        }
        if (!b) {
            return R.ok();

        } else {
            return R.error(BizCodeEnume.PRODUCT_UP_EXCRPTION.getCode(), BizCodeEnume.PRODUCT_UP_EXCRPTION.getMsg());
        }
    }

}

package com.ccc.gulimall.product.feign;


import com.ccc.common.to.es.SkuEsModel;
import com.ccc.common.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@FeignClient("gulimall-search")
public interface SearchService {
    @PostMapping("/search/save/product")
     R productStatusUp(@RequestBody List<SkuEsModel> skuEsModels);

}

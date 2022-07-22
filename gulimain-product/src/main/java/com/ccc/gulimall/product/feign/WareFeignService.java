package com.ccc.gulimall.product.feign;


import com.ccc.common.to.SkuHasStockVo;
import com.ccc.common.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@FeignClient("gulimall-ware")
@Component
public interface WareFeignService {

    @PostMapping("ware/waresku/hasstock")
    R getSkuHasStock(@RequestBody List<Long> skuIds);
}

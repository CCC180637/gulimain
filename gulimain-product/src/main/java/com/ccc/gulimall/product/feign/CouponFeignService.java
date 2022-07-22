package com.ccc.gulimall.product.feign;


import com.ccc.common.to.SkuReductionTo;
import com.ccc.common.to.SpuBoundTo;
import com.ccc.common.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient("gulimall-coupon")
@Component
public interface CouponFeignService {

    /**
     *   SpringCloud调用流程
     *   1、CouponFeignService.saveSpuBounds(spuBoundTo);
     *     1)、@RequestBody注解将这个对象转为json。
     *     2)、找到gulimall_coupon服务，给/coupon/spubounds/save发送请求
     *     3)、对方服务接收到请求。请求体里有json数据
     *      (@RequestBody SpuBoundsEntity spuBounds);将请求体转换为SpuBoundsEntity；
     * 只要json数据模型是兼容的。双方服务无需使用同一个to
     *
     */


    @PostMapping("/coupon/spubounds/save")
    R saveSpuBounds(@RequestBody SpuBoundTo spuBoundTo);

    @RequestMapping("/coupon/skufullreduction/saveInfo")
    R saveSkuReduction(SkuReductionTo skuReductionTo);
}

package com.ccc.gulimall.auth.feign;

import com.ccc.common.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient("gulimall-third-party")
public interface ThriedFeignService {

    @GetMapping("/sms/PhoneCode")
    public R sendCode(@RequestParam("phone") String phone, @RequestParam("code") String code);
}

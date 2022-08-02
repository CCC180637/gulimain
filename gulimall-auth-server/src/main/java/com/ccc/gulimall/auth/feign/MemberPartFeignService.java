package com.ccc.gulimall.auth.feign;


import com.ccc.common.utils.R;
import com.ccc.gulimall.auth.vo.SocialUser;
import com.ccc.gulimall.auth.vo.UserLoginVo;
import com.ccc.gulimall.auth.vo.UserRegistVo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient("gulimall-member")
public interface MemberPartFeignService {

    @PostMapping("/member/member/regist")
    R regist(@RequestBody UserRegistVo vo);


    @PostMapping("/member/member/login")
    R logins(@RequestBody UserLoginVo vo);


    @PostMapping("/member/member/oauth/login")
    public R login(@RequestBody SocialUser socialUser) throws Exception;

}

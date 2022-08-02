package com.ccc.gulimall.auth.controller;



import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.ccc.common.constant.AuthServerConstant;
import com.ccc.common.utils.HttpUtils;
import com.ccc.common.utils.R;
import com.ccc.gulimall.auth.feign.MemberPartFeignService;
import com.ccc.gulimall.auth.vo.MemberRespVo;
import com.ccc.gulimall.auth.vo.SocialUser;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpResponse;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;


import javax.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.Map;


@Slf4j
@Controller
public class Oauth2Controller {


    @Autowired
    MemberPartFeignService memberPartFeignService;

    @GetMapping("/oauth2.0/weibo/success")
    public String weibo(@RequestParam("code") String code, HttpSession session) throws Exception {




        HashMap<String,String> map = new HashMap<>();
        map.put("client_id","263917288");
        map.put("client_secret","6a263e9284c6c1a74a62eadacc11b6e2");
        map.put("grant_type","authorization_code");
        map.put("redirect_uri","http://auth.gulimall.com/oauth2.0/weibo/success");
        map.put("code",code);


        HashMap<String,String> header = new HashMap<>();

        //1、根据 code 换取 accessToken
        HttpResponse response = HttpUtils.doPost("https://api.weibo.com", "/oauth2/access_token", "post", header,  null,map);

        //2.处理
        if (response.getStatusLine().getStatusCode() == 200){
            //获取到了accessToken
            String s = EntityUtils.toString(response.getEntity());
            SocialUser socialUser = JSON.parseObject(s, SocialUser.class);

            R login = memberPartFeignService.login(socialUser);
            if (login.getCode() ==0){
                MemberRespVo data = login.getData("data", new TypeReference<MemberRespVo>() {
                });
                log.info("登陆成功：用户：{}"+data.toString());
                //登录成功就跳转回首页
                session.setAttribute(AuthServerConstant.LOGIN_USER,data);

                return "redirect:http://gulimall.com";

            }else {
                return "redirect:http://auth.gulimall.com/login.html";
            }

        }else{
            return "redirect:http://auth.gulimall.com/login.html";
        }


    }
}

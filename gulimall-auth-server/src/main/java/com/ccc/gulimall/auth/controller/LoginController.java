package com.ccc.gulimall.auth.controller;


import com.alibaba.fastjson.TypeReference;
import com.ccc.common.constant.AuthServerConstant;
import com.ccc.common.excrption.BizCodeEnume;
import com.ccc.common.utils.R;
import com.ccc.gulimall.auth.feign.MemberPartFeignService;
import com.ccc.gulimall.auth.feign.ThriedFeignService;
import com.ccc.gulimall.auth.vo.MemberRespVo;
import com.ccc.gulimall.auth.vo.UserLoginVo;
import com.ccc.gulimall.auth.vo.UserRegistVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;

import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Controller
public class LoginController {
/*    @GetMapping("/login.html")
    public String loginPage(){
        return "login";
    }


    @GetMapping("/reg.html")
    public String regPage(){
        return "reg";
    }*/

    @Autowired
    ThriedFeignService thriedFeignService;

    @Autowired
    StringRedisTemplate redisTemplate;

    @Autowired
    MemberPartFeignService memberPartFeignService;


    //解决连续发送
    @ResponseBody
    @GetMapping("/sms/sendCode")
    public R sendCode(@RequestParam("phone") String phone) {

        String redisCode = redisTemplate.opsForValue().get(AuthServerConstant.SMS_CODE_CHECK_PREFIX + phone);

        if (!StringUtils.isEmpty(redisCode)) {
            long l = Long.parseLong(redisCode.split("_")[1]);
            if (System.currentTimeMillis() - l < 60000) {
                //小于60秒不能发送
                return R.error(BizCodeEnume.SMS_CODE_EXCRPTION.getCode(), BizCodeEnume.SMS_CODE_EXCRPTION.getMsg());
            }
        }

        //接口防刷
        String code = UUID.randomUUID().toString().substring(0, 5);
        //验证码的再次校验
        //验证码缓存到redis key value 形式 10分钟过期时间
        thriedFeignService.sendCode(phone, code);

        redisTemplate.opsForValue().set(AuthServerConstant.SMS_CODE_CHECK_PREFIX + phone, code + "_" + System.currentTimeMillis(), 10, TimeUnit.MINUTES);
        return R.ok();
    }


    //注册账号
    @PostMapping("/regist")
    public String regist(@Valid UserRegistVo vo, BindingResult result, RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {

            Map<String, String> errors = result.getFieldErrors().stream().collect(Collectors.toMap(FieldError::getField, FieldError::getDefaultMessage, (last, next) -> next));

            //model.addAttribute("errors",erroes);
            redirectAttributes.addFlashAttribute("errors", errors);

            //校验出错,转发到注册页
            return "redirect:http://auth.gulimall.com/reg.html";
        }

        //校验验证码
        String code = vo.getCode();
        String s = redisTemplate.opsForValue().get(AuthServerConstant.SMS_CODE_CHECK_PREFIX + vo.getPhone());
        if (!StringUtils.isEmpty(s)) {
            if (code.equals(s.split("_")[0])) {
                //校验正确，删除redis缓存
                redisTemplate.delete(AuthServerConstant.SMS_CODE_CHECK_PREFIX + vo.getPhone());

                //调用远程服务注册
                R regist = memberPartFeignService.regist(vo);
                if (regist.getCode() == 0) {
                    return "redirect:http://auth.gulimall.com/login.html";
                } else {
                    HashMap<String, String> errors = new HashMap<>();
                    errors.put("msg", regist.getData("msg", new TypeReference<String>() {
                    }));
                    return "redirect:http://auth.gulimall.com/reg.html";
                }

            } else {
                HashMap<String, String> errors = new HashMap<>();
                errors.put("meg", "验证码错误");
                redirectAttributes.addFlashAttribute("errors", errors);
                return "redirect:http://auth.gulimall.com/reg.html";

            }
        } else {
            HashMap<String, String> errors = new HashMap<>();
            errors.put("meg", "验证码错误");
            redirectAttributes.addFlashAttribute("errors", errors);
            return "redirect:http://auth.gulimall.com/reg.html";

        }

    }


    //登录
    @PostMapping("/login")
    public String login(UserLoginVo vo, RedirectAttributes redirectAttributes,
                        HttpSession session) {

        //远程登录
        R login = memberPartFeignService.logins(vo);
        //登录成功
        if (login.getCode() == 0) {
            MemberRespVo data = login.getData("data", new TypeReference<MemberRespVo>() {
            });

            session.setAttribute(AuthServerConstant.LOGIN_USER, data);

            return "redirect:http://gulimall.com";
        } else {
            HashMap<String, String> errors = new HashMap<>();
            errors.put("msg", login.getData("msg", new TypeReference<String>() {
            }));
            redirectAttributes.addFlashAttribute("errors", errors);
            return "redirect:http://auth.gulimall.com/login.html";
        }


    }

    //登录判断
    @GetMapping("/login.html")
    public String loginPage(HttpSession session){
        //获取session并进行判断
        Object attribute = session.getAttribute(AuthServerConstant.LOGIN_USER);
        if (attribute != null){
            return "redirect:http://gulimall.com";
        }else{
            return  "login" ;
        }
    }


}



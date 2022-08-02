package com.ccc.gulimall.member.controller;

import com.ccc.common.excrption.BizCodeEnume;
import com.ccc.common.utils.PageUtils;
import com.ccc.common.utils.R;
import com.ccc.gulimall.member.entity.MemberEntity;
import com.ccc.gulimall.member.exception.PhoneExsitException;
import com.ccc.gulimall.member.exception.UsernameExsitException;
import com.ccc.gulimall.member.feign.CouponFeignService;
import com.ccc.gulimall.member.service.MemberService;
import com.ccc.gulimall.member.vo.MemberLoginVo;
import com.ccc.gulimall.member.vo.MemberRegisVo;
import com.ccc.gulimall.member.vo.SocialUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.Map;


/**
 * 会员
 *
 * @author ccc
 * @email ccc@gmail.com
 * @date 2022-06-05 20:42:16
 */
@RestController
@RequestMapping("member/member")
public class MemberController {
    @Autowired
    private MemberService memberService;


    @Autowired
    CouponFeignService couponFeignService;




    /*
    * 社交登录
    * */
    @PostMapping("/oauth/login")
    public R login(@RequestBody SocialUser socialUser) throws Exception {

        MemberEntity entity= memberService.login(socialUser);

        if (entity != null){
            //TODO  登录成功处理
            return R.ok().setData(entity);
        }else{
            return R.error(BizCodeEnume.LOGIN_EXIST_EXCEPTION.getCode() ,BizCodeEnume.LOGIN_EXIST_EXCEPTION.getMsg());
        }

    }



    @RequestMapping("/coupons")
    public R test() {
        MemberEntity memberEntity = new MemberEntity();
        memberEntity.setNickname("张三");

        R memberCoupons = couponFeignService.memberCoupons();
        return R.ok().put("member", memberEntity).put("coupons", memberCoupons.get("coupons"));
    }


    //账号密码登录
    @PostMapping("/login")
    public R logins(@RequestBody MemberLoginVo vo){

      MemberEntity entity= memberService.login(vo);

      if (entity != null){
          return R.ok().setData(entity);
      }else{
          return R.error(BizCodeEnume.LOGIN_EXIST_EXCEPTION.getCode() ,BizCodeEnume.LOGIN_EXIST_EXCEPTION.getMsg());
      }

    }

    @PostMapping("/regist")
    public R regist(@RequestBody MemberRegisVo vo) {
        try {
            memberService.regist(vo);
        } catch (PhoneExsitException e) {
            return R.error(BizCodeEnume.SMS_CODE_EXCRPTION.getCode(),BizCodeEnume.PHONE_EXIST_EXCEPTION.getMsg());
        }catch (UsernameExsitException e){
            return R.error(BizCodeEnume.USER_EXIST_EXCEPTION.getCode(),BizCodeEnume.USER_EXIST_EXCEPTION.getMsg());
        }

        return R.ok();
    }


    /**
     * 列表
     */
    @RequestMapping("/list")
    //@RequiresPermissions("member:member:list")
    public R list(@RequestParam Map<String, Object> params) {
        PageUtils page = memberService.queryPage(params);

        return R.ok().put("page", page);
    }


    /**
     * 信息
     */
    @RequestMapping("/info/{id}")
    //  @RequiresPermissions("member:member:info")
    public R info(@PathVariable("id") Long id) {
        MemberEntity member = memberService.getById(id);

        return R.ok().put("member", member);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
    //@RequiresPermissions("member:member:save")
    public R save(@RequestBody MemberEntity member) {
        memberService.save(member);

        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    //@RequiresPermissions("member:member:update")
    public R update(@RequestBody MemberEntity member) {
        memberService.updateById(member);

        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    //@RequiresPermissions("member:member:delete")
    public R delete(@RequestBody Long[] ids) {
        memberService.removeByIds(Arrays.asList(ids));

        return R.ok();
    }

}

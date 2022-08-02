package com.ccc.gulimall.member.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.ccc.common.utils.PageUtils;
import com.ccc.gulimall.member.entity.MemberEntity;
import com.ccc.gulimall.member.exception.PhoneExsitException;
import com.ccc.gulimall.member.exception.UsernameExsitException;
import com.ccc.gulimall.member.vo.MemberLoginVo;
import com.ccc.gulimall.member.vo.MemberRegisVo;
import com.ccc.gulimall.member.vo.SocialUser;

import java.util.Map;

/**
 * 会员
 *
 * @author ccc
 * @email ccc@gmail.com
 * @date 2022-06-05 20:42:16
 */
public interface MemberService extends IService<MemberEntity> {

    PageUtils queryPage(Map<String, Object> params);

    void regist(MemberRegisVo vo);


    void checkPhoneUnique(String phone) throws PhoneExsitException;

    void checkUsernameUnique(String username) throws UsernameExsitException;

    MemberEntity login(MemberLoginVo vo);

    MemberEntity login(SocialUser socialUser) throws Exception;
}


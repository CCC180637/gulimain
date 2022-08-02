package com.ccc.gulimall.member.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ccc.common.utils.HttpUtils;
import com.ccc.common.utils.PageUtils;
import com.ccc.common.utils.Query;
import com.ccc.gulimall.member.dao.MemberDao;
import com.ccc.gulimall.member.dao.MemberLevelDao;
import com.ccc.gulimall.member.entity.MemberEntity;
import com.ccc.gulimall.member.entity.MemberLevelEntity;
import com.ccc.gulimall.member.exception.PhoneExsitException;
import com.ccc.gulimall.member.exception.UsernameExsitException;
import com.ccc.gulimall.member.service.MemberLevelService;
import com.ccc.gulimall.member.service.MemberService;
import com.ccc.gulimall.member.vo.MemberLoginVo;
import com.ccc.gulimall.member.vo.MemberRegisVo;
import com.ccc.gulimall.member.vo.SocialUser;
import org.apache.http.HttpResponse;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Service("memberService")
public class MemberServiceImpl extends ServiceImpl<MemberDao, MemberEntity> implements MemberService {

    @Autowired
    MemberLevelService memberLevelService;

    @Autowired
    MemberLevelDao memberLevelDao;


    @Override
    public MemberEntity login(SocialUser socialUser) throws Exception {
        //登录和注册合并逻辑
        String uid = socialUser.getUid();
        //1、判断当前社交用户是否已经登录过系统
        MemberDao memberDao = this.baseMapper;
        MemberEntity entity = memberDao.selectOne(new QueryWrapper<MemberEntity>().eq("social_uid", uid));
        if (!StringUtils.isEmpty(entity)){
            //这个用户已经注册
            MemberEntity update = new MemberEntity();
            update.setId(entity.getId());
            update.setAccessToken(socialUser.getAccess_token());
            update.setExpiresIn(socialUser.getExpires_in());

            memberDao.updateById(update);

            entity.setAccessToken(socialUser.getAccess_token());
            return entity;
        }else{
            //2、没有查询到当前社交用户对应记录我们就需要注册一个
            MemberEntity regist = new MemberEntity();
            //3、查询当前社交用户的社交账号信息（昵称、性别等）
            HashMap<String,String> query = new HashMap<>();
            query.put("access_token",socialUser.getAccess_token()) ;
            query.put("uid",socialUser.getUid());
            HttpResponse response = HttpUtils.doGet("https://api.weibo.com", "/2/users/show.json", "get", new HashMap<String, String>(), query);

            try {
                if (response.getStatusLine().getStatusCode() == 200) {
                    //查询成功
                    String json = EntityUtils.toString(response.getEntity());
                    JSONObject jsonObject = JSON.parseObject(json);
                    //昵称
                    String name = jsonObject.getString("name");
                    String gender = jsonObject.getString("gender");
                    regist.setNickname(name);
                    regist.setGender("m".equals(gender) ? 1 : 0);

                }
            }catch (Exception e){

            }
            regist.setSocialUid(socialUser.getUid());
            regist.setAccessToken(socialUser.getAccess_token());
            regist.setExpiresIn(socialUser.getExpires_in());
            memberDao.insert(regist);
            return regist;

        }

    }

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<MemberEntity> page = this.page(
                new Query<MemberEntity>().getPage(params),
                new QueryWrapper<MemberEntity>()
        );

        return new PageUtils(page);
    }

    @Override
    public void regist(MemberRegisVo vo) {
        MemberDao memberDao = this.baseMapper;
        MemberEntity entity = new MemberEntity();

        MemberLevelEntity memberLevelEntity = memberLevelDao.getDefaultLevel();

        //检查用户名和手机号是否唯一
        checkPhoneUnique(vo.getPhone());
        checkUsernameUnique(vo.getUserName());

        entity.setMobile(vo.getPhone());
        entity.setLevelId(memberLevelEntity.getId());
        entity.setUsername(vo.getUserName());
        entity.setNickname(vo.getUserName());

        //密码加密存储
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        String encode = encoder.encode(vo.getPassWord());

        entity.setPassword(encode);

        //保存
        memberDao.insert(entity);
    }

    @Override
    public void checkPhoneUnique(String phone) {
        MemberDao memberDao = this.baseMapper;
        Integer integer = memberDao.selectCount(new QueryWrapper<MemberEntity>().eq("mobile", phone));
        if (integer > 0) {
            throw new PhoneExsitException();
        }
    }

    @Override
    public void checkUsernameUnique(String username) {
        MemberDao memberDao = this.baseMapper;
        Integer integer = memberDao.selectCount(new QueryWrapper<MemberEntity>().eq("username", username));
        if (integer > 0) {
            throw new UsernameExsitException();
        }

    }


    @Override
    public MemberEntity login(MemberLoginVo vo) {

        String loginacct = vo.getLoginacct();
        String password = vo.getPassword();

        MemberDao memberDao = this.baseMapper;

        MemberEntity entity = memberDao.selectOne(new QueryWrapper<MemberEntity>().eq("mobile", loginacct).or().eq("username", loginacct));

        if (entity == null) {
            //登录失败
            return null;
        } else {
            String password1 = entity.getPassword();
            BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
            boolean matches = encoder.matches(password, password1);
            if (matches) {
                return entity;
            } else {
                return null;
            }
        }

    }
}
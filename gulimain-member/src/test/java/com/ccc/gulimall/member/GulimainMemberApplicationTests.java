package com.ccc.gulimall.member;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.ccc.gulimall.member.dao.MemberDao;
import com.ccc.gulimall.member.entity.MemberEntity;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

@SpringBootTest
public class GulimainMemberApplicationTests {


    @Autowired
    MemberDao memberDao;

    @Test
    public void contextLoads() {
        System.out.println(11111);
    }


    @Test
    public void test1(){


        List<MemberEntity> entities = memberDao.selectList(new QueryWrapper<MemberEntity>().eq("username", "lpf1234"));
        System.out.println(entities);

    }

}

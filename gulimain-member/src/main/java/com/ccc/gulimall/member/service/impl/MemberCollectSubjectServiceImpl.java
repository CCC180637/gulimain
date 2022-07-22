package com.ccc.gulimall.member.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ccc.common.utils.PageUtils;
import com.ccc.common.utils.Query;
import com.ccc.gulimall.member.dao.MemberCollectSubjectDao;
import com.ccc.gulimall.member.entity.MemberCollectSubjectEntity;
import com.ccc.gulimall.member.service.MemberCollectSubjectService;
import org.springframework.stereotype.Service;

import java.util.Map;


@Service("memberCollectSubjectService")
public class MemberCollectSubjectServiceImpl extends ServiceImpl<MemberCollectSubjectDao, MemberCollectSubjectEntity> implements MemberCollectSubjectService {

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<MemberCollectSubjectEntity> page = this.page(
                new Query<MemberCollectSubjectEntity>().getPage(params),
                new QueryWrapper<MemberCollectSubjectEntity>()
        );

        return new PageUtils(page);
    }

}
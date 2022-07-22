package com.ccc.gulimall.member.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ccc.common.utils.PageUtils;
import com.ccc.common.utils.Query;
import com.ccc.gulimall.member.dao.MemberReceiveAddressDao;
import com.ccc.gulimall.member.entity.MemberReceiveAddressEntity;
import com.ccc.gulimall.member.service.MemberReceiveAddressService;
import org.springframework.stereotype.Service;

import java.util.Map;


@Service("memberReceiveAddressService")
public class MemberReceiveAddressServiceImpl extends ServiceImpl<MemberReceiveAddressDao, MemberReceiveAddressEntity> implements MemberReceiveAddressService {

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<MemberReceiveAddressEntity> page = this.page(
                new Query<MemberReceiveAddressEntity>().getPage(params),
                new QueryWrapper<MemberReceiveAddressEntity>()
        );

        return new PageUtils(page);
    }

}
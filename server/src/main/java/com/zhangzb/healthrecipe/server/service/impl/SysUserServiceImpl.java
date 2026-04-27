package com.zhangzb.healthrecipe.server.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zhangzb.healthrecipe.server.entity.SysUser;
import com.zhangzb.healthrecipe.server.mapper.SysUserMapper;
import com.zhangzb.healthrecipe.server.service.SysUserService;
import org.springframework.stereotype.Service;

@Service
public class SysUserServiceImpl extends ServiceImpl<SysUserMapper, SysUser> implements SysUserService {

    @Override
    public SysUser getDefaultUser() {
        return getOne(new LambdaQueryWrapper<SysUser>().last("LIMIT 1"));
    }
}

package com.zhangzb.healthrecipe.server.config;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.zhangzb.healthrecipe.server.entity.SysUser;
import com.zhangzb.healthrecipe.server.service.SysUserService;

@Component
public class SecurityUtil {

    @Autowired
    private SysUserService userService;

    public Long getCurrentUserId() {
        SysUser user = userService.getDefaultUser();
        if (user != null) {
            return user.getId();
        }
        SysUser newUser = new SysUser();
        newUser.setUsername("default");
        newUser.setPassword(UUID.randomUUID().toString());
        userService.save(newUser);
        return newUser.getId();
    }
}

package com.zhangzb.healthrecipe.server.config;

import com.zhangzb.healthrecipe.server.entity.SysUser;
import com.zhangzb.healthrecipe.server.service.SysUserService;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class SecurityUtil {

    private static SecurityUtil instance;

    @Autowired
    private SysUserService userService;

    @PostConstruct
    private void init() {
        instance = this;
    }

    public static Long getCurrentUserId() {
        SysUser user = instance.userService.getDefaultUser();
        if (user != null) {
            return user.getId();
        }
        SysUser newUser = new SysUser();
        newUser.setUsername("default");
        newUser.setPassword("default");
        instance.userService.save(newUser);
        return newUser.getId();
    }
}

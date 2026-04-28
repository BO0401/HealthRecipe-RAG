package com.zhangzb.healthrecipe.server.config;

public class SecurityUtil {

    private static final Long DEFAULT_USER_ID = 1L;

    public static Long getCurrentUserId() {
        return DEFAULT_USER_ID;
    }
}

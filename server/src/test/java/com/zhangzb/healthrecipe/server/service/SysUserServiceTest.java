package com.zhangzb.healthrecipe.server.service;

import com.zhangzb.healthrecipe.server.entity.SysUser;
import com.zhangzb.healthrecipe.server.mapper.SysUserMapper;
import com.zhangzb.healthrecipe.server.service.impl.SysUserServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SysUserServiceTest {

    @Mock
    private SysUserMapper userMapper;

    @InjectMocks
    private SysUserServiceImpl userService;

    @Test
    void getDefaultUser_shouldReturnFirstUser() {
        SysUser expected = new SysUser();
        expected.setId(1L);
        expected.setUsername("admin");
        when(userMapper.selectOne(any())).thenReturn(expected);

        SysUser result = userService.getDefaultUser();

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("admin", result.getUsername());
        verify(userMapper).selectOne(any());
    }

    @Test
    void getDefaultUser_shouldReturnNull_whenNoUser() {
        when(userMapper.selectOne(any())).thenReturn(null);

        SysUser result = userService.getDefaultUser();

        assertNull(result);
        verify(userMapper).selectOne(any());
    }
}

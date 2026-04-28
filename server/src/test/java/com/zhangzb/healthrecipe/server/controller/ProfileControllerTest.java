package com.zhangzb.healthrecipe.server.controller;

import com.zhangzb.healthrecipe.server.config.Result;
import com.zhangzb.healthrecipe.server.dto.ProfileDataDTO;
import com.zhangzb.healthrecipe.server.entity.SysUser;
import com.zhangzb.healthrecipe.server.service.SysInventoryService;
import com.zhangzb.healthrecipe.server.service.SysUserService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProfileControllerTest {

    @Mock
    private SysUserService userService;

    @Mock
    private SysInventoryService inventoryService;

    @InjectMocks
    private ProfileController profileController;

    @Test
    void get_shouldReturnProfile_whenUserExists() {
        SysUser user = new SysUser();
        user.setId(1L);
        user.setHeightCm(BigDecimal.valueOf(170));
        user.setWeightKg(BigDecimal.valueOf(65));
        user.setAllergens("花生,牛奶");
        when(userService.getDefaultUser()).thenReturn(user);
        when(inventoryService.listByUserId(1L)).thenReturn(List.of());

        Result<ProfileDataDTO> result = profileController.get();

        assertTrue(result.getCode() == 200);
        assertNotNull(result.getData().getUser());
        assertEquals(0, BigDecimal.valueOf(170).compareTo(result.getData().getUser().getHeight()));
        assertEquals(2, result.getData().getUser().getAllergens().size());
    }

    @Test
    void get_shouldReturnEmpty_whenNoUser() {
        when(userService.getDefaultUser()).thenReturn(null);

        Result<ProfileDataDTO> result = profileController.get();

        assertTrue(result.getCode() == 200);
        assertNotNull(result.getData().getUser());
        assertEquals(0, BigDecimal.ZERO.compareTo(result.getData().getUser().getHeight()));
        assertEquals(0, BigDecimal.ZERO.compareTo(result.getData().getUser().getWeight()));
        assertTrue(result.getData().getUser().getAllergens().isEmpty());
    }

    @Test
    void save_shouldUpdateUser() {
        SysUser existing = new SysUser();
        existing.setId(1L);
        existing.setUsername("default");
        when(userService.getDefaultUser()).thenReturn(existing);

        ProfileDataDTO dto = new ProfileDataDTO();
        ProfileDataDTO.UserProfile profile = new ProfileDataDTO.UserProfile();
        profile.setHeight(BigDecimal.valueOf(175));
        profile.setWeight(BigDecimal.valueOf(70));
        profile.setAllergens(List.of("海鲜"));
        dto.setUser(profile);
        dto.setInventory(List.of());

        Result<ProfileDataDTO> result = profileController.save(dto);

        assertTrue(result.getCode() == 200);
        verify(userService).updateById(any());
    }
}

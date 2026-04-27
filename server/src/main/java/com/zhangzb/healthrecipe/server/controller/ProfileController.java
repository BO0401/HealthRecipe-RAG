package com.zhangzb.healthrecipe.server.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.zhangzb.healthrecipe.server.config.Result;
import com.zhangzb.healthrecipe.server.dto.ProfileDataDTO;
import com.zhangzb.healthrecipe.server.entity.SysInventory;
import com.zhangzb.healthrecipe.server.entity.SysUser;
import com.zhangzb.healthrecipe.server.service.SysInventoryService;
import com.zhangzb.healthrecipe.server.service.SysUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/profile")
public class ProfileController {

    @Autowired
    private SysUserService userService;

    @Autowired
    private SysInventoryService inventoryService;

    @GetMapping
    public Result<ProfileDataDTO> get() {
        SysUser user = userService.getById(1L);
        if (user == null) {
            return Result.success(new ProfileDataDTO());
        }

        ProfileDataDTO dto = new ProfileDataDTO();
        ProfileDataDTO.UserProfile profile = new ProfileDataDTO.UserProfile();
        profile.setHeight(user.getHeightCm());
        profile.setWeight(user.getWeightKg());
        if (user.getAllergens() != null && !user.getAllergens().isEmpty()) {
            profile.setAllergens(List.of(user.getAllergens().split(",")));
        } else {
            profile.setAllergens(List.of());
        }
        dto.setUser(profile);

        List<SysInventory> inventoryList = inventoryService.list(
                new LambdaQueryWrapper<SysInventory>().eq(SysInventory::getUserId, 1L)
        );
        List<ProfileDataDTO.InventoryItem> inventoryItems = inventoryList.stream().map(item -> {
            ProfileDataDTO.InventoryItem inv = new ProfileDataDTO.InventoryItem();
            inv.setName(item.getIngredientId().toString());
            inv.setQuantity(item.getQuantity());
            return inv;
        }).collect(Collectors.toList());
        dto.setInventory(inventoryItems);

        return Result.success(dto);
    }

    @PutMapping
    public Result<ProfileDataDTO> save(@RequestBody ProfileDataDTO dto) {
        SysUser user = userService.getById(1L);
        if (user == null) {
            user = new SysUser();
            user.setId(1L);
            user.setUsername("default");
            user.setPassword("default");
        }

        if (dto.getUser() != null) {
            ProfileDataDTO.UserProfile profile = dto.getUser();
            if (profile.getHeight() != null) user.setHeightCm(profile.getHeight());
            if (profile.getWeight() != null) user.setWeightKg(profile.getWeight());
            if (profile.getAllergens() != null) {
                user.setAllergens(String.join(",", profile.getAllergens()));
            }
        }

        if (user.getId() == null) {
            userService.save(user);
        } else {
            userService.updateById(user);
        }

        if (dto.getInventory() != null) {
            inventoryService.remove(new LambdaQueryWrapper<SysInventory>().eq(SysInventory::getUserId, 1L));
            for (ProfileDataDTO.InventoryItem invItem : dto.getInventory()) {
                SysInventory inv = new SysInventory();
                inv.setUserId(1L);
                inv.setQuantity(invItem.getQuantity());
                inv.setUnit("个");
                inventoryService.save(inv);
            }
        }

        return Result.success(dto);
    }
}

package com.zhangzb.healthrecipe.server.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.zhangzb.healthrecipe.server.config.Result;
import com.zhangzb.healthrecipe.server.dto.ProfileDataDTO;
import com.zhangzb.healthrecipe.server.entity.SysInventory;
import com.zhangzb.healthrecipe.server.entity.SysUser;
import com.zhangzb.healthrecipe.server.service.SysInventoryService;
import com.zhangzb.healthrecipe.server.service.SysUserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@Tag(name = "个人中心", description = "用户资料与库存管理")
@RestController
@RequestMapping("/api/profile")
public class ProfileController {

    @Autowired
    private SysUserService userService;

    @Autowired
    private SysInventoryService inventoryService;

    @Operation(summary = "获取用户资料与库存")
    @GetMapping
    public Result<ProfileDataDTO> get() {
        SysUser user = userService.getDefaultUser();
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

        List<SysInventory> inventoryList = inventoryService.listByUserId(user.getId());
        List<ProfileDataDTO.InventoryItem> inventoryItems = inventoryList.stream().map(item -> {
            ProfileDataDTO.InventoryItem inv = new ProfileDataDTO.InventoryItem();
            inv.setName(item.getIngredientId().toString());
            inv.setQuantity(item.getQuantity());
            return inv;
        }).collect(Collectors.toList());
        dto.setInventory(inventoryItems);

        return Result.success(dto);
    }

    @Operation(summary = "保存用户资料与库存")
    @PutMapping
    public Result<ProfileDataDTO> save(@Valid @RequestBody ProfileDataDTO dto) {
        SysUser user = userService.getDefaultUser();
        if (user == null) {
            user = new SysUser();
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
            inventoryService.remove(new LambdaQueryWrapper<SysInventory>().eq(SysInventory::getUserId, user.getId()));
            for (ProfileDataDTO.InventoryItem invItem : dto.getInventory()) {
                SysInventory inv = new SysInventory();
                inv.setUserId(user.getId());
                inv.setQuantity(invItem.getQuantity());
                inv.setUnit("个");
                inventoryService.save(inv);
            }
        }

        return Result.success(dto);
    }
}

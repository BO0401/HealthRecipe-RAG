package com.zhangzb.healthrecipe.server.controller;

import com.zhangzb.healthrecipe.server.config.Result;
import com.zhangzb.healthrecipe.server.config.SecurityUtil;
import com.zhangzb.healthrecipe.server.dto.InventoryCreateDTO;
import com.zhangzb.healthrecipe.server.dto.InventoryUpdateDTO;
import com.zhangzb.healthrecipe.server.dto.InventoryVO;
import com.zhangzb.healthrecipe.server.entity.SysIngredient;
import com.zhangzb.healthrecipe.server.entity.SysInventory;
import com.zhangzb.healthrecipe.server.service.SysIngredientService;
import com.zhangzb.healthrecipe.server.service.SysInventoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@Tag(name = "库存管理", description = "家庭库存的增删改查及过期预警")
@RestController
@RequestMapping("/api/inventory")
public class InventoryController {

    @Autowired
    private SysInventoryService inventoryService;

    @Autowired
    private SysIngredientService ingredientService;

    @Operation(summary = "获取库存列表")
    @GetMapping("/list")
    public Result<List<InventoryVO>> list() {
        List<SysInventory> list = inventoryService.listByUserId(SecurityUtil.getCurrentUserId());
        return Result.success(toVOList(list));
    }

    @Operation(summary = "添加库存")
    @PostMapping("/add")
    public Result<InventoryVO> add(@Valid @RequestBody InventoryCreateDTO dto) {
        SysIngredient ingredient = ingredientService.getOrCreate(dto.getIngredientName(), null, dto.getUnit());

        SysInventory inventory = new SysInventory();
        inventory.setUserId(SecurityUtil.getCurrentUserId());
        inventory.setIngredientId(ingredient.getId());
        inventory.setQuantity(dto.getQuantity());
        inventory.setUnit(dto.getUnit());
        inventory.setExpireDate(dto.getExpireDate());
        inventoryService.save(inventory);

        InventoryVO vo = toVO(inventory);
        vo.setIngredientName(ingredient.getName());
        vo.setCategory(ingredient.getCategory());
        return Result.success(vo);
    }

    @Operation(summary = "更新库存")
    @PutMapping("/update")
    public Result<InventoryVO> update(@Valid @RequestBody InventoryUpdateDTO dto) {
        SysInventory inventory = inventoryService.getById(dto.getId());
        if (inventory == null) {
            return Result.error(404, "库存项不存在");
        }
        if (dto.getQuantity() != null) inventory.setQuantity(dto.getQuantity());
        if (dto.getUnit() != null) inventory.setUnit(dto.getUnit());
        if (dto.getExpireDate() != null) inventory.setExpireDate(dto.getExpireDate());
        inventoryService.updateById(inventory);
        return Result.success(toVO(inventory));
    }

    @Operation(summary = "删除库存")
    @DeleteMapping("/remove/{id}")
    public Result<Void> remove(@Parameter(description = "库存ID") @PathVariable Long id) {
        if (!inventoryService.removeById(id)) {
            return Result.error(404, "库存项不存在");
        }
        return Result.success();
    }

    @Operation(summary = "获取即将过期的库存", description = "返回3天内过期的库存项")
    @GetMapping("/expiring-soon")
    public Result<List<InventoryVO>> expiringSoon() {
        List<SysInventory> list = inventoryService.findExpiringSoon(SecurityUtil.getCurrentUserId(), 3);
        return Result.success(toVOList(list));
    }

    private InventoryVO toVO(SysInventory inventory) {
        InventoryVO vo = new InventoryVO();
        vo.setId(inventory.getId());
        vo.setUserId(inventory.getUserId());
        vo.setIngredientId(inventory.getIngredientId());
        vo.setQuantity(inventory.getQuantity());
        vo.setUnit(inventory.getUnit());
        vo.setExpireDate(inventory.getExpireDate());
        vo.setCreateTime(inventory.getCreateTime());
        vo.setUpdateTime(inventory.getUpdateTime());

        if (inventory.getIngredientId() != null && inventory.getIngredientId() > 0) {
            SysIngredient ingredient = ingredientService.getById(inventory.getIngredientId());
            if (ingredient != null) {
                vo.setIngredientName(ingredient.getName());
                vo.setCategory(ingredient.getCategory());
            }
        }
        return vo;
    }

    private List<InventoryVO> toVOList(List<SysInventory> list) {
        return list.stream().map(this::toVO).collect(Collectors.toList());
    }
}

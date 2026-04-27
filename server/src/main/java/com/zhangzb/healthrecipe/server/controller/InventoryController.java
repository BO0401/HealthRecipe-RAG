package com.zhangzb.healthrecipe.server.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.zhangzb.healthrecipe.server.config.Result;
import com.zhangzb.healthrecipe.server.dto.InventoryCreateDTO;
import com.zhangzb.healthrecipe.server.dto.InventoryUpdateDTO;
import com.zhangzb.healthrecipe.server.dto.InventoryVO;
import com.zhangzb.healthrecipe.server.entity.SysIngredient;
import com.zhangzb.healthrecipe.server.entity.SysInventory;
import com.zhangzb.healthrecipe.server.service.SysIngredientService;
import com.zhangzb.healthrecipe.server.service.SysInventoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/inventory")
public class InventoryController {

    @Autowired
    private SysInventoryService inventoryService;

    @Autowired
    private SysIngredientService ingredientService;

    @GetMapping("/list")
    public Result<List<InventoryVO>> list() {
        List<SysInventory> list = inventoryService.list(
                new LambdaQueryWrapper<SysInventory>().orderByDesc(SysInventory::getCreateTime)
        );
        return Result.success(toVOList(list));
    }

    @PostMapping("/add")
    public Result<InventoryVO> add(@RequestBody InventoryCreateDTO dto) {
        SysInventory inventory = new SysInventory();
        inventory.setUserId(1L);
        inventory.setIngredientId(0L);
        inventory.setQuantity(dto.getQuantity());
        inventory.setUnit(dto.getUnit());
        inventory.setExpireDate(dto.getExpireDate());
        inventoryService.save(inventory);

        InventoryVO vo = toVO(inventory);
        vo.setIngredientName(dto.getIngredientName());
        return Result.success(vo);
    }

    @PutMapping("/update")
    public Result<InventoryVO> update(@RequestBody InventoryUpdateDTO dto) {
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

    @DeleteMapping("/remove/{id}")
    public Result<Void> remove(@PathVariable Long id) {
        if (!inventoryService.removeById(id)) {
            return Result.error(404, "库存项不存在");
        }
        return Result.success();
    }

    @GetMapping("/expiring-soon")
    public Result<List<InventoryVO>> expiringSoon() {
        LocalDate now = LocalDate.now();
        LocalDate threshold = now.plus(3, ChronoUnit.DAYS);
        List<SysInventory> list = inventoryService.list(
                new LambdaQueryWrapper<SysInventory>()
                        .isNotNull(SysInventory::getExpireDate)
                        .le(SysInventory::getExpireDate, threshold)
                        .ge(SysInventory::getExpireDate, now)
                        .orderByAsc(SysInventory::getExpireDate)
        );
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

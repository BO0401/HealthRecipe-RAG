package com.zhangzb.healthrecipe.server.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.zhangzb.healthrecipe.server.config.Result;
import com.zhangzb.healthrecipe.server.dto.ShoppingListCreateDTO;
import com.zhangzb.healthrecipe.server.dto.ShoppingListUpdateDTO;
import com.zhangzb.healthrecipe.server.entity.SysShoppingList;
import com.zhangzb.healthrecipe.server.service.SysShoppingListService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/shopping")
public class ShoppingController {

    @Autowired
    private SysShoppingListService shoppingListService;

    @GetMapping("/list")
    public Result<List<SysShoppingList>> list() {
        LambdaQueryWrapper<SysShoppingList> wrapper = new LambdaQueryWrapper<>();
        wrapper.orderByDesc(SysShoppingList::getCreateTime);
        return Result.success(shoppingListService.list(wrapper));
    }

    @PostMapping("/add")
    public Result<SysShoppingList> add(@RequestBody ShoppingListCreateDTO dto) {
        SysShoppingList item = new SysShoppingList();
        item.setUserId(1L);
        item.setIngredientName(dto.getIngredientName());
        item.setQuantity(dto.getQuantity());
        item.setUnit(dto.getUnit());
        item.setStatus(0);
        shoppingListService.save(item);
        return Result.success(item);
    }

    @PostMapping("/batch-add")
    public Result<List<SysShoppingList>> batchAdd(@RequestBody List<ShoppingListCreateDTO> items) {
        List<SysShoppingList> list = items.stream().map(dto -> {
            SysShoppingList item = new SysShoppingList();
            item.setUserId(1L);
            item.setIngredientName(dto.getIngredientName());
            item.setQuantity(dto.getQuantity());
            item.setUnit(dto.getUnit());
            item.setStatus(0);
            return item;
        }).collect(Collectors.toList());
        shoppingListService.saveBatch(list);
        return Result.success(list);
    }

    @PutMapping("/update")
    public Result<SysShoppingList> update(@RequestBody ShoppingListUpdateDTO dto) {
        SysShoppingList item = shoppingListService.getById(dto.getId());
        if (item == null) {
            return Result.error(404, "采购项不存在");
        }
        if (dto.getQuantity() != null) item.setQuantity(dto.getQuantity());
        if (dto.getUnit() != null) item.setUnit(dto.getUnit());
        if (dto.getStatus() != null) item.setStatus(dto.getStatus());
        shoppingListService.updateById(item);
        return Result.success(item);
    }

    @DeleteMapping("/remove/{id}")
    public Result<Void> remove(@PathVariable Long id) {
        if (!shoppingListService.removeById(id)) {
            return Result.error(404, "采购项不存在");
        }
        return Result.success();
    }

    @DeleteMapping("/clear-purchased")
    public Result<Void> clearPurchased() {
        LambdaQueryWrapper<SysShoppingList> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysShoppingList::getStatus, 1);
        shoppingListService.remove(wrapper);
        return Result.success();
    }

    @PostMapping("/generate")
    public Result<List<SysShoppingList>> generate(@RequestBody Map<String, List<Long>> body) {
        List<Long> recipeIds = body.get("recipeIds");
        return Result.success(shoppingListService.listByIds(recipeIds));
    }
}

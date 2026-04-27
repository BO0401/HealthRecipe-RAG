package com.zhangzb.healthrecipe.server.controller;

import com.zhangzb.healthrecipe.server.config.Result;
import com.zhangzb.healthrecipe.server.dto.ShoppingListCreateDTO;
import com.zhangzb.healthrecipe.server.dto.ShoppingListUpdateDTO;
import com.zhangzb.healthrecipe.server.entity.SysShoppingList;
import com.zhangzb.healthrecipe.server.service.SysShoppingListService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Tag(name = "采购清单管理", description = "采购清单的增删改查、批量操作及清空已购")
@RestController
@RequestMapping("/api/shopping")
public class ShoppingController {

    @Autowired
    private SysShoppingListService shoppingListService;

    @Operation(summary = "获取采购清单")
    @GetMapping("/list")
    public Result<List<SysShoppingList>> list() {
        return Result.success(shoppingListService.listByUserId(1L));
    }

    @Operation(summary = "添加采购项")
    @PostMapping("/add")
    public Result<SysShoppingList> add(@Valid @RequestBody ShoppingListCreateDTO dto) {
        SysShoppingList item = new SysShoppingList();
        item.setUserId(1L);
        item.setIngredientName(dto.getIngredientName());
        item.setQuantity(dto.getQuantity());
        item.setUnit(dto.getUnit());
        item.setStatus(0);
        shoppingListService.save(item);
        return Result.success(item);
    }

    @Operation(summary = "批量添加采购项")
    @PostMapping("/batch-add")
    public Result<List<SysShoppingList>> batchAdd(@Valid @RequestBody List<ShoppingListCreateDTO> items) {
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

    @Operation(summary = "更新采购项")
    @PutMapping("/update")
    public Result<SysShoppingList> update(@Valid @RequestBody ShoppingListUpdateDTO dto) {
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

    @Operation(summary = "删除采购项")
    @DeleteMapping("/remove/{id}")
    public Result<Void> remove(@Parameter(description = "采购项ID") @PathVariable Long id) {
        if (!shoppingListService.removeById(id)) {
            return Result.error(404, "采购项不存在");
        }
        return Result.success();
    }

    @Operation(summary = "清空已购项目")
    @DeleteMapping("/clear-purchased")
    public Result<Void> clearPurchased() {
        shoppingListService.clearPurchased(1L);
        return Result.success();
    }

    @Operation(summary = "根据食谱生成采购清单")
    @PostMapping("/generate")
    public Result<List<SysShoppingList>> generate(@RequestBody Map<String, List<Long>> body) {
        List<Long> recipeIds = body.get("recipeIds");
        return Result.success(shoppingListService.listByIds(recipeIds));
    }
}

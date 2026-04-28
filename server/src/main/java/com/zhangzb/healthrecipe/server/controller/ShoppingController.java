package com.zhangzb.healthrecipe.server.controller;

import com.zhangzb.healthrecipe.server.config.Result;
import com.zhangzb.healthrecipe.server.dto.ShoppingListCreateDTO;
import com.zhangzb.healthrecipe.server.dto.ShoppingListUpdateDTO;
import com.zhangzb.healthrecipe.server.entity.RelRecipeIngredient;
import com.zhangzb.healthrecipe.server.entity.SysIngredient;
import com.zhangzb.healthrecipe.server.entity.SysInventory;
import com.zhangzb.healthrecipe.server.entity.SysShoppingList;
import com.zhangzb.healthrecipe.server.service.RelRecipeIngredientService;
import com.zhangzb.healthrecipe.server.service.SysIngredientService;
import com.zhangzb.healthrecipe.server.service.SysInventoryService;
import com.zhangzb.healthrecipe.server.service.SysShoppingListService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Tag(name = "采购清单管理", description = "采购清单的增删改查、批量操作及清空已购")
@RestController
@RequestMapping("/api/shopping")
public class ShoppingController {

    @Autowired
    private SysShoppingListService shoppingListService;

    @Autowired
    private RelRecipeIngredientService relRecipeIngredientService;

    @Autowired
    private SysIngredientService ingredientService;

    @Autowired
    private SysInventoryService inventoryService;

    private Long currentUserId() {
        return 1L;
    }

    @Operation(summary = "获取采购清单")
    @GetMapping("/list")
    public Result<List<SysShoppingList>> list() {
        return Result.success(shoppingListService.listByUserId(currentUserId()));
    }

    @Operation(summary = "添加采购项")
    @PostMapping("/add")
    public Result<SysShoppingList> add(@Valid @RequestBody ShoppingListCreateDTO dto) {
        SysShoppingList item = new SysShoppingList();
        item.setUserId(currentUserId());
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
            item.setUserId(currentUserId());
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
        shoppingListService.clearPurchased(currentUserId());
        return Result.success();
    }

    @Operation(summary = "根据食谱生成采购清单（含智能去重）",
               description = "遍历所选食谱的所有关联食材，合并相同食材用量，再比对家庭库存自动剔除或扣减已有食材")
    @PostMapping("/generate")
    public Result<List<SysShoppingList>> generate(@RequestBody Map<String, List<Long>> body) {
        List<Long> recipeIds = body.get("recipeIds");
        if (recipeIds == null || recipeIds.isEmpty()) {
            return Result.error(400, "请至少选择一个食谱");
        }

        Long userId = currentUserId();

        // 1. 查询所有选中食谱的食材关联
        List<RelRecipeIngredient> rels = relRecipeIngredientService.listByRecipeIds(recipeIds);
        if (rels.isEmpty()) {
            return Result.error(400, "所选食谱暂无食材数据");
        }

        // 2. 获取食材ID到名称的映射
        Set<Long> ingredientIds = rels.stream().map(RelRecipeIngredient::getIngredientId).collect(Collectors.toSet());
        Map<Long, SysIngredient> ingredientMap = ingredientService.listByIds(ingredientIds)
                .stream().collect(Collectors.toMap(SysIngredient::getId, ing -> ing));

        // 3. 合并相同食材的用量（按 ingredientId + unit 分组）
        Map<String, ShoppingItem> merged = new LinkedHashMap<>();
        for (RelRecipeIngredient rel : rels) {
            String key = rel.getIngredientId() + "::" + (rel.getUnit() != null ? rel.getUnit() : "");
            merged.merge(key, new ShoppingItem(rel.getIngredientId(), rel.getAmount(), rel.getUnit(), ingredientMap.get(rel.getIngredientId())),
                    (a, b) -> {
                        a.quantity = a.quantity.add(b.quantity != null ? b.quantity : BigDecimal.ZERO);
                        return a;
                    });
        }

        // 4. 查询用户库存，执行智能去重
        List<SysInventory> inventoryList = inventoryService.listByUserId(userId);
        Map<Long, BigDecimal> inventoryMap = new HashMap<>();
        for (SysInventory inv : inventoryList) {
            inventoryMap.merge(inv.getIngredientId(), inv.getQuantity(), BigDecimal::add);
        }

        // 5. 生成最终采购清单
        List<SysShoppingList> result = new ArrayList<>();
        for (ShoppingItem item : merged.values()) {
            String ingredientName = item.ingredient != null ? item.ingredient.getName() : ("食材_" + item.ingredientId);
            BigDecimal needed = item.quantity != null ? item.quantity : BigDecimal.ZERO;
            BigDecimal stock = inventoryMap.getOrDefault(item.ingredientId, BigDecimal.ZERO);

            BigDecimal toBuy = needed.subtract(stock);
            if (toBuy.compareTo(BigDecimal.ZERO) <= 0) {
                continue;
            }

            SysShoppingList shoppingItem = new SysShoppingList();
            shoppingItem.setUserId(userId);
            shoppingItem.setIngredientName(ingredientName);
            shoppingItem.setQuantity(toBuy);
            shoppingItem.setUnit(item.unit);
            shoppingItem.setStatus(0);
            shoppingItem.setCreateTime(LocalDateTime.now());
            shoppingItem.setUpdateTime(LocalDateTime.now());
            result.add(shoppingItem);
        }

        if (!result.isEmpty()) {
            shoppingListService.saveBatch(result);
        }

        return Result.success(result);
    }

    private static class ShoppingItem {
        Long ingredientId;
        BigDecimal quantity;
        String unit;
        SysIngredient ingredient;

        ShoppingItem(Long ingredientId, BigDecimal quantity, String unit, SysIngredient ingredient) {
            this.ingredientId = ingredientId;
            this.quantity = quantity != null ? quantity : BigDecimal.ZERO;
            this.unit = unit;
            this.ingredient = ingredient;
        }
    }
}

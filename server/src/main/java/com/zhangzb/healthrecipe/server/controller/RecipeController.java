package com.zhangzb.healthrecipe.server.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.zhangzb.healthrecipe.server.config.Result;
import com.zhangzb.healthrecipe.server.config.SecurityUtil;
import com.zhangzb.healthrecipe.server.dto.RecipeCreateDTO;
import com.zhangzb.healthrecipe.server.dto.RecipeQueryDTO;
import com.zhangzb.healthrecipe.server.dto.RecipeVO;
import com.zhangzb.healthrecipe.server.entity.RelRecipeIngredient;
import com.zhangzb.healthrecipe.server.entity.SysRecipe;
import com.zhangzb.healthrecipe.server.entity.SysShoppingList;
import com.zhangzb.healthrecipe.server.entity.SysIngredient;
import com.zhangzb.healthrecipe.server.service.RelRecipeIngredientService;
import com.zhangzb.healthrecipe.server.service.SysIngredientService;
import com.zhangzb.healthrecipe.server.service.SysRecipeService;
import com.zhangzb.healthrecipe.server.service.SysShoppingListService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Tag(name = "食谱管理", description = "食谱的增删改查、推荐及加入采购清单")
@RestController
@RequestMapping("/api/recipe")
public class RecipeController {

    @Autowired
    private SysRecipeService recipeService;

    @Autowired
    private RelRecipeIngredientService relRecipeIngredientService;

    @Autowired
    private SysShoppingListService shoppingListService;

    @Autowired
    private SysIngredientService ingredientService;

    @Operation(summary = "获取食谱列表", description = "根据关键词搜索食谱")
    @GetMapping("/list")
    public Result<List<RecipeVO>> list(@Valid RecipeQueryDTO params) {
        List<SysRecipe> list = recipeService.searchByKeyword(params.getKeyword());
        return Result.success(toVOList(list));
    }

    @Operation(summary = "推荐食谱", description = "根据体质和过敏原推荐食谱")
    @GetMapping("/recommend")
    public Result<List<RecipeVO>> recommend(@Valid RecipeQueryDTO params) {
        List<SysRecipe> list = recipeService.recommendByBodyType(params.getBodyType(), params.getAllergens());
        return Result.success(toVOList(list));
    }

    @Operation(summary = "获取食谱详情")
    @GetMapping("/{id}")
    public Result<RecipeVO> getById(@Parameter(description = "食谱ID") @PathVariable Long id) {
        SysRecipe recipe = recipeService.getById(id);
        if (recipe == null) {
            return Result.error(404, "食谱不存在");
        }
        return Result.success(toVO(recipe));
    }

    @Operation(summary = "创建食谱")
    @PostMapping("/create")
    public Result<RecipeVO> create(@Valid @RequestBody RecipeCreateDTO dto) {
        SysRecipe recipe = new SysRecipe();
        recipe.setName(dto.getName());
        recipe.setCoverImg(dto.getCoverImg());
        recipe.setCookTime(dto.getCookTime());
        recipe.setDifficulty(dto.getDifficulty());
        recipe.setSteps(dto.getSteps());
        recipe.setCalories(dto.getCalories());
        recipe.setDescription(dto.getDescription());
        recipeService.save(recipe);
        return Result.success(toVO(recipe));
    }

    @Operation(summary = "更新食谱")
    @PutMapping("/{id}")
    public Result<RecipeVO> update(@Parameter(description = "食谱ID") @PathVariable Long id, @Valid @RequestBody RecipeCreateDTO dto) {
        SysRecipe recipe = recipeService.getById(id);
        if (recipe == null) {
            return Result.error(404, "食谱不存在");
        }
        if (dto.getName() != null) recipe.setName(dto.getName());
        if (dto.getCoverImg() != null) recipe.setCoverImg(dto.getCoverImg());
        if (dto.getCookTime() != null) recipe.setCookTime(dto.getCookTime());
        if (dto.getDifficulty() != null) recipe.setDifficulty(dto.getDifficulty());
        if (dto.getSteps() != null) recipe.setSteps(dto.getSteps());
        if (dto.getCalories() != null) recipe.setCalories(dto.getCalories());
        if (dto.getDescription() != null) recipe.setDescription(dto.getDescription());
        recipeService.updateById(recipe);
        return Result.success(toVO(recipe));
    }

    @Operation(summary = "删除食谱")
    @DeleteMapping("/{id}")
    public Result<Void> remove(@Parameter(description = "食谱ID") @PathVariable Long id) {
        if (!recipeService.removeById(id)) {
            return Result.error(404, "食谱不存在");
        }
        return Result.success();
    }

    @Operation(summary = "将食谱加入采购清单", description = "根据食谱关联的食材生成采购项")
    @PostMapping("/{id}/add-to-shopping-list")
    public Result<Void> addToShoppingList(@Parameter(description = "食谱ID") @PathVariable Long id) {
        SysRecipe recipe = recipeService.getById(id);
        if (recipe == null) {
            return Result.error(404, "食谱不存在");
        }

        List<RelRecipeIngredient> ingredients = relRecipeIngredientService.list(
                new LambdaQueryWrapper<RelRecipeIngredient>().eq(RelRecipeIngredient::getRecipeId, id)
        );

        for (RelRecipeIngredient ri : ingredients) {
            SysShoppingList item = new SysShoppingList();
            item.setUserId(SecurityUtil.getCurrentUserId());
            SysIngredient ingredient = ingredientService.getById(ri.getIngredientId());
            item.setIngredientName(ingredient != null ? ingredient.getName() : ri.getIngredientId().toString());
            item.setQuantity(ri.getAmount());
            item.setUnit(ri.getUnit());
            item.setStatus(0);
            shoppingListService.save(item);
        }

        return Result.success();
    }

    private RecipeVO toVO(SysRecipe recipe) {
        RecipeVO vo = new RecipeVO();
        vo.setId(recipe.getId());
        vo.setName(recipe.getName());
        vo.setCoverImg(recipe.getCoverImg());
        vo.setCookTime(recipe.getCookTime());
        vo.setDifficulty(recipe.getDifficulty());
        vo.setSteps(recipe.getSteps());
        vo.setCalories(recipe.getCalories());
        vo.setDescription(recipe.getDescription());
        vo.setTags(new ArrayList<>());
        return vo;
    }

    private List<RecipeVO> toVOList(List<SysRecipe> list) {
        return list.stream().map(this::toVO).collect(Collectors.toList());
    }
}

package com.zhangzb.healthrecipe.server.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.zhangzb.healthrecipe.server.config.Result;
import com.zhangzb.healthrecipe.server.dto.RecipeCreateDTO;
import com.zhangzb.healthrecipe.server.dto.RecipeQueryDTO;
import com.zhangzb.healthrecipe.server.dto.RecipeVO;
import com.zhangzb.healthrecipe.server.entity.RelRecipeIngredient;
import com.zhangzb.healthrecipe.server.entity.SysRecipe;
import com.zhangzb.healthrecipe.server.entity.SysShoppingList;
import com.zhangzb.healthrecipe.server.service.RelRecipeIngredientService;
import com.zhangzb.healthrecipe.server.service.SysRecipeService;
import com.zhangzb.healthrecipe.server.service.SysShoppingListService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/recipe")
public class RecipeController {

    @Autowired
    private SysRecipeService recipeService;

    @Autowired
    private RelRecipeIngredientService relRecipeIngredientService;

    @Autowired
    private SysShoppingListService shoppingListService;

    @GetMapping("/list")
    public Result<List<RecipeVO>> list(RecipeQueryDTO params) {
        LambdaQueryWrapper<SysRecipe> wrapper = new LambdaQueryWrapper<>();
        if (params.getKeyword() != null && !params.getKeyword().isEmpty()) {
            wrapper.like(SysRecipe::getName, params.getKeyword());
        }
        wrapper.orderByDesc(SysRecipe::getCreateTime);
        List<SysRecipe> list = recipeService.list(wrapper);
        return Result.success(toVOList(list));
    }

    @GetMapping("/recommend")
    public Result<List<RecipeVO>> recommend(RecipeQueryDTO params) {
        LambdaQueryWrapper<SysRecipe> wrapper = new LambdaQueryWrapper<>();
        if (params.getKeyword() != null && !params.getKeyword().isEmpty()) {
            wrapper.like(SysRecipe::getName, params.getKeyword());
        }
        wrapper.orderByDesc(SysRecipe::getCreateTime);
        List<SysRecipe> list = recipeService.list(wrapper);
        return Result.success(toVOList(list));
    }

    @GetMapping("/{id}")
    public Result<RecipeVO> getById(@PathVariable Long id) {
        SysRecipe recipe = recipeService.getById(id);
        if (recipe == null) {
            return Result.error(404, "食谱不存在");
        }
        return Result.success(toVO(recipe));
    }

    @PostMapping("/create")
    public Result<RecipeVO> create(@RequestBody RecipeCreateDTO dto) {
        SysRecipe recipe = new SysRecipe();
        recipe.setName(dto.getName());
        recipe.setCoverImg(dto.getCoverImg());
        recipe.setCookTime(dto.getCookTime());
        recipe.setDifficulty(dto.getDifficulty());
        recipe.setSteps(dto.getSteps());
        recipeService.save(recipe);
        return Result.success(toVO(recipe));
    }

    @PutMapping("/{id}")
    public Result<RecipeVO> update(@PathVariable Long id, @RequestBody RecipeCreateDTO dto) {
        SysRecipe recipe = recipeService.getById(id);
        if (recipe == null) {
            return Result.error(404, "食谱不存在");
        }
        if (dto.getName() != null) recipe.setName(dto.getName());
        if (dto.getCoverImg() != null) recipe.setCoverImg(dto.getCoverImg());
        if (dto.getCookTime() != null) recipe.setCookTime(dto.getCookTime());
        if (dto.getDifficulty() != null) recipe.setDifficulty(dto.getDifficulty());
        if (dto.getSteps() != null) recipe.setSteps(dto.getSteps());
        recipeService.updateById(recipe);
        return Result.success(toVO(recipe));
    }

    @DeleteMapping("/{id}")
    public Result<Void> remove(@PathVariable Long id) {
        if (!recipeService.removeById(id)) {
            return Result.error(404, "食谱不存在");
        }
        return Result.success();
    }

    @PostMapping("/{id}/add-to-shopping-list")
    public Result<Void> addToShoppingList(@PathVariable Long id) {
        SysRecipe recipe = recipeService.getById(id);
        if (recipe == null) {
            return Result.error(404, "食谱不存在");
        }

        List<RelRecipeIngredient> ingredients = relRecipeIngredientService.list(
                new LambdaQueryWrapper<RelRecipeIngredient>().eq(RelRecipeIngredient::getRecipeId, id)
        );

        for (RelRecipeIngredient ri : ingredients) {
            SysShoppingList item = new SysShoppingList();
            item.setUserId(1L);
            item.setIngredientName(ri.getIngredientId().toString());
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
        vo.setTags(new ArrayList<>());
        vo.setDescription("");
        vo.setCalories(0);
        return vo;
    }

    private List<RecipeVO> toVOList(List<SysRecipe> list) {
        return list.stream().map(this::toVO).collect(Collectors.toList());
    }
}

package com.zhangzb.healthrecipe.server.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zhangzb.healthrecipe.server.entity.RelRecipeIngredient;
import com.zhangzb.healthrecipe.server.entity.SysIngredient;
import com.zhangzb.healthrecipe.server.entity.SysRecipe;
import com.zhangzb.healthrecipe.server.mapper.SysRecipeMapper;
import com.zhangzb.healthrecipe.server.service.RelRecipeIngredientService;
import com.zhangzb.healthrecipe.server.service.SysIngredientService;
import com.zhangzb.healthrecipe.server.service.SysRecipeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class SysRecipeServiceImpl extends ServiceImpl<SysRecipeMapper, SysRecipe> implements SysRecipeService {

    @Autowired
    private RelRecipeIngredientService relRecipeIngredientService;

    @Autowired
    private SysIngredientService ingredientService;

    @Override
    public List<SysRecipe> searchByKeyword(String keyword) {
        if (keyword == null || keyword.isBlank()) {
            return list();
        }
        return list(new LambdaQueryWrapper<SysRecipe>()
                .like(SysRecipe::getName, keyword)
                .or()
                .like(SysRecipe::getSteps, keyword));
    }

    @Override
    public List<SysRecipe> recommendByBodyType(String bodyType, List<String> allergens) {
        LambdaQueryWrapper<SysRecipe> wrapper = new LambdaQueryWrapper<>();

        if (bodyType != null && !bodyType.isBlank()) {
            String descPattern = getBodyTypeDescriptionPattern(bodyType);
            wrapper.like(SysRecipe::getDescription, descPattern);
        }

        wrapper.orderByDesc(SysRecipe::getCalories);
        wrapper.last("LIMIT 50");
        List<SysRecipe> candidates = list(wrapper);

        if (candidates.isEmpty()) {
            return Collections.emptyList();
        }

        if (allergens == null || allergens.isEmpty()) {
            return candidates.size() > 20 ? candidates.subList(0, 20) : candidates;
        }

        Set<Long> allergenIngredientIds = resolveAllergenIngredientIds(allergens);
        if (allergenIngredientIds.isEmpty()) {
            return candidates.size() > 20 ? candidates.subList(0, 20) : candidates;
        }

        List<Long> recipeIds = candidates.stream().map(SysRecipe::getId).collect(Collectors.toList());
        List<RelRecipeIngredient> rels = relRecipeIngredientService.listByRecipeIds(recipeIds);

        Map<Long, List<Long>> recipeIngredientMap = rels.stream()
                .collect(Collectors.groupingBy(
                        RelRecipeIngredient::getRecipeId,
                        Collectors.mapping(RelRecipeIngredient::getIngredientId, Collectors.toList())
                ));

        List<SysRecipe> filtered = candidates.stream()
                .filter(r -> {
                    List<Long> ingredientIds = recipeIngredientMap.get(r.getId());
                    if (ingredientIds == null || ingredientIds.isEmpty()) {
                        return true;
                    }
                    return ingredientIds.stream().noneMatch(allergenIngredientIds::contains);
                })
                .limit(20)
                .collect(Collectors.toList());

        return filtered;
    }

    private String getBodyTypeDescriptionPattern(String bodyType) {
        switch (bodyType) {
            case "阳虚":
                return "温补";
            case "阴虚":
                return "滋阴";
            case "气虚":
                return "补气";
            case "痰湿":
                return "祛湿";
            case "湿热":
                return "清热";
            case "血瘀":
                return "活血";
            case "气郁":
                return "理气";
            case "特禀":
                return "益气";
            default:
                return bodyType;
        }
    }

    private Set<Long> resolveAllergenIngredientIds(List<String> allergens) {
        List<SysIngredient> ingredients = ingredientService.list(
                new LambdaQueryWrapper<SysIngredient>().in(SysIngredient::getName, allergens)
        );
        return ingredients.stream().map(SysIngredient::getId).collect(Collectors.toSet());
    }

    @Override
    public SysRecipe getRecipeDetail(Long id) {
        return getById(id);
    }
}

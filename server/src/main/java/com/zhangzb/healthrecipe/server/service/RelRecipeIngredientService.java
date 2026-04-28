package com.zhangzb.healthrecipe.server.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.zhangzb.healthrecipe.server.entity.RelRecipeIngredient;

import java.util.List;

public interface RelRecipeIngredientService extends IService<RelRecipeIngredient> {
    List<RelRecipeIngredient> listByRecipeIds(List<Long> recipeIds);
}

package com.zhangzb.healthrecipe.server.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.zhangzb.healthrecipe.server.entity.SysRecipe;

import java.util.List;

public interface SysRecipeService extends IService<SysRecipe> {
    List<SysRecipe> searchByKeyword(String keyword);

    List<SysRecipe> recommendByBodyType(String bodyType, List<String> allergens);
}

package com.zhangzb.healthrecipe.server.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.zhangzb.healthrecipe.server.entity.SysIngredient;

import java.util.List;

public interface SysIngredientService extends IService<SysIngredient> {
    SysIngredient findByName(String name);

    SysIngredient getOrCreate(String name, String category, String unit);

    List<SysIngredient> listByCategory(String category);
}

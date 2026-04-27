package com.zhangzb.healthrecipe.server.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zhangzb.healthrecipe.server.entity.SysIngredient;
import com.zhangzb.healthrecipe.server.mapper.SysIngredientMapper;
import com.zhangzb.healthrecipe.server.service.SysIngredientService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SysIngredientServiceImpl extends ServiceImpl<SysIngredientMapper, SysIngredient> implements SysIngredientService {

    @Override
    public SysIngredient findByName(String name) {
        return getOne(new LambdaQueryWrapper<SysIngredient>().eq(SysIngredient::getName, name));
    }

    @Override
    public SysIngredient getOrCreate(String name, String category, String unit) {
        SysIngredient existing = findByName(name);
        if (existing != null) {
            return existing;
        }
        SysIngredient ingredient = new SysIngredient();
        ingredient.setName(name);
        ingredient.setCategory(category);
        ingredient.setUnit(unit != null ? unit : "g");
        save(ingredient);
        return ingredient;
    }

    @Override
    public List<SysIngredient> listByCategory(String category) {
        return list(new LambdaQueryWrapper<SysIngredient>().eq(SysIngredient::getCategory, category));
    }
}

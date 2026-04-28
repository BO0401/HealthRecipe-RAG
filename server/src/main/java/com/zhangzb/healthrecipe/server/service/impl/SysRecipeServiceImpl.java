package com.zhangzb.healthrecipe.server.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zhangzb.healthrecipe.server.entity.SysRecipe;
import com.zhangzb.healthrecipe.server.mapper.SysRecipeMapper;
import com.zhangzb.healthrecipe.server.service.SysRecipeService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SysRecipeServiceImpl extends ServiceImpl<SysRecipeMapper, SysRecipe> implements SysRecipeService {

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
            wrapper.like(SysRecipe::getSteps, bodyType);
        }
        wrapper.last("LIMIT 20");
        return list(wrapper);
    }

    @Override
    public SysRecipe getRecipeDetail(Long id) {
        return getById(id);
    }
}

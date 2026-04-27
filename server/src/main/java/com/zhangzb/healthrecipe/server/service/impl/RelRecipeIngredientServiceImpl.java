package com.zhangzb.healthrecipe.server.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zhangzb.healthrecipe.server.entity.RelRecipeIngredient;
import com.zhangzb.healthrecipe.server.mapper.RelRecipeIngredientMapper;
import com.zhangzb.healthrecipe.server.service.RelRecipeIngredientService;
import org.springframework.stereotype.Service;

@Service
public class RelRecipeIngredientServiceImpl extends ServiceImpl<RelRecipeIngredientMapper, RelRecipeIngredient> implements RelRecipeIngredientService {
}

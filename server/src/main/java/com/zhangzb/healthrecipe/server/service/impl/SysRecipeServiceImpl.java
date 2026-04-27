package com.zhangzb.healthrecipe.server.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zhangzb.healthrecipe.server.entity.SysRecipe;
import com.zhangzb.healthrecipe.server.mapper.SysRecipeMapper;
import com.zhangzb.healthrecipe.server.service.SysRecipeService;
import org.springframework.stereotype.Service;

@Service
public class SysRecipeServiceImpl extends ServiceImpl<SysRecipeMapper, SysRecipe> implements SysRecipeService {
}

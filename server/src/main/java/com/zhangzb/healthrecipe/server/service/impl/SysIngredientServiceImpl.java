package com.zhangzb.healthrecipe.server.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zhangzb.healthrecipe.server.entity.SysIngredient;
import com.zhangzb.healthrecipe.server.mapper.SysIngredientMapper;
import com.zhangzb.healthrecipe.server.service.SysIngredientService;
import org.springframework.stereotype.Service;

@Service
public class SysIngredientServiceImpl extends ServiceImpl<SysIngredientMapper, SysIngredient> implements SysIngredientService {
}

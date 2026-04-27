package com.zhangzb.healthrecipe.server.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zhangzb.healthrecipe.server.entity.SysShoppingList;
import com.zhangzb.healthrecipe.server.mapper.SysShoppingListMapper;
import com.zhangzb.healthrecipe.server.service.SysShoppingListService;
import org.springframework.stereotype.Service;

@Service
public class SysShoppingListServiceImpl extends ServiceImpl<SysShoppingListMapper, SysShoppingList> implements SysShoppingListService {
}

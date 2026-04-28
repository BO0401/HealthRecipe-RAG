package com.zhangzb.healthrecipe.server.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zhangzb.healthrecipe.server.entity.SysShoppingList;
import com.zhangzb.healthrecipe.server.mapper.SysShoppingListMapper;
import com.zhangzb.healthrecipe.server.service.SysShoppingListService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SysShoppingListServiceImpl extends ServiceImpl<SysShoppingListMapper, SysShoppingList> implements SysShoppingListService {

    @Override
    public List<SysShoppingList> listByUserId(Long userId) {
        return list(new LambdaQueryWrapper<SysShoppingList>()
                .eq(SysShoppingList::getUserId, userId)
                .orderByDesc(SysShoppingList::getCreateTime));
    }

    @Override
    public List<SysShoppingList> listByStatus(Long userId, Integer status) {
        return list(new LambdaQueryWrapper<SysShoppingList>()
                .eq(SysShoppingList::getUserId, userId)
                .eq(SysShoppingList::getStatus, status)
                .orderByDesc(SysShoppingList::getCreateTime));
    }

    @Override
    public long countPending(Long userId) {
        return count(new LambdaQueryWrapper<SysShoppingList>()
                .eq(SysShoppingList::getUserId, userId)
                .eq(SysShoppingList::getStatus, 0));
    }

    @Override
    public void clearPurchased(Long userId) {
        remove(new LambdaQueryWrapper<SysShoppingList>()
                .eq(SysShoppingList::getUserId, userId)
                .eq(SysShoppingList::getStatus, 1));
    }
}

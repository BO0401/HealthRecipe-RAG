package com.zhangzb.healthrecipe.server.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.zhangzb.healthrecipe.server.entity.SysShoppingList;

import java.util.List;

public interface SysShoppingListService extends IService<SysShoppingList> {
    List<SysShoppingList> listByUserId(Long userId);

    List<SysShoppingList> listByStatus(Long userId, Integer status);

    long countPending(Long userId);

    void clearPurchased(Long userId);
}

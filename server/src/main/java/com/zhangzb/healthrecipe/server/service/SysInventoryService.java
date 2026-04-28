package com.zhangzb.healthrecipe.server.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.zhangzb.healthrecipe.server.entity.SysInventory;

import java.util.List;

public interface SysInventoryService extends IService<SysInventory> {
    List<SysInventory> listByUserId(Long userId);

    List<SysInventory> findExpiringSoon(Long userId, int days);

    long countByUserId(Long userId);
}

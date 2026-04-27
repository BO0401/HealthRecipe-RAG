package com.zhangzb.healthrecipe.server.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zhangzb.healthrecipe.server.entity.SysInventory;
import com.zhangzb.healthrecipe.server.mapper.SysInventoryMapper;
import com.zhangzb.healthrecipe.server.service.SysInventoryService;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class SysInventoryServiceImpl extends ServiceImpl<SysInventoryMapper, SysInventory> implements SysInventoryService {

    @Override
    public List<SysInventory> listByUserId(Long userId) {
        return list(new LambdaQueryWrapper<SysInventory>()
                .eq(SysInventory::getUserId, userId)
                .orderByDesc(SysInventory::getCreateTime));
    }

    @Override
    public List<SysInventory> findExpiringSoon(Long userId, int days) {
        LocalDate threshold = LocalDate.now().plusDays(days);
        return list(new LambdaQueryWrapper<SysInventory>()
                .eq(SysInventory::getUserId, userId)
                .isNotNull(SysInventory::getExpireDate)
                .le(SysInventory::getExpireDate, threshold)
                .orderByAsc(SysInventory::getExpireDate));
    }

    @Override
    public long countByUserId(Long userId) {
        return count(new LambdaQueryWrapper<SysInventory>().eq(SysInventory::getUserId, userId));
    }
}

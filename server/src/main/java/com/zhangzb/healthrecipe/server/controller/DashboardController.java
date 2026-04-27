package com.zhangzb.healthrecipe.server.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.zhangzb.healthrecipe.server.config.Result;
import com.zhangzb.healthrecipe.server.dto.DashboardMetricsVO;
import com.zhangzb.healthrecipe.server.entity.SysInventory;
import com.zhangzb.healthrecipe.server.entity.SysShoppingList;
import com.zhangzb.healthrecipe.server.service.SysInventoryService;
import com.zhangzb.healthrecipe.server.service.SysShoppingListService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

@RestController
@RequestMapping("/api/dashboard")
public class DashboardController {

    @Autowired
    private SysInventoryService inventoryService;

    @Autowired
    private SysShoppingListService shoppingListService;

    @GetMapping("/metrics")
    public Result<DashboardMetricsVO> getMetrics() {
        long inventoryCount = inventoryService.count();
        long expiringCount = inventoryService.count(
                new LambdaQueryWrapper<SysInventory>()
                        .isNotNull(SysInventory::getExpireDate)
                        .le(SysInventory::getExpireDate, LocalDate.now().plus(3, ChronoUnit.DAYS))
                        .ge(SysInventory::getExpireDate, LocalDate.now())
        );
        long pendingTasks = shoppingListService.count(
                new LambdaQueryWrapper<SysShoppingList>().eq(SysShoppingList::getStatus, 0)
        );

        DashboardMetricsVO metrics = new DashboardMetricsVO(
                0, inventoryCount, expiringCount, pendingTasks, 85
        );

        return Result.success(new DashboardMetricsVO(0, inventoryCount, expiringCount, pendingTasks, 85));
    }
}

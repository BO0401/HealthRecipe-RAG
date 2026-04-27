package com.zhangzb.healthrecipe.server.controller;

import com.zhangzb.healthrecipe.server.config.Result;
import com.zhangzb.healthrecipe.server.dto.DashboardMetricsVO;
import com.zhangzb.healthrecipe.server.service.SysInventoryService;
import com.zhangzb.healthrecipe.server.service.SysShoppingListService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "仪表盘", description = "首页仪表盘数据指标")
@RestController
@RequestMapping("/api/dashboard")
public class DashboardController {

    @Autowired
    private SysInventoryService inventoryService;

    @Autowired
    private SysShoppingListService shoppingListService;

    @Operation(summary = "获取仪表盘指标", description = "返回今日热量、剩余食材、过期数量、待办任务、健康评分")
    @GetMapping("/metrics")
    public Result<DashboardMetricsVO> getMetrics() {
        Long userId = getCurrentUserId();

        long inventoryCount = inventoryService.countByUserId(userId);
        long expiringCount = inventoryService.findExpiringSoon(userId, 3).size();
        long pendingTasks = shoppingListService.countPending(userId);

        DashboardMetricsVO metrics = new DashboardMetricsVO(
                0, inventoryCount, expiringCount, pendingTasks, 85
        );

        return Result.success(metrics);
    }

    private Long getCurrentUserId() {
        return 1L;
    }
}

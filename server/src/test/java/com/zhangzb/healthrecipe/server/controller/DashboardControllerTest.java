package com.zhangzb.healthrecipe.server.controller;

import com.zhangzb.healthrecipe.server.config.Result;
import com.zhangzb.healthrecipe.server.dto.DashboardMetricsVO;
import com.zhangzb.healthrecipe.server.service.SysInventoryService;
import com.zhangzb.healthrecipe.server.service.SysShoppingListService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DashboardControllerTest {

    @Mock
    private SysInventoryService inventoryService;

    @Mock
    private SysShoppingListService shoppingListService;

    @InjectMocks
    private DashboardController dashboardController;

    @Test
    void getMetrics_shouldReturnAllMetrics() {
        when(inventoryService.countByUserId(1L)).thenReturn(10L);
        when(inventoryService.findExpiringSoon(1L, 3)).thenReturn(List.of());
        when(shoppingListService.countPending(1L)).thenReturn(3L);

        Result<DashboardMetricsVO> result = dashboardController.getMetrics();

        assertTrue(result.getCode() == 200);
        DashboardMetricsVO metrics = result.getData();
        assertEquals(0, metrics.getTodayCalories());
        assertEquals(10, metrics.getRemainingIngredients());
        assertEquals(0, metrics.getExpiringCount());
        assertEquals(3, metrics.getPendingTasks());
        assertEquals(85, metrics.getHealthScore());
    }
}

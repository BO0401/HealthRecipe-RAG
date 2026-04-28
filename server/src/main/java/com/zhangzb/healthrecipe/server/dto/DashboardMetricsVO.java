package com.zhangzb.healthrecipe.server.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class DashboardMetricsVO {
    private long todayCalories;
    private long remainingIngredients;
    private long expiringCount;
    private long pendingTasks;
    private long healthScore;
}

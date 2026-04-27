import http from './http'

export interface DashboardMetricsVO {
  todayCalories: number
  remainingIngredients: number
  expiringCount: number
  pendingTasks: number
  healthScore: number
}

export interface DashboardData {
  metrics: DashboardMetricsVO
}

export const dashboardApi = {
  getMetrics: () =>
    http.get<DashboardData>('/dashboard/metrics')
}

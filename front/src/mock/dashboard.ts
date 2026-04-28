import type { DashboardMetricsVO } from '../types/api'

export type DashboardAlertType = 'primary' | 'success' | 'warning' | 'danger' | 'info'

export interface DashboardAlertItem {
  id: string
  title: string
  dateText: string
  type: DashboardAlertType
  tag: string
}

export const mockDashboard = {
  getMetrics: async (): Promise<DashboardMetricsVO> => ({
    todayCalories: 1860,
    remainingIngredients: 12,
    expiringCount: 2,
    pendingTasks: 1,
    healthScore: 87
  }),

  getAlerts: async (): Promise<DashboardAlertItem[]> => ([
    { id: 'a1', title: '全脂牛奶即将过期', dateText: '还剩 1 天', type: 'danger', tag: '库存预警' },
    { id: 'a2', title: '本周蛋白质摄入不足', dateText: '建议摄入 350g，实际 210g', type: 'warning', tag: '营养预警' },
    { id: 'a3', title: '冰箱库存：鸡蛋仅剩 2 枚', dateText: '建议补充', type: 'warning', tag: '采购提醒' },
    { id: 'a4', title: '推荐食谱：番茄牛腩', dateText: '今日推荐 · 高蛋白', type: 'primary', tag: '智能食谱' },
    { id: 'a5', title: '膳食纤维摄入偏低', dateText: '建议增加蔬菜水果', type: 'info', tag: '健康提示' }
  ])
}


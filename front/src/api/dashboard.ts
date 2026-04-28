import http from './http'
import { DashboardMetricsVOSchema, type DashboardMetricsVO } from '../types/api'
import { MOCK_ENABLED } from '../mock/enabled'
import { mockDashboard, type DashboardAlertItem } from '../mock/dashboard'

export const dashboardApi = {
  getMetrics: async (): Promise<DashboardMetricsVO> => {
    if (MOCK_ENABLED) {
      return DashboardMetricsVOSchema.parse(await mockDashboard.getMetrics())
    }
    const data = await http.get<DashboardMetricsVO>('/dashboard/metrics')
    return DashboardMetricsVOSchema.parse(data)
  },

  getAlerts: async (): Promise<DashboardAlertItem[]> => {
    if (MOCK_ENABLED) {
      return await mockDashboard.getAlerts()
    }
    try {
      return await http.get<DashboardAlertItem[]>('/dashboard/alerts')
    } catch (err: any) {
      const status = err?.response?.status
      if (status === 404) return []
      throw err
    }
  }
}

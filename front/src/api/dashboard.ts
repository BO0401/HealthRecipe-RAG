import http from './http'
import { DashboardMetricsVOSchema, type DashboardMetricsVO } from '../types/api'

export const dashboardApi = {
  getMetrics: async (): Promise<DashboardMetricsVO> => {
    const data = await http.get<DashboardMetricsVO>('/dashboard/metrics')
    return DashboardMetricsVOSchema.parse(data)
  }
}

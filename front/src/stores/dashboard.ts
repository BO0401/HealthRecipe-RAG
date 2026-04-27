import { defineStore } from 'pinia'
import { ref } from 'vue'
import { Timer, Refrigerator, List, Star } from '@element-plus/icons-vue'
import { dashboardApi } from '../api/dashboard'

export interface MetricItem {
  title: string
  value: string | number
  unit: string
  icon: any
  color: string
  trend: string
}

export const useDashboardStore = defineStore('dashboard', () => {
  const metrics = ref<MetricItem[]>([
    { title: '今日热量', value: '--', unit: 'kcal', icon: Timer, color: '#409EFF', trend: '加载中...' },
    { title: '剩余食材', value: '--', unit: '件', icon: Refrigerator, color: '#67C23A', trend: '加载中...' },
    { title: '待办事项', value: '--', unit: '项', icon: List, color: '#E6A23C', trend: '加载中...' },
    { title: '健康评分', value: '--', unit: '分', icon: Star, color: '#F56C6C', trend: '加载中...' }
  ])

  const updateMetrics = (newMetrics: MetricItem[]) => {
    metrics.value = newMetrics
  }

  const fetchMetrics = async () => {
    try {
      const data = await dashboardApi.getMetrics()

      metrics.value = [
        {
          title: '今日热量',
          value: data.todayCalories?.toLocaleString() ?? '--',
          unit: 'kcal',
          icon: Timer,
          color: '#409EFF',
          trend: data.todayCalories ? '实时数据' : '暂无数据'
        },
        {
          title: '剩余食材',
          value: data.remainingIngredients?.toString() ?? '--',
          unit: '件',
          icon: Refrigerator,
          color: '#67C23A',
          trend: data.expiringCount ? `${data.expiringCount}件即将过期` : '库存充足'
        },
        {
          title: '待办事项',
          value: data.pendingTasks?.toString() ?? '--',
          unit: '项',
          icon: List,
          color: '#E6A23C',
          trend: data.pendingTasks ? '有待处理事项' : '全部完成'
        },
        {
          title: '健康评分',
          value: data.healthScore?.toString() ?? '--',
          unit: '分',
          icon: Star,
          color: '#F56C6C',
          trend: data.healthScore ? '状态良好' : '暂无数据'
        }
      ]
    } catch (err) {
      console.error('[dashboard store] fetch error:', err)
    }
  }

  return {
    metrics,
    updateMetrics,
    fetchMetrics
  }
})

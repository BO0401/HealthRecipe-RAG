import { defineStore } from 'pinia'
import { ref, computed, markRaw } from 'vue'
import { Timer, Refrigerator, List, Star } from '@element-plus/icons-vue'
import { dashboardApi } from '../api/dashboard'
import type { DashboardAlertItem } from '../mock/dashboard'

export interface MetricItem {
  title: string
  value: string | number
  unit: string
  icon: any
  color: string
  trend: string
}

export interface NutritionData {
  carbsG: number
  proteinG: number
  fatG: number
}

export const useDashboardStore = defineStore('dashboard', () => {
  const metrics = ref<MetricItem[]>([
    { title: '今日热量', value: '--', unit: 'kcal', icon: markRaw(Timer), color: '#409EFF', trend: '加载中...' },
    { title: '剩余食材', value: '--', unit: '件', icon: markRaw(Refrigerator), color: '#67C23A', trend: '加载中...' },
    { title: '待办事项', value: '--', unit: '项', icon: markRaw(List), color: '#E6A23C', trend: '加载中...' },
    { title: '健康评分', value: '--', unit: '分', icon: markRaw(Star), color: '#F56C6C', trend: '加载中...' }
  ])

  const loading = ref(false)
  const error = ref('')
  const lastSyncAt = ref<Date | null>(null)

  const nutrition = ref<NutritionData>({ carbsG: 0, proteinG: 0, fatG: 0 })

  const alerts = ref<DashboardAlertItem[]>([])
  const alertsLoading = ref(false)
  const alertsError = ref('')

  const totalCalories = computed(() => {
    const metricsItem = metrics.value.find(m => m.title === '今日热量')
    if (!metricsItem || metricsItem.value === '--') return 0
    return Number(metricsItem.value) || 0
  })

  const updateMetrics = (newMetrics: MetricItem[]) => {
    metrics.value = newMetrics
  }

  const fetchMetrics = async () => {
    loading.value = true
    error.value = ''

    try {
      const data = await dashboardApi.getMetrics()

      metrics.value = [
        {
          title: '今日热量',
          value: data.todayCalories ?? '--',
          unit: 'kcal',
          icon: markRaw(Timer),
          color: '#409EFF',
          trend: data.todayCalories != null ? '实时数据' : '暂无数据'
        },
        {
          title: '剩余食材',
          value: data.remainingIngredients ?? '--',
          unit: '件',
          icon: markRaw(Refrigerator),
          color: '#67C23A',
          trend: data.expiringCount != null && data.expiringCount > 0 ? `${data.expiringCount}件即将过期` : '库存充足'
        },
        {
          title: '待办事项',
          value: data.pendingTasks ?? '--',
          unit: '项',
          icon: markRaw(List),
          color: '#E6A23C',
          trend: data.pendingTasks != null && data.pendingTasks > 0 ? '有待处理事项' : '全部完成'
        },
        {
          title: '健康评分',
          value: data.healthScore ?? '--',
          unit: '分',
          icon: markRaw(Star),
          color: '#F56C6C',
          trend: data.healthScore != null ? '状态良好' : '暂无数据'
        }
      ]

      if (data.todayCalories != null) {
        const kcal = data.todayCalories
        nutrition.value = {
          carbsG: Math.round(kcal * 0.5 / 4),
          proteinG: Math.round(kcal * 0.25 / 4),
          fatG: Math.round(kcal * 0.25 / 9)
        }
      }

      lastSyncAt.value = new Date()
    } catch (err) {
      error.value = '获取仪表盘数据失败'
      console.error('[dashboard store] fetch error:', err)
    } finally {
      loading.value = false
    }
  }

  const fetchAlerts = async () => {
    alertsLoading.value = true
    alertsError.value = ''
    try {
      alerts.value = await dashboardApi.getAlerts()
    } catch (err) {
      alertsError.value = '获取动态与预警失败'
      console.error('[dashboard store] fetch alerts error:', err)
    } finally {
      alertsLoading.value = false
    }
  }

  return {
    metrics,
    loading,
    error,
    lastSyncAt,
    nutrition,
    alerts,
    alertsLoading,
    alertsError,
    totalCalories,
    updateMetrics,
    fetchMetrics,
    fetchAlerts
  }
})

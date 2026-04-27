import { defineStore } from 'pinia'
import { ref } from 'vue'
import { Timer, Refrigerator, List, Star } from '@element-plus/icons-vue'

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
    { title: '今日热量', value: '1,240', unit: 'kcal', icon: Timer, color: '#409EFF', trend: '较昨日 -5%' },
    { title: '剩余食材', value: '12', unit: '件', icon: Refrigerator, color: '#67C23A', trend: '3件即将过期' },
    { title: '待办事项', value: '3', unit: '项', icon: List, color: '#E6A23C', trend: '包含 1 个高优先级' },
    { title: '健康评分', value: '88', unit: '分', icon: Star, color: '#F56C6C', trend: '状态极佳' }
  ])

  const updateMetrics = (newMetrics: MetricItem[]) => {
    metrics.value = newMetrics
  }

  // 模拟从 API 获取数据
  const fetchMetrics = async () => {
    // 模拟延迟
    await new Promise(resolve => setTimeout(resolve, 500))
    // 这里可以添加真实的 API 调用
    console.log('[dashboard store] metrics fetched')
  }

  return {
    metrics,
    updateMetrics,
    fetchMetrics
  }
})

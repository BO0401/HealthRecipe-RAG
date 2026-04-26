<script setup lang="ts">
import { reactive } from 'vue'
import { Timer, Refrigerator, List, Star } from '@element-plus/icons-vue'

const metrics = reactive([
  { title: '今日热量', value: '1,240', unit: 'kcal', icon: Timer, color: '#409EFF', trend: '较昨日 -5%' },
  { title: '剩余食材', value: '12', unit: '件', icon: Refrigerator, color: '#67C23A', trend: '3件即将过期' },
  { title: '待办事项', value: '3', unit: '项', icon: List, color: '#E6A23C', trend: '包含 1 个高优先级' },
  { title: '健康评分', value: '88', unit: '分', icon: Star, color: '#F56C6C', trend: '状态极佳' }
])

console.log('[dashboard] metric cards loaded', metrics.length)
</script>

<template>
  <div class="metric-grid">
    <div v-for="item in metrics" :key="item.title" class="metric-card">
      <div class="metric-content">
        <div class="metric-info">
          <div class="metric-title">{{ item.title }}</div>
          <div class="metric-value-wrap">
            <span class="value">{{ item.value }}</span>
            <span class="unit">{{ item.unit }}</span>
          </div>
          <div class="metric-trend">{{ item.trend }}</div>
        </div>
        <el-icon class="metric-icon" :style="{ color: item.color }" :size="28">
          <component :is="item.icon" />
        </el-icon>
      </div>
    </div>
  </div>
</template>

<style scoped>
.metric-grid {
  display: flex;
  gap: 8px;
  width: 100%;
}

.metric-card {
  flex: 1;
  background: #fff;
  border-radius: 6px;
  padding: 16px 18px;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.06);
}

.metric-content {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.metric-title {
  font-size: 14px;
  color: #606266;
  margin-bottom: 4px;
  font-weight: 500;
}

.metric-value-wrap {
  margin-bottom: 4px;
}

.metric-value-wrap .value {
  font-size: 28px;
  font-weight: bold;
  color: #1a1a2e;
}

.metric-value-wrap .unit {
  font-size: 13px;
  color: #606266;
  margin-left: 3px;
}

.metric-trend {
  font-size: 13px;
  color: #67C23A;
  font-weight: 500;
}

.metric-icon {
  opacity: 0.85;
}
</style>

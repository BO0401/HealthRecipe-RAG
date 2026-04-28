<script setup lang="ts">
import { onMounted } from 'vue'
import { useDashboardStore } from '../../stores/dashboard'

const dashboardStore = useDashboardStore()

onMounted(() => {
  dashboardStore.fetchMetrics()
})

console.log('[dashboard] metric cards loaded with Pinia store')
</script>

<template>
  <div class="metric-grid" v-loading="dashboardStore.loading">
    <div v-for="item in dashboardStore.metrics" :key="item.title" class="metric-card">
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
    <div v-if="dashboardStore.error" class="error-bar">
      <el-alert :title="dashboardStore.error" type="error" show-icon :closable="false" />
    </div>
  </div>
</template>

<style scoped>
.metric-grid {
  display: flex;
  gap: 8px;
  width: 100%;
  position: relative;
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

.error-bar {
  position: absolute;
  left: 0;
  right: 0;
  bottom: -44px;
}
</style>

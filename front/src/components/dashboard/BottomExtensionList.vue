<script setup lang="ts">
import { onMounted } from 'vue'
import { Bell, ArrowRight } from '@element-plus/icons-vue'
import { useDashboardStore } from '../../stores/dashboard'

const dashboardStore = useDashboardStore()

onMounted(() => {
  dashboardStore.fetchAlerts()
})
</script>

<template>
  <div class="ext-panel">
    <div class="ext-header">
      <span class="title">动态与预警</span>
      <el-button link type="primary" size="small">
        查看全部 <el-icon><ArrowRight /></el-icon>
      </el-button>
    </div>

    <div class="ext-list" v-loading="dashboardStore.alertsLoading">
      <el-result
        v-if="dashboardStore.alertsError"
        icon="error"
        title="加载失败"
        :sub-title="dashboardStore.alertsError"
      >
        <template #extra>
          <el-button type="primary" size="small" @click="dashboardStore.fetchAlerts()">重试</el-button>
        </template>
      </el-result>

      <el-empty
        v-else-if="dashboardStore.alerts.length === 0"
        description="暂无动态与预警"
        :image-size="80"
      />

      <div v-else v-for="item in dashboardStore.alerts" :key="item.id" class="ext-item">
        <div class="item-left">
          <el-icon :class="['item-icon', item.type]"><Bell /></el-icon>
          <div class="item-info">
            <div class="item-title">{{ item.title }}</div>
            <div class="item-date">{{ item.dateText }}</div>
          </div>
        </div>
        <el-tag :type="item.type" size="small" effect="plain">{{ item.tag }}</el-tag>
      </div>
    </div>
  </div>
</template>

<style scoped>
.ext-panel {
  background: #fff;
  border-radius: 6px;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.06);
  display: flex;
  flex-direction: column;
  height: 100%;
  overflow: hidden;
}

.ext-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 14px 18px 0;
  flex-shrink: 0;
}

.ext-header .title {
  font-weight: bold;
  font-size: 15px;
  color: #1a1a2e;
}

.ext-list {
  flex: 1;
  overflow-y: auto;
  padding: 0 18px;
  display: flex;
  flex-direction: column;
  justify-content: center;
}

.ext-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 9px 0;
  border-bottom: 1px solid #e5e7eb;
}

.ext-item:last-child {
  border-bottom: none;
}

.item-left {
  display: flex;
  align-items: center;
  gap: 10px;
}

.item-icon {
  font-size: 18px;
  padding: 7px;
  border-radius: 50%;
}

.item-icon.danger { background: #fef0f0; color: #f56c6c; }
.item-icon.primary { background: #ecf5ff; color: #409eff; }
.item-icon.warning { background: #fdf6ec; color: #e6a23c; }
.item-icon.info { background: #f4f4f5; color: #909399; }

.item-title {
  font-size: 14px;
  color: #1a1a2e;
  font-weight: 600;
}

.item-date {
  font-size: 12px;
  color: #909399;
  margin-top: 2px;
}
</style>

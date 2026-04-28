<script setup lang="ts">
import { computed } from 'vue'
import { useDashboardStore } from '../../stores/dashboard'

const dashboardStore = useDashboardStore()

const fmtTime = (d: Date) => {
  const hh = String(d.getHours()).padStart(2, '0')
  const mm = String(d.getMinutes()).padStart(2, '0')
  return `${hh}:${mm}`
}

const hasError = computed(() => Boolean(dashboardStore.error))
const lastSyncText = computed(() => {
  const t = dashboardStore.lastSyncAt
  if (!t) return '-'
  return fmtTime(t)
})
</script>

<template>
  <el-footer class="bottom">
    <div class="left">
      <span class="badge" :data-ok="!hasError">{{ hasError ? '数据异常' : '数据已同步' }}</span>
      <span class="muted">最后更新 {{ lastSyncText }}</span>
    </div>
    <div class="right">
      <span v-if="hasError" class="err">{{ dashboardStore.error }}</span>
      <span v-else class="muted">状态栏仅展示最小必要信息</span>
    </div>
  </el-footer>
</template>

<style scoped>
.bottom {
  height: 40px;
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 16px;
  background: #ffffff;
  border-top: 1px solid #e5e7eb;
  flex-shrink: 0;
}

.left,
.right {
  display: flex;
  align-items: center;
  gap: 12px;
}

.badge {
  font-size: 13px;
  padding: 4px 10px;
  border-radius: 999px;
  background: rgba(34, 197, 94, 0.12);
  color: #16a34a;
  font-weight: 600;
}

.badge[data-ok='false'] {
  background: rgba(239, 68, 68, 0.12);
  color: #dc2626;
}

.muted {
    font-size: 13px;
    color: #475569;
}

.err {
  font-size: 13px;
  color: #dc2626;
}

@media (max-width: 768px) {
  .bottom {
    padding: 0 16px;
  }
}
</style>

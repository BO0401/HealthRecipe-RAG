<script setup lang="ts">
import { computed, ref } from 'vue'
import { useProfileStore } from '../../stores/profile'
import { useDashboardStore } from '../../stores/dashboard'

const profileStore = useProfileStore()
const dashboardStore = useDashboardStore()

const syncing = ref(false)

const nickname = computed(() => {
  if (profileStore.user.height || profileStore.user.weight) return '用户'
  return '用户'
})

const greetText = computed(() => {
  const hour = new Date().getHours()
  if (hour < 11) return '上午好'
  if (hour < 18) return '下午好'
  return '晚上好'
})

const todayText = computed(() => {
  const d = new Date()
  return `${d.getFullYear()}-${String(d.getMonth() + 1).padStart(2, '0')}-${String(d.getDate()).padStart(2, '0')}`
})

const lastSyncText = computed(() => {
  const t = dashboardStore.lastSyncAt
  if (!t) return '--:--'
  return `${String(t.getHours()).padStart(2, '0')}:${String(t.getMinutes()).padStart(2, '0')}`
})

const doReload = async () => {
  if (syncing.value) return
  syncing.value = true
  console.debug('[dashboard] reload start')

  try {
    await Promise.all([
      dashboardStore.fetchMetrics(),
      dashboardStore.fetchAlerts(),
      profileStore.fetchProfile()
    ])
  } catch (err) {
    console.error('[dashboard] reload error:', err)
  } finally {
    syncing.value = false
    console.debug('[dashboard] reload end')
  }
}
</script>

<template>
  <el-header class="top">
    <div class="left">
      <div class="brand">HealthRecipe RAG</div>
      <div class="sub">
        {{ greetText }}，{{ nickname }} · {{ todayText }} · 更新 {{ lastSyncText }}
      </div>
    </div>
    <div class="right">
      <el-tag size="small" type="success">在线</el-tag>
      <el-button size="small" :loading="syncing" @click="doReload">重新加载</el-button>
    </div>
  </el-header>
</template>

<style scoped>
.top {
  height: 60px;
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 16px;
  background: #ffffff;
  border-bottom: 1px solid #e5e7eb;
  flex-shrink: 0;
}

.left {
  display: flex;
  flex-direction: column;
  gap: 3px;
}

.brand {
  font-size: 18px;
  font-weight: 800;
  color: #0f172a;
}

.sub {
    font-size: 13px;
    color: #475569;
}

.right {
  display: flex;
  align-items: center;
  gap: 12px;
}

@media (max-width: 768px) {
  .top {
    padding: 0 16px;
  }
}
</style>

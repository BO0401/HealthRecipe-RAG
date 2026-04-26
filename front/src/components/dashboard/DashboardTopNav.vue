<script setup lang="ts">
import { computed, reactive } from 'vue'

const now = new Date()

const me = reactive({
    nickname: '张同学',
    online: true,
    syncing: false,
    lastSyncAt: new Date(now.getTime() - 1000 * 60 * 9)
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

const fmtTime = (d: Date) => {
  const hh = String(d.getHours()).padStart(2, '0')
  const mm = String(d.getMinutes()).padStart(2, '0')
  return `${hh}:${mm}`
}

const doReload = async () => {
  if (me.syncing) return
  me.syncing = true
  console.debug('[dashboard] reload start')

  try {
    await new Promise((r) => setTimeout(r, 450))
    me.lastSyncAt = new Date()
    me.online = true
  } finally {
    me.syncing = false
    console.debug('[dashboard] reload end', { lastSyncAt: me.lastSyncAt.toISOString() })
  }
}
</script>

<template>
  <el-header class="top">
    <div class="left">
      <div class="brand">HealthRecipe RAG</div>
      <div class="sub">
        {{ greetText }}，{{ me.nickname }} · {{ todayText }} · 更新 {{ fmtTime(me.lastSyncAt) }}
      </div>
    </div>
    <div class="right">
      <el-tag size="small" :type="me.online ? 'success' : 'danger'">
        {{ me.online ? '在线' : '离线' }}
      </el-tag>
      <el-button size="small" :loading="me.syncing" @click="doReload">重新加载</el-button>
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


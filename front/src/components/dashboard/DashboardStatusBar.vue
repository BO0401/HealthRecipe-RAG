<script setup lang="ts">
import { reactive } from 'vue'

const now = new Date()

const sync = reactive({
    err: '',
    lastSyncAt: new Date(now.getTime() - 1000 * 60 * 9)
})

const fmtTime = (d: Date) => {
  const hh = String(d.getHours()).padStart(2, '0')
  const mm = String(d.getMinutes()).padStart(2, '0')
  return `${hh}:${mm}`
}

console.debug('[dashboard] status bar init', { lastSyncAt: sync.lastSyncAt.toISOString() })
</script>

<template>
  <el-footer class="bottom">
    <div class="left">
      <span class="badge" :data-ok="!sync.err">{{ sync.err ? '数据异常' : '数据已同步' }}</span>
      <span class="muted">最后更新 {{ fmtTime(sync.lastSyncAt) }}</span>
    </div>
    <div class="right">
      <span v-if="sync.err" class="err">{{ sync.err }}</span>
      <span v-else class="muted">状态栏仅展示最小必要信息</span>
    </div>
  </el-footer>
</template>

<style scoped>
.bottom {
  height: 36px;
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 12px;
  background: #ffffff;
  border-top: 1px solid #e5e7eb;
  flex-shrink: 0;
}

.left,
.right {
  display: flex;
  align-items: center;
  gap: 10px;
}

.badge {
  font-size: 12px;
  padding: 3px 8px;
  border-radius: 999px;
  background: rgba(34, 197, 94, 0.12);
  color: #16a34a;
}

.badge[data-ok='false'] {
  background: rgba(239, 68, 68, 0.12);
  color: #dc2626;
}

.muted {
    font-size: 12px;
    color: #64748b;
}

.err {
  font-size: 12px;
  color: #dc2626;
}

@media (max-width: 768px) {
  .bottom {
    padding: 0 16px;
  }
}
</style>


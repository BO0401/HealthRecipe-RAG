<script setup lang="ts">
console.log('[ai-chat] page mounted')
</script>

<template>
  <div class="page-placeholder">
    <div class="placeholder-icon">🤖</div>
    <h2>AI 营养助手</h2>
    <p class="muted">与 AI 助手讨论饮食和营养问题</p>
  </div>
</template>

<style scoped>
.page-placeholder {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  height: 100%;
  color: #606266;
}

.placeholder-icon {
  font-size: 48px;
  margin-bottom: 16px;
}

h2 {
  margin: 0 0 8px;
  font-size: 20px;
  color: #303133;
}

.muted {
  font-size: 14px;
  color: #909399;
}
</style>

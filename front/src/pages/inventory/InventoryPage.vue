<script setup lang="ts">
console.log('[inventory] page mounted')
</script>

<template>
  <div class="page-placeholder">
    <div class="placeholder-icon">📦</div>
    <h2>库存管理</h2>
    <p class="muted">管理冰箱和储物柜中的食材</p>
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

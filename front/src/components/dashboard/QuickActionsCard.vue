<script setup lang="ts">
import { useRouter } from 'vue-router'
import { List, ShoppingCart, MagicStick } from '@element-plus/icons-vue'

const router = useRouter()

const quickActions = [
  { key: 'recipe', label: '推荐食谱', icon: MagicStick, type: 'primary' },
  { key: 'inventory', label: '库存管理', icon: List, type: 'success' },
  { key: 'shopping', label: '采购清单', icon: ShoppingCart, type: 'warning' }
]

const onClickAction = (key: string) => {
  console.debug('[dashboard] quick action click', { key })
  router.push(`/${key}`)
}
</script>

<template>
  <div class="actions-panel">
    <div class="actions-header">快捷入口</div>
    <div class="actions-list">
      <el-button
        v-for="a in quickActions"
        :key="a.key"
        class="action-btn"
        text
        bg
        :type="a.type"
        @click="onClickAction(a.key)"
      >
        <el-icon class="m-r-6"><component :is="a.icon" /></el-icon>
        {{ a.label }}
      </el-button>
    </div>
  </div>
</template>

<style scoped>
.actions-panel {
  background: #fff;
  border-radius: 6px;
  padding: 16px 18px;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.06);
  display: flex;
  flex-direction: column;
  height: 100%;
}

.actions-header {
  font-weight: bold;
  font-size: 15px;
  color: #1a1a2e;
  margin-bottom: 14px;
}

.actions-list {
  display: flex;
  flex-direction: column;
  gap: 10px;
  flex: 1;
}

.action-btn {
  width: 100%;
  justify-content: flex-start;
  margin: 0 !important;
  font-weight: 600;
  padding: 14px 18px !important;
  font-size: 15px;
  flex: 1;
}

.m-r-6 {
  margin-right: 8px;
}
</style>

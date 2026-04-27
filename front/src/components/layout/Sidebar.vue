<script setup lang="ts">
import { useRoute, useRouter } from 'vue-router'
import { computed } from 'vue'
import {
  DataAnalysis,
  Food,
  Goods,
  ChatLineSquare,
  User
} from '@element-plus/icons-vue'

const route = useRoute()
const router = useRouter()

const menuItems = [
  { path: '/dashboard', label: '工作台', icon: DataAnalysis },
  { path: '/recipe', label: '食谱库', icon: Food },
  { path: '/inventory', label: '食材管理', icon: Goods },
  { path: '/ai-chat', label: 'AI 营养助手', icon: ChatLineSquare },
  { path: '/profile', label: '个人中心', icon: User }
]

const activePath = computed(() => route.path)

const onMenuClick = (path: string) => {
  console.debug('[sidebar] navigate to', path)
  router.push(path)
}
</script>

<template>
  <el-menu
    :default-active="activePath"
    class="sidebar-menu"
    router
    @select="onMenuClick"
  >
    <div class="sidebar-brand">
      <span class="brand-text">HealthRecipe</span>
    </div>

    <el-menu-item
      v-for="item in menuItems"
      :key="item.path"
      :index="item.path"
    >
      <el-icon><component :is="item.icon" /></el-icon>
      <span>{{ item.label }}</span>
    </el-menu-item>
  </el-menu>
</template>

<style scoped>
.sidebar-menu {
  height: 100%;
  border-right: 1px solid #e5e7eb;
  background: #ffffff;
  user-select: none;
}

.sidebar-brand {
  height: 56px;
  display: flex;
  align-items: center;
  padding: 0 20px;
  border-bottom: 1px solid #e5e7eb;
}

.brand-text {
  font-size: 16px;
  font-weight: 800;
  color: #0f172a;
}

.el-menu-item {
  font-size: 14px;
}

.el-menu-item.is-active {
  background-color: #ecf5ff !important;
  color: #409eff;
  font-weight: 600;
}
</style>

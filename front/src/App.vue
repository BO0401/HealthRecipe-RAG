<script setup lang="ts">
import Sidebar from './components/layout/Sidebar.vue'

console.log('[app] App.vue mounted with sidebar layout')
</script>

<template>
  <el-container class="app-layout">
    <Sidebar class="app-sidebar" />
    <el-container class="app-main">
      <router-view />
    </el-container>
  </el-container>
</template>

<style scoped>
.app-layout {
  height: 100vh;
  overflow: hidden;
}

.app-sidebar {
  width: 200px;
  flex-shrink: 0;
}

.app-main {
  flex: 1;
  overflow: hidden;
}
</style>

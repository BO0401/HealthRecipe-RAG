<script setup lang="ts">
import Sidebar from './components/layout/Sidebar.vue'

console.log('[app] App.vue mounted with sidebar layout')
</script>

<template>
  <div class="app-layout">
    <Sidebar class="app-sidebar" />
    <div class="app-main">
      <router-view />
    </div>
  </div>
</template>

<style scoped>
.app-layout {
  display: flex;
  width: 100vw;
  height: 100vh;
  overflow: hidden;
}

.app-sidebar {
  width: 220px;
  flex-shrink: 0;
}

.app-main {
  flex: 1;
  min-width: 0;
  height: 100%;
  overflow: hidden;
  background: #f0f2f5;
}
</style>

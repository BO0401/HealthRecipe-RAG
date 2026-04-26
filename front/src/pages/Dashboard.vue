<script setup lang="ts">
import DashboardTopNav from '../components/dashboard/DashboardTopNav.vue'
import DashboardStatusBar from '../components/dashboard/DashboardStatusBar.vue'
import MetricCards from '../components/dashboard/MetricCards.vue'
import NutritionPieCard from '../components/dashboard/NutritionPieCard.vue'
import UserStatusCard from '../components/dashboard/UserStatusCard.vue'
import QuickActionsCard from '../components/dashboard/QuickActionsCard.vue'
import BottomExtensionList from '../components/dashboard/BottomExtensionList.vue'

console.log('[dashboard] final grid layout activated')
</script>

<template>
  <el-container class="dash-layout">
    <DashboardTopNav />

    <el-main class="dash-content">
      <div class="grid-wall">
        <!-- 第一行：4 个指标卡片 (全宽) -->
        <div class="gw-row">
          <MetricCards />
        </div>

        <!-- 第二行：饼图 (60%) + 预警列表 (40%) -->
        <div class="gw-row gw-row--main">
          <div class="gw-cell gw-cell--60">
            <NutritionPieCard />
          </div>
          <div class="gw-cell gw-cell--40">
            <BottomExtensionList />
          </div>
        </div>

        <!-- 第三行：用户卡片 (60%) + 快捷入口 (40%) -->
        <div class="gw-row gw-row--footer">
          <div class="gw-cell gw-cell--60">
            <UserStatusCard />
          </div>
          <div class="gw-cell gw-cell--40">
            <QuickActionsCard />
          </div>
        </div>
      </div>
    </el-main>

    <DashboardStatusBar />
  </el-container>
</template>

<style scoped>
.dash-layout {
  height: 100vh;
  display: flex;
  flex-direction: column;
  background-color: #f0f2f5;
  overflow: hidden;
}

.dash-content {
  flex: 1;
  padding: 0;
  overflow-y: auto; /* 允许滚动，不强行拉伸 */
}

.grid-wall {
  display: flex;
  flex-direction: column;
  gap: 12px; /* 统一间距 12px */
  padding: 16px; /* 统一外边距 16px */
  max-width: 1440px;
  margin: 0 auto;
  width: 100%;
  box-sizing: border-box;
}

.gw-row {
  display: flex;
  gap: 12px;
  width: 100%;
}

.gw-row--main {
  height: 320px; /* 固定第二行高度 */
}

.gw-row--footer {
  /* 由内容撑开高度 */
  align-items: stretch;
}

.gw-cell {
  display: flex;
  min-width: 0; /* 防止子元素溢出 flex 容器 */
}

.gw-cell--60 {
  flex: 6;
}

.gw-cell--40 {
  flex: 4;
}

/* 让所有组件撑满格子 */
.gw-cell > * {
  width: 100%;
}

@media (max-width: 768px) {
  .gw-row {
    flex-direction: column;
    height: auto !important;
  }
}
</style>

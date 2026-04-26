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
  overflow-y: auto;
}

.grid-wall {
  display: flex;
  flex-direction: column;
  gap: 8px;
  padding: 10px;
  width: 100%;
  box-sizing: border-box;
}

.gw-row {
  display: flex;
  gap: 8px;
  width: 100%;
}

.gw-row--main {
  height: 360px;
}

.gw-row--footer {
  align-items: stretch;
}

.gw-cell {
  display: flex;
  min-width: 0;
}

.gw-cell--60 {
  flex: 6;
}

.gw-cell--40 {
  flex: 4;
}

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

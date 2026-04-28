<script setup lang="ts">
import { reactive } from 'vue'
import { Bell, ArrowRight } from '@element-plus/icons-vue'

const alerts = reactive([
  { id: 1, title: '全脂牛奶即将过期', date: '还剩 1 天', type: 'danger', tag: '库存预警' },
  { id: 2, title: '本周蛋白质摄入不足', date: '建议摄入 350g，实际 210g', type: 'warning', tag: '营养预警' },
  { id: 3, title: '冰箱库存：鸡蛋仅剩 2 枚', date: '建议补充', type: 'warning', tag: '采购提醒' },
  { id: 4, title: '推荐食谱：番茄牛腩', date: '今日推荐 · 高蛋白', type: 'primary', tag: '智能食谱' },
  { id: 5, title: '膳食纤维摄入偏低', date: '建议增加蔬菜水果', type: 'info', tag: '健康提示' }
])

console.debug('[dashboard] extension list init', alerts.length)
</script>

<template>
  <div class="ext-panel">
    <div class="ext-header">
      <span class="title">动态与预警</span>
      <el-button link type="primary" size="small">
        查看全部 <el-icon><ArrowRight /></el-icon>
      </el-button>
    </div>

    <div class="ext-list">
      <div v-for="item in alerts" :key="item.id" class="ext-item">
        <div class="item-left">
          <el-icon :class="['item-icon', item.type]"><Bell /></el-icon>
          <div class="item-info">
            <div class="item-title">{{ item.title }}</div>
            <div class="item-date">{{ item.date }}</div>
          </div>
        </div>
        <el-tag :type="item.type" size="small" effect="plain">{{ item.tag }}</el-tag>
      </div>
    </div>
  </div>
</template>

<style scoped>
.ext-panel {
  background: #fff;
  border-radius: 6px;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.06);
  display: flex;
  flex-direction: column;
  height: 100%;
  overflow: hidden;
}

.ext-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 14px 18px 0;
  flex-shrink: 0;
}

.ext-header .title {
  font-weight: bold;
  font-size: 15px;
  color: #1a1a2e;
}

.ext-list {
  flex: 1;
  overflow-y: auto;
  padding: 0 18px;
  display: flex;
  flex-direction: column;
  justify-content: center;
}

.ext-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 9px 0;
  border-bottom: 1px solid #e5e7eb;
}

.ext-item:last-child {
  border-bottom: none;
}

.item-left {
  display: flex;
  align-items: center;
  gap: 10px;
}

.item-icon {
  font-size: 18px;
  padding: 7px;
  border-radius: 50%;
}

.item-icon.danger { background: #fef0f0; color: #f56c6c; }
.item-icon.primary { background: #ecf5ff; color: #409eff; }
.item-icon.warning { background: #fdf6ec; color: #e6a23c; }
.item-icon.info { background: #f4f4f5; color: #909399; }

.item-title {
  font-size: 14px;
  color: #1a1a2e;
  font-weight: 600;
}

.item-date {
  font-size: 12px;
  color: #909399;
  margin-top: 2px;
}
</style>

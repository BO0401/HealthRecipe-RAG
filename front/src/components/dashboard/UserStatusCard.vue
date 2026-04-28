<script setup lang="ts">
import { computed } from 'vue'
import { User } from '@element-plus/icons-vue'
import { useProfileStore } from '../../stores/profile'

const profileStore = useProfileStore()

const bmi = computed(() => {
  const height = profileStore.user.height
  const weight = profileStore.user.weight
  if (!height || !weight) return 0
  const m = height / 100
  const v = weight / (m * m)
  return Number.isFinite(v) ? Number(v.toFixed(1)) : 0
})

const bmiInfo = computed(() => {
  const v = bmi.value
  if (v === 0) return { label: '未设置', type: 'info' as const, tip: '请先设置身高体重' }
  if (v < 18.5) return { label: '偏瘦', type: 'warning' as const, tip: '建议增加优质蛋白摄入' }
  if (v < 24) return { label: '正常', type: 'success' as const, tip: '状态良好，继续保持！' }
  if (v < 28) return { label: '偏胖', type: 'warning' as const, tip: '建议控制油脂摄入' }
  return { label: '肥胖', type: 'danger' as const, tip: '建议咨询专业营养师' }
})

const lastCheckText = computed(() => {
  if (!profileStore.lastSaved) return '暂无记录'
  return profileStore.lastSaved
})
</script>

<template>
  <div class="user-panel">
    <div class="user-header">
      <el-avatar :size="40">
        <el-icon :size="20"><User /></el-icon>
      </el-avatar>
      <div class="user-name">
        <h3>用户</h3>
        <p>{{ lastCheckText }}</p>
      </div>
    </div>

    <div class="status-grid">
      <div class="status-item">
        <div class="label">BMI</div>
        <div class="value">{{ bmi || '-' }}</div>
        <el-tag :type="bmiInfo.type" size="small" effect="plain">{{ bmiInfo.label }}</el-tag>
      </div>
      <div class="status-item">
        <div class="label">状态</div>
        <div class="status-tip">{{ bmiInfo.tip }}</div>
      </div>
    </div>

    <div class="body-metrics">
      <span>{{ profileStore.user.height || '-' }}cm</span>
      <span class="sep">|</span>
      <span>{{ profileStore.user.weight || '-' }}kg</span>
    </div>
  </div>
</template>

<style scoped>
.user-panel {
  background: #fff;
  border-radius: 6px;
  padding: 16px 18px;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.06);
  display: flex;
  flex-direction: column;
  height: 100%;
}

.user-header {
  display: flex;
  align-items: center;
  gap: 12px;
  margin-bottom: 12px;
}

.user-name h3 {
  margin: 0;
  font-size: 16px;
  color: #1a1a2e;
  font-weight: 700;
}

.user-name p {
  margin: 0;
  font-size: 12px;
  color: #909399;
}

.status-grid {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 8px;
  margin-bottom: 12px;
  flex: 1;
}

.status-item .label {
  font-size: 12px;
  color: #909399;
  margin-bottom: 4px;
  font-weight: 500;
}

.status-item .value {
  font-size: 28px;
  font-weight: bold;
  color: #1a1a2e;
  margin-bottom: 4px;
}

.status-tip {
  font-size: 14px;
  color: #303133;
  line-height: 1.5;
  font-weight: 500;
}

.body-metrics {
  font-size: 14px;
  color: #303133;
  text-align: center;
  padding-top: 10px;
  border-top: 1px solid #e5e7eb;
  font-weight: 500;
}

.sep {
  margin: 0 8px;
  color: #dcdfe6;
}
</style>

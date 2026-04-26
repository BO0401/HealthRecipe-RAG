<script setup lang="ts">
import { computed, reactive } from 'vue'

const user = reactive({
  nickname: '健康达人',
  avatar: 'https://cube.elemecdn.com/0/88/03b0d39583f48206768a7534e55bcpng.png',
  heightCm: 172,
  weightKg: 68.5,
  lastCheckAt: '今日 08:30'
})

const bmi = computed(() => {
  const m = user.heightCm / 100
  const v = user.weightKg / (m * m)
  return Number.isFinite(v) ? Number(v.toFixed(1)) : 0
})

const bmiInfo = computed(() => {
  const v = bmi.value
  if (v < 18.5) return { label: '偏瘦', type: 'warning' as const, tip: '建议增加优质蛋白摄入' }
  if (v < 24) return { label: '正常', type: 'success' as const, tip: '状态良好，继续保持！' }
  if (v < 28) return { label: '偏胖', type: 'warning' as const, tip: '建议控制油脂摄入' }
  return { label: '肥胖', type: 'danger' as const, tip: '建议咨询专业营养师' }
})

console.debug('[dashboard] user status card mounted')
</script>

<template>
  <div class="user-panel">
    <div class="user-header">
      <el-avatar :size="40" :src="user.avatar" />
      <div class="user-name">
        <h3>{{ user.nickname }}</h3>
        <p>{{ user.lastCheckAt }}</p>
      </div>
    </div>

    <div class="status-grid">
      <div class="status-item">
        <div class="label">BMI</div>
        <div class="value">{{ bmi }}</div>
        <el-tag :type="bmiInfo.type" size="small" effect="plain">{{ bmiInfo.label }}</el-tag>
      </div>
      <div class="status-item">
        <div class="label">状态</div>
        <div class="status-tip">{{ bmiInfo.tip }}</div>
      </div>
    </div>

    <div class="body-metrics">
      <span>{{ user.heightCm }}cm</span>
      <span class="sep">|</span>
      <span>{{ user.weightKg }}kg</span>
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

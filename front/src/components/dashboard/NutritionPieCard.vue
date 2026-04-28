<script setup lang="ts">
import { computed, nextTick, onBeforeUnmount, onMounted, ref, watch } from 'vue'
import * as echarts from 'echarts'
import { useDashboardStore } from '../../stores/dashboard'

const dashboardStore = useDashboardStore()

const chartEl = ref<HTMLDivElement | null>(null)
let chart: echarts.ECharts | null = null
let ro: ResizeObserver | null = null

const pieData = computed(() => {
  const n = dashboardStore.nutrition
  const c = n.carbsG * 4
  const p = n.proteinG * 4
  const f = n.fatG * 9
  if (c === 0 && p === 0 && f === 0) {
    return [
      { name: '碳水', value: 0, itemStyle: { color: '#409EFF' } },
      { name: '蛋白', value: 0, itemStyle: { color: '#67C23A' } },
      { name: '脂肪', value: 0, itemStyle: { color: '#E6A23C' } }
    ]
  }
  return [
    { name: '碳水', value: c, itemStyle: { color: '#409EFF' } },
    { name: '蛋白', value: p, itemStyle: { color: '#67C23A' } },
    { name: '脂肪', value: f, itemStyle: { color: '#E6A23C' } }
  ]
})

const totalKcal = computed(() => pieData.value.reduce((sum, x) => sum + x.value, 0))

const pctText = (kcal: number) => {
  const total = totalKcal.value
  if (!total) return '0.0%'
  return `${((kcal / total) * 100).toFixed(1)}%`
}

const renderPie = () => {
  if (!chartEl.value) return
  if (!chart) {
    chart = echarts.init(chartEl.value)
    console.debug('[dashboard] chart init', { w: chartEl.value.clientWidth, h: chartEl.value.clientHeight })
  }

  const option: echarts.EChartsOption = {
    tooltip: {
      trigger: 'item',
      backgroundColor: 'rgba(255, 255, 255, 0.9)',
      borderWidth: 0,
      textStyle: { color: '#303133' },
      formatter: (p: any) => {
        const kcal = Number(p?.value ?? 0)
        const pct = Number(p?.percent ?? 0)
        return `<div style="padding: 4px">
                  <div style="font-weight: bold; margin-bottom: 4px">${p?.name}</div>
                  <div>占比: ${pct.toFixed(1)}%</div>
                  <div>能量: ${kcal.toFixed(0)} kcal</div>
                </div>`
      }
    },
    legend: { show: false },
    series: [
      {
        type: 'pie',
        radius: ['50%', '80%'],
        center: ['40%', '50%'],
        avoidLabelOverlap: true,
        itemStyle: { borderRadius: 6, borderColor: '#f0f2f5', borderWidth: 3 },
        label: { show: false },
        emphasis: {
          label: {
            show: true,
            fontSize: 22,
            fontWeight: 'bold',
            formatter: '{b}\n{d}%'
          }
        },
        data: pieData.value
      }
    ]
  }

  chart.setOption(option)
  chart.resize()
}

onMounted(async () => {
  await nextTick()
  renderPie()

  if (chartEl.value) {
    ro = new ResizeObserver(() => {
      if (!chart) return
      chart.resize()
    })
    ro.observe(chartEl.value)
  }
})

watch(pieData, async () => {
  await nextTick()
  renderPie()
})

onBeforeUnmount(() => {
  ro?.disconnect()
  ro = null
  chart?.dispose()
  chart = null
  console.debug('[dashboard] chart dispose')
})
</script>

<template>
  <div class="pie-panel" v-loading="dashboardStore.loading">
    <div class="pie-header">
      <span class="title">建议摄入量</span>
      <span class="total">{{ totalKcal.toFixed(0) }} kcal</span>
    </div>
    <div class="pie-body">
      <div class="chart-wrap" ref="chartEl"></div>
      <div class="nutri-detail">
        <div class="nutri-item" v-for="item in pieData" :key="item.name">
          <span class="nutri-dot" :style="{ background: item.itemStyle.color }"></span>
          <span class="nutri-name">{{ item.name }}</span>
          <span class="nutri-val">{{ (item.value / (item.name === '脂肪' ? 9 : 4)).toFixed(0) }}g</span>
          <span class="nutri-pct">{{ pctText(item.value) }}</span>
        </div>
        <div v-if="dashboardStore.error" class="err-line">{{ dashboardStore.error }}</div>
        <div v-else-if="totalKcal === 0" class="muted-line">暂无可用数据</div>
      </div>
    </div>
  </div>
</template>

<style scoped>
.pie-panel {
  background: #fff;
  border-radius: 6px;
  padding: 16px 18px;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.06);
  display: flex;
  flex-direction: column;
  height: 100%;
}

.pie-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 10px;
  flex-shrink: 0;
}

.pie-header .title {
  font-weight: bold;
  font-size: 15px;
  color: #1a1a2e;
}

.pie-header .total {
  font-size: 15px;
  color: #606266;
  font-weight: 600;
}

.pie-body {
  display: flex;
  align-items: center;
  gap: 14px;
  flex: 1;
  min-height: 0;
}

.chart-wrap {
  height: 100%;
  width: 60%;
  min-height: 200px;
}

.nutri-detail {
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.nutri-item {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 14px;
}

.nutri-dot {
  width: 10px;
  height: 10px;
  border-radius: 50%;
  flex-shrink: 0;
}

.nutri-name {
  width: 30px;
  color: #606266;
  font-weight: 500;
}

.nutri-val {
  font-weight: bold;
  color: #1a1a2e;
  min-width: 44px;
}

.nutri-pct {
  color: #909399;
  min-width: 48px;
  text-align: right;
}

.err-line {
  margin-top: 10px;
  font-size: 13px;
  color: #dc2626;
}

.muted-line {
  margin-top: 10px;
  font-size: 13px;
  color: #909399;
}
</style>

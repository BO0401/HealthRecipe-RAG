<script setup lang="ts">
import { computed, nextTick, onBeforeUnmount, onMounted, reactive, ref, watch } from 'vue'
import * as echarts from 'echarts'

const nutri = reactive({
  carbsG: 240,
  proteinG: 100,
  fatG: 65
})

const chartEl = ref<HTMLDivElement | null>(null)
let chart: echarts.ECharts | null = null
let ro: ResizeObserver | null = null

const pieData = computed(() => {
  const c = nutri.carbsG * 4
  const p = nutri.proteinG * 4
  const f = nutri.fatG * 9
  return [
    { name: '碳水', value: c, itemStyle: { color: '#409EFF' } },
    { name: '蛋白', value: p, itemStyle: { color: '#67C23A' } },
    { name: '脂肪', value: f, itemStyle: { color: '#E6A23C' } }
  ]
})

const totalKcal = computed(() => pieData.value.reduce((sum, x) => sum + x.value, 0))

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
        radius: ['45%', '75%'],
        center: ['35%', '50%'],
        avoidLabelOverlap: true,
        itemStyle: { borderRadius: 4, borderColor: '#f0f2f5', borderWidth: 2 },
        label: { show: false },
        emphasis: {
          label: {
            show: true,
            fontSize: 20,
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
  <div class="pie-panel">
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
          <span class="nutri-pct">{{ ((item.value / totalKcal) * 100).toFixed(1) }}%</span>
        </div>
      </div>
    </div>
  </div>
</template>

<style scoped>
.pie-panel {
  background: #fff;
  border-radius: 6px;
  padding: 16px;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.06);
  display: flex;
  flex-direction: column;
  height: 100%;
}

.pie-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 8px;
  flex-shrink: 0;
}

.pie-header .title {
  font-weight: bold;
  font-size: 14px;
  color: #303133;
}

.pie-header .total {
  font-size: 13px;
  color: #909399;
}

.pie-body {
  display: flex;
  align-items: center;
  gap: 12px;
  flex: 1;
  min-height: 0;
}

.chart-wrap {
  height: 100%;
  width: 60%;
  min-height: 180px;
}

.nutri-detail {
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.nutri-item {
  display: flex;
  align-items: center;
  gap: 6px;
  font-size: 13px;
}

.nutri-dot {
  width: 8px;
  height: 8px;
  border-radius: 50%;
  flex-shrink: 0;
}

.nutri-name {
  width: 28px;
  color: #606266;
}

.nutri-val {
  font-weight: bold;
  color: #303133;
  min-width: 40px;
}

.nutri-pct {
  color: #909399;
  font-size: 12px;
}
</style>

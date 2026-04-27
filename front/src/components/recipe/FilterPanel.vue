<script setup lang="ts">
import { useRecipeStore } from '../../stores/recipe'

const recipeStore = useRecipeStore()
</script>

<template>
  <div class="filter-panel">
    <div class="filter-header">
      <span class="filter-title">筛选条件</span>
      <el-button text size="small" @click="recipeStore.fetchRecipes()">
        重置
      </el-button>
    </div>

    <el-divider style="margin: 8px 0" />

    <div class="filter-section">
      <h4 class="section-label">身体指标</h4>
      <el-select
        :model-value="recipeStore.filter.bodyType"
        placeholder="选择身体类型"
        class="filter-select"
        @update:model-value="recipeStore.setBodyType"
      >
        <el-option
          v-for="opt in recipeStore.bodyTypeOptions"
          :key="opt.value"
          :label="opt.label"
          :value="opt.value"
        />
      </el-select>
    </div>

    <div class="filter-section">
      <h4 class="section-label">过敏原排除</h4>
      <el-checkbox-group
        :model-value="recipeStore.filter.allergens"
        class="allergen-group"
        @update:model-value="recipeStore.setAllergens"
      >
        <el-checkbox
          v-for="opt in recipeStore.allergenOptions"
          :key="opt.value"
          :label="opt.value"
          :value="opt.value"
        >
          {{ opt.label }}
        </el-checkbox>
      </el-checkbox-group>
    </div>

    <div class="filter-section">
      <h4 class="section-label">关键词搜索</h4>
      <el-input
        :model-value="recipeStore.filter.keyword"
        placeholder="搜索食谱名称、描述..."
        clearable
        @input="recipeStore.setKeyword"
      />
    </div>

    <el-divider style="margin: 8px 0" />

    <div class="filter-summary">
      <span class="summary-text">
        共 <strong>{{ recipeStore.filteredRecipes().length }}</strong> 个食谱
      </span>
    </div>
  </div>
</template>

<style scoped>
.filter-panel {
  background: #fff;
  border-radius: 8px;
  padding: 16px;
  box-shadow: 0 1px 4px rgba(0, 0, 0, 0.08);
}

.filter-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.filter-title {
  font-size: 16px;
  font-weight: 600;
  color: #1a1a2e;
}

.filter-section {
  margin-bottom: 16px;
}

.section-label {
  font-size: 13px;
  font-weight: 500;
  color: #606266;
  margin: 0 0 8px;
}

.filter-select {
  width: 100%;
}

.allergen-group {
  display: flex;
  flex-direction: column;
  gap: 6px;
}

.filter-summary {
  text-align: center;
}

.summary-text {
  font-size: 13px;
  color: #909399;
}

.summary-text strong {
  color: #409eff;
  font-size: 15px;
}
</style>

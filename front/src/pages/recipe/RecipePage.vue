<script setup lang="ts">
import { onMounted } from 'vue'
import { useRecipeStore } from '../../stores/recipe'
import FilterPanel from '../../components/recipe/FilterPanel.vue'
import RecipeCard from '../../components/recipe/RecipeCard.vue'
import type { RecipeItem } from '../../stores/recipe'

const recipeStore = useRecipeStore()

onMounted(() => {
  recipeStore.fetchRecipes()
})

const handleAddToList = (recipe: RecipeItem) => {
  recipeStore.addToShoppingList(recipe)
}
</script>

<template>
  <div class="recipe-page">
    <div class="page-header">
      <h2 class="page-title">智能食谱推荐</h2>
      <p class="page-subtitle">根据身体指标和饮食偏好，为你推荐合适的健康食谱</p>
    </div>

    <div class="recipe-layout">
      <aside class="recipe-sidebar">
        <FilterPanel />
      </aside>

      <main class="recipe-main">
        <div v-if="recipeStore.loading" class="loading-state">
          <el-icon class="loading-icon" :size="32"><svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M21 12a9 9 0 11-6.219-8.56"/></svg></el-icon>
          <p>正在为你推荐食谱...</p>
        </div>

        <div v-else-if="recipeStore.error" class="error-state">
          <el-result
            icon="error"
            title="加载失败"
            :sub-title="recipeStore.error"
          >
            <template #extra>
              <el-button type="primary" @click="recipeStore.fetchRecipes()">
                重新加载
              </el-button>
            </template>
          </el-result>
        </div>

        <div v-else-if="recipeStore.filteredRecipes().length === 0" class="empty-state">
          <el-empty description="没有找到匹配的食谱">
            <el-button type="primary" @click="recipeStore.fetchRecipes()">
              查看全部食谱
            </el-button>
          </el-empty>
        </div>

        <div v-else class="card-grid">
          <RecipeCard
            v-for="recipe in recipeStore.filteredRecipes()"
            :key="recipe.id"
            :recipe="recipe"
            @add="handleAddToList"
          />
        </div>
      </main>
    </div>
  </div>
</template>

<style scoped>
.recipe-page {
  width: 100%;
  height: 100%;
  display: flex;
  flex-direction: column;
  padding: 24px;
  background: #f0f2f5;
  box-sizing: border-box;
  overflow: hidden;
}

.page-header {
  margin-bottom: 20px;
  flex-shrink: 0;
}

.page-title {
  font-size: 22px;
  font-weight: 700;
  color: #1a1a2e;
  margin: 0 0 6px;
}

.page-subtitle {
  font-size: 14px;
  color: #909399;
  margin: 0;
}

.recipe-layout {
  display: flex;
  gap: 20px;
  flex: 1;
  min-height: 0;
}

.recipe-sidebar {
  width: 240px;
  flex-shrink: 0;
  overflow-y: auto;
}

.recipe-main {
  flex: 1;
  min-width: 0;
  overflow-y: auto;
}

.loading-state {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  height: 300px;
  color: #909399;
  gap: 12px;
}

.loading-icon {
  animation: spin 1s linear infinite;
}

@keyframes spin {
  from { transform: rotate(0deg); }
  to { transform: rotate(360deg); }
}

.error-state {
  display: flex;
  justify-content: center;
  padding-top: 40px;
}

.empty-state {
  display: flex;
  justify-content: center;
  padding-top: 60px;
}

.card-grid {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 16px;
}
</style>

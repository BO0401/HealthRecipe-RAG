<script setup lang="ts">
import type { RecipeItem } from '../../stores/recipe'
import { useRecipeStore } from '../../stores/recipe'
import { Timer, Lightning } from '@element-plus/icons-vue'

const props = defineProps<{
  recipe: RecipeItem
}>()

const emit = defineEmits<{
  add: [recipe: RecipeItem]
}>()

const recipeStore = useRecipeStore()

const handleAdd = () => {
  recipeStore.addToShoppingList(props.recipe)
  emit('add', props.recipe)
}
</script>

<template>
  <div class="recipe-card">
    <div class="card-image-wrap">
      <img :src="recipe.image" :alt="recipe.name" class="card-image" />
      <div class="card-tags">
        <span v-for="tag in recipe.tags" :key="tag" class="tag-badge">{{ tag }}</span>
      </div>
    </div>
    <div class="card-body">
      <h3 class="card-title">{{ recipe.name }}</h3>
      <p class="card-desc">{{ recipe.description }}</p>
      <div class="card-meta">
        <span class="meta-item">
          <el-icon :size="14"><Lightning /></el-icon>
          <span>{{ recipe.calories }} kcal</span>
        </span>
        <span class="meta-item">
          <el-icon :size="14"><Timer /></el-icon>
          <span>{{ recipe.cookingTime }} 分钟</span>
        </span>
      </div>
      <el-button type="primary" class="add-btn" @click="handleAdd">
        添加到清单
      </el-button>
    </div>
  </div>
</template>

<style scoped>
.recipe-card {
  background: #fff;
  border-radius: 8px;
  overflow: hidden;
  box-shadow: 0 1px 4px rgba(0, 0, 0, 0.08);
  transition: box-shadow 0.25s, transform 0.25s;
  display: flex;
  flex-direction: column;
}

.recipe-card:hover {
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.12);
  transform: translateY(-2px);
}

.card-image-wrap {
  position: relative;
  width: 100%;
  aspect-ratio: 4 / 3;
  overflow: hidden;
  background: #f5f7fa;
}

.card-image {
  width: 100%;
  height: 100%;
  object-fit: cover;
  display: block;
}

.card-tags {
  position: absolute;
  top: 8px;
  left: 8px;
  display: flex;
  flex-wrap: wrap;
  gap: 4px;
}

.tag-badge {
  background: rgba(0, 0, 0, 0.55);
  color: #fff;
  font-size: 11px;
  padding: 2px 8px;
  border-radius: 4px;
  backdrop-filter: blur(4px);
}

.card-body {
  padding: 14px;
  display: flex;
  flex-direction: column;
  flex: 1;
}

.card-title {
  font-size: 16px;
  font-weight: 600;
  color: #1a1a2e;
  margin: 0 0 6px;
  line-height: 1.4;
}

.card-desc {
  font-size: 13px;
  color: #606266;
  margin: 0 0 12px;
  line-height: 1.5;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.card-meta {
  display: flex;
  gap: 16px;
  margin-bottom: 12px;
}

.meta-item {
  display: flex;
  align-items: center;
  gap: 4px;
  font-size: 13px;
  color: #909399;
}

.add-btn {
  width: 100%;
  margin-top: auto;
}
</style>

<script setup lang="ts">
import { onMounted } from 'vue'
import { useShoppingStore } from '../../stores/shopping'
import {
  ShoppingCart,
  Check,
  Delete,
  Refresh,
  Goods,
  Money
} from '@element-plus/icons-vue'

const shoppingStore = useShoppingStore()

onMounted(() => {
  shoppingStore.fetchShoppingList()
})

const categoryLabels: Record<string, string> = {
  meat: '肉类',
  seafood: '海鲜',
  vegetables: '蔬菜',
  fruits: '水果',
  grains: '谷物',
  eggs: '蛋类',
  beans: '豆制品',
  dairy: '奶制品',
  mushrooms: '菌菇',
  nuts: '坚果',
  other: '其他'
}

const statusLabels: Record<string, string> = {
  pending: '待采购',
  purchased: '已购买',
  excluded: '已剔除'
}

const statusTypes: Record<string, string> = {
  pending: 'warning',
  purchased: 'success',
  excluded: 'info'
}
</script>

<template>
  <div class="shopping-page">
    <div class="page-header">
      <div class="header-left">
        <h2 class="page-title">采购清单</h2>
        <p class="page-subtitle">
          已剔除库存中已有的
          <span class="highlight">{{ shoppingStore.excludedCount }}</span>
          种食材
        </p>
      </div>
      <el-button
        :icon="Refresh"
        :loading="shoppingStore.loading"
        @click="shoppingStore.fetchShoppingList()"
      >
        刷新清单
      </el-button>
    </div>

    <div class="summary-cards">
      <div class="summary-card">
        <div class="card-icon pending">
          <ShoppingCart />
        </div>
        <div class="card-info">
          <span class="card-value">{{ shoppingStore.pendingItems.length }}</span>
          <span class="card-label">待采购</span>
        </div>
      </div>
      <div class="summary-card">
        <div class="card-icon purchased">
          <Check />
        </div>
        <div class="card-info">
          <span class="card-value">{{ shoppingStore.purchasedItems.length }}</span>
          <span class="card-label">已购买</span>
        </div>
      </div>
      <div class="summary-card">
        <div class="card-icon total">
          <Goods />
        </div>
        <div class="card-info">
          <span class="card-value">{{ shoppingStore.totalItems }}</span>
          <span class="card-label">食材总数</span>
        </div>
      </div>
      <div class="summary-card">
        <div class="card-icon cost">
          <Money />
        </div>
        <div class="card-info">
          <span class="card-value">¥{{ shoppingStore.totalEstimatedCost.toFixed(0) }}</span>
          <span class="card-label">预估总价</span>
        </div>
      </div>
    </div>

    <div class="table-container">
      <el-table
        :data="shoppingStore.items"
        style="width: 100%"
        height="100%"
        stripe
        v-loading="shoppingStore.loading"
      >
        <el-table-column type="index" label="#" width="50" />
        <el-table-column prop="name" label="食材名称" min-width="120">
          <template #default="{ row }">
            <span :class="{ 'purchased-text': row.status === 'purchased' }">
              {{ row.name }}
            </span>
          </template>
        </el-table-column>
        <el-table-column label="分类" width="100">
          <template #default="{ row }">
            <el-tag size="small" effect="plain">
              {{ categoryLabels[row.category] || '其他' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="数量" width="100">
          <template #default="{ row }">
            {{ row.quantity }} {{ row.unit }}
          </template>
        </el-table-column>
        <el-table-column label="预估单价" width="100">
          <template #default="{ row }">
            ¥{{ row.estimatedPrice.toFixed(1) }}
          </template>
        </el-table-column>
        <el-table-column label="小计" width="100">
          <template #default="{ row }">
            <span class="subtotal">¥{{ (row.estimatedPrice * row.quantity).toFixed(1) }}</span>
          </template>
        </el-table-column>
        <el-table-column label="来源" min-width="130">
          <template #default="{ row }">
            <span class="source-text">{{ row.source || '-' }}</span>
          </template>
        </el-table-column>
        <el-table-column label="状态" width="100">
          <template #default="{ row }">
            <el-tag
              :type="statusTypes[row.status] as any"
              size="small"
              effect="light"
            >
              {{ statusLabels[row.status] }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="120" fixed="right">
          <template #default="{ row }">
            <el-button
              v-if="row.status === 'pending'"
              type="success"
              size="small"
              :icon="Check"
              circle
              @click="shoppingStore.toggleStatus(row.id)"
            />
            <el-button
              v-if="row.status === 'purchased'"
              type="warning"
              size="small"
              :icon="Refresh"
              circle
              @click="shoppingStore.toggleStatus(row.id)"
            />
            <el-button
              type="danger"
              size="small"
              :icon="Delete"
              circle
              @click="shoppingStore.removeItem(row.id)"
            />
          </template>
        </el-table-column>
      </el-table>

      <div v-if="shoppingStore.error" class="error-bar">
        <el-alert
          :title="shoppingStore.error"
          type="error"
          show-icon
          closable
        />
      </div>
    </div>

    <div class="page-footer">
      <el-button
        type="primary"
        :icon="ShoppingCart"
        size="large"
        :loading="shoppingStore.loading"
        @click="shoppingStore.fetchShoppingList()"
      >
        生成采购清单
      </el-button>
      <el-button
        :disabled="shoppingStore.purchasedItems.length === 0"
        :icon="Delete"
        size="large"
        @click="shoppingStore.clearPurchased()"
      >
        清除已购买
      </el-button>
    </div>
  </div>
</template>

<style scoped>
.shopping-page {
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
  display: flex;
  align-items: flex-start;
  justify-content: space-between;
  margin-bottom: 20px;
  flex-shrink: 0;
}

.header-left {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.page-title {
  font-size: 22px;
  font-weight: 700;
  color: #1a1a2e;
  margin: 0;
}

.page-subtitle {
  font-size: 13px;
  color: #909399;
  margin: 0;
}

.highlight {
  color: #e6a23c;
  font-weight: 700;
  font-size: 15px;
}

.summary-cards {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 16px;
  margin-bottom: 20px;
  flex-shrink: 0;
}

.summary-card {
  display: flex;
  align-items: center;
  gap: 14px;
  background: #fff;
  border-radius: 10px;
  padding: 18px 20px;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.06);
}

.card-icon {
  width: 44px;
  height: 44px;
  border-radius: 10px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 20px;
  flex-shrink: 0;
}

.card-icon.pending {
  background: #fdf6ec;
  color: #e6a23c;
}

.card-icon.purchased {
  background: #f0f9eb;
  color: #67c23a;
}

.card-icon.total {
  background: #ecf5ff;
  color: #409eff;
}

.card-icon.cost {
  background: #fef0f0;
  color: #f56c6c;
}

.card-info {
  display: flex;
  flex-direction: column;
}

.card-value {
  font-size: 22px;
  font-weight: 700;
  color: #303133;
  line-height: 1.2;
}

.card-label {
  font-size: 12px;
  color: #909399;
  margin-top: 2px;
}

.table-container {
  flex: 1;
  min-height: 0;
  background: #fff;
  border-radius: 10px;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.06);
  overflow: hidden;
  display: flex;
  flex-direction: column;
}

.error-bar {
  flex-shrink: 0;
  padding: 12px 16px 0;
}

.purchased-text {
  text-decoration: line-through;
  color: #c0c4cc;
}

.subtotal {
  font-weight: 600;
  color: #f56c6c;
}

.source-text {
  font-size: 12px;
  color: #909399;
}

.page-footer {
  display: flex;
  gap: 12px;
  padding-top: 16px;
  flex-shrink: 0;
  justify-content: center;
}
</style>

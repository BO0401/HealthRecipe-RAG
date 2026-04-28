<script setup lang="ts">
import { onMounted, ref } from 'vue'
import { useInventoryShoppingStore } from '../../stores/inventoryShopping'
import {
  INVENTORY_CATEGORY_LABELS,
  INVENTORY_LOCATION_LABELS,
  INVENTORY_LOCATION_TAG_TYPES,
  SHOPPING_STATUS_LABELS,
  SHOPPING_STATUS_TAG_TYPES
} from '../../constants/inventory'
import {
  ShoppingCart,
  Check,
  Delete,
  Refresh,
  Goods,
  Money,
  Plus,
  Warning,
  Refrigerator
} from '@element-plus/icons-vue'

const store = useInventoryShoppingStore()
const activeTab = ref<'inventory' | 'shopping'>('inventory')

onMounted(() => {
  store.fetchInventoryList()
  store.fetchShoppingList()
})

const addDialogVisible = ref(false)
const addForm = ref({
  name: '',
  category: 'vegetables',
  quantity: 1,
  unit: '个',
  expiryDate: '',
  location: 'fridge' as 'fridge' | 'freezer' | 'pantry'
})

const resetAddForm = () => {
  addForm.value = {
    name: '',
    category: 'vegetables',
    quantity: 1,
    unit: '个',
    expiryDate: '',
    location: 'fridge'
  }
}

const handleAddInventory = () => {
  if (!addForm.value.name.trim()) return
  store.addInventoryItem({
    name: addForm.value.name.trim(),
    category: addForm.value.category,
    quantity: addForm.value.quantity,
    unit: addForm.value.unit,
    expiryDate: addForm.value.expiryDate || undefined,
    addedDate: new Date().toISOString().slice(0, 10),
    location: addForm.value.location
  })
  addDialogVisible.value = false
  resetAddForm()
}

const formatDate = (dateStr?: string) => {
  if (!dateStr) return '-'
  const d = new Date(dateStr)
  return `${d.getMonth() + 1}/${d.getDate()}`
}

const getExpiryDays = (dateStr?: string): number | null => {
  if (!dateStr) return null
  const diff = new Date(dateStr).getTime() - Date.now()
  return Math.ceil(diff / (1000 * 60 * 60 * 24))
}
</script>

<template>
  <div class="inventory-shopping-page">
    <div class="page-header">
      <div class="header-left">
        <h2 class="page-title">食材管理</h2>
        <p class="page-subtitle">统一管理库存食材与采购清单</p>
      </div>
      <div class="header-actions">
        <el-button
          v-if="activeTab === 'inventory'"
          type="primary"
          :icon="Plus"
          @click="addDialogVisible = true"
        >
          添加食材
        </el-button>
        <el-button
          v-if="activeTab === 'shopping'"
          :icon="Refresh"
          :loading="store.loading"
          @click="store.fetchShoppingList()"
        >
          刷新清单
        </el-button>
      </div>
    </div>

    <el-tabs v-model="activeTab" class="page-tabs" type="border-card">
      <el-tab-pane label="库存管理" name="inventory">
        <div class="summary-cards">
          <div class="summary-card">
            <div class="card-icon total">
              <Goods />
            </div>
            <div class="card-info">
              <span class="card-value">{{ store.totalInventoryItems }}</span>
              <span class="card-label">食材总数</span>
            </div>
          </div>
          <div class="summary-card">
            <div class="card-icon pending">
              <Warning />
            </div>
            <div class="card-info">
              <span class="card-value">{{ store.expiringSoonItems.length }}</span>
              <span class="card-label">临期食材</span>
            </div>
          </div>
          <div class="summary-card">
            <div class="card-icon purchased">
              <Refrigerator />
            </div>
            <div class="card-info">
              <span class="card-value">{{ store.inventoryItems.filter(i => i.location === 'fridge').length }}</span>
              <span class="card-label">冷藏</span>
            </div>
          </div>
          <div class="summary-card">
            <div class="card-icon cost">
              <Refrigerator />
            </div>
            <div class="card-info">
              <span class="card-value">{{ store.inventoryItems.filter(i => i.location === 'freezer').length }}</span>
              <span class="card-label">冷冻</span>
            </div>
          </div>
        </div>

        <div class="table-container">
          <el-table
            :data="store.inventoryItems"
            style="width: 100%"
            height="100%"
            stripe
            v-loading="store.loading"
          >
            <el-table-column type="index" label="#" width="50" />
            <el-table-column prop="name" label="食材名称" min-width="120" />
            <el-table-column label="分类" width="90">
              <template #default="{ row }">
                <el-tag size="small" effect="plain">
                  {{ INVENTORY_CATEGORY_LABELS[row.category] || '其他' }}
                </el-tag>
              </template>
            </el-table-column>
            <el-table-column label="数量" width="90">
              <template #default="{ row }">
                {{ row.quantity }} {{ row.unit }}
              </template>
            </el-table-column>
            <el-table-column label="存放位置" width="90">
              <template #default="{ row }">
                <el-tag :type="INVENTORY_LOCATION_TAG_TYPES[row.location] as any" size="small" effect="light">
                  {{ INVENTORY_LOCATION_LABELS[row.location] }}
                </el-tag>
              </template>
            </el-table-column>
            <el-table-column label="保质期" width="100">
              <template #default="{ row }">
                <span v-if="!row.expiryDate" class="no-expiry">-</span>
                <span
                  v-else
                  :class="{
                    'expiry-warning': getExpiryDays(row.expiryDate) !== null && getExpiryDays(row.expiryDate)! <= 3,
                    'expiry-danger': getExpiryDays(row.expiryDate) !== null && getExpiryDays(row.expiryDate)! <= 0
                  }"
                >
                  {{ formatDate(row.expiryDate) }}
                  <span v-if="getExpiryDays(row.expiryDate) !== null && getExpiryDays(row.expiryDate)! <= 3" class="expiry-badge">
                    ({{ getExpiryDays(row.expiryDate)! <= 0 ? '已过期' : getExpiryDays(row.expiryDate) + '天' }})
                  </span>
                </span>
              </template>
            </el-table-column>
            <el-table-column label="入库日期" width="90">
              <template #default="{ row }">
                {{ formatDate(row.addedDate) }}
              </template>
            </el-table-column>
            <el-table-column label="操作" width="80" fixed="right">
              <template #default="{ row }">
                <el-button
                  type="danger"
                  size="small"
                  :icon="Delete"
                  circle
                  @click="store.removeInventoryItem(row.id)"
                />
              </template>
            </el-table-column>
          </el-table>
        </div>
      </el-tab-pane>

      <el-tab-pane label="采购清单" name="shopping">
        <div class="summary-cards">
          <div class="summary-card">
            <div class="card-icon pending">
              <ShoppingCart />
            </div>
            <div class="card-info">
              <span class="card-value">{{ store.pendingItems.length }}</span>
              <span class="card-label">待采购</span>
            </div>
          </div>
          <div class="summary-card">
            <div class="card-icon purchased">
              <Check />
            </div>
            <div class="card-info">
              <span class="card-value">{{ store.purchasedItems.length }}</span>
              <span class="card-label">已购买</span>
            </div>
          </div>
          <div class="summary-card">
            <div class="card-icon total">
              <Goods />
            </div>
            <div class="card-info">
              <span class="card-value">{{ store.totalShoppingItems }}</span>
              <span class="card-label">食材总数</span>
            </div>
          </div>
          <div class="summary-card">
            <div class="card-icon cost">
              <Money />
            </div>
            <div class="card-info">
              <span class="card-value">¥{{ store.totalEstimatedCost.toFixed(0) }}</span>
              <span class="card-label">预估总价</span>
            </div>
          </div>
        </div>

        <div class="table-container">
          <el-table
            :data="store.shoppingItems"
            style="width: 100%"
            height="100%"
            stripe
            v-loading="store.loading"
          >
            <el-table-column type="index" label="#" width="50" />
            <el-table-column prop="name" label="食材名称" min-width="120">
              <template #default="{ row }">
                <span :class="{ 'purchased-text': row.status === 'purchased' }">
                  {{ row.name }}
                </span>
              </template>
            </el-table-column>
            <el-table-column label="分类" width="90">
              <template #default="{ row }">
                <el-tag size="small" effect="plain">
                  {{ INVENTORY_CATEGORY_LABELS[row.category] || '其他' }}
                </el-tag>
              </template>
            </el-table-column>
            <el-table-column label="数量" width="90">
              <template #default="{ row }">
                {{ row.quantity }} {{ row.unit }}
              </template>
            </el-table-column>
            <el-table-column label="预估单价" width="90">
              <template #default="{ row }">
                ¥{{ row.estimatedPrice.toFixed(1) }}
              </template>
            </el-table-column>
            <el-table-column label="小计" width="90">
              <template #default="{ row }">
                <span class="subtotal">¥{{ (row.estimatedPrice * row.quantity).toFixed(1) }}</span>
              </template>
            </el-table-column>
            <el-table-column label="来源" min-width="120">
              <template #default="{ row }">
                <span class="source-text">{{ row.source || '-' }}</span>
              </template>
            </el-table-column>
            <el-table-column label="状态" width="90">
              <template #default="{ row }">
                <el-tag
                  :type="SHOPPING_STATUS_TAG_TYPES[row.status] as any"
                  size="small"
                  effect="light"
                >
                  {{ SHOPPING_STATUS_LABELS[row.status] }}
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
                  @click="store.toggleShoppingStatus(row.id)"
                />
                <el-button
                  v-if="row.status === 'purchased'"
                  type="warning"
                  size="small"
                  :icon="Refresh"
                  circle
                  @click="store.toggleShoppingStatus(row.id)"
                />
                <el-button
                  type="danger"
                  size="small"
                  :icon="Delete"
                  circle
                  @click="store.removeShoppingItem(row.id)"
                />
              </template>
            </el-table-column>
          </el-table>

          <div v-if="store.error" class="error-bar">
            <el-alert
              :title="store.error"
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
            :loading="store.loading"
            @click="store.fetchShoppingList()"
          >
            生成采购清单
          </el-button>
          <el-button
            :disabled="store.purchasedItems.length === 0"
            :icon="Delete"
            size="large"
            @click="store.clearPurchased()"
          >
            清除已购买
          </el-button>
        </div>
      </el-tab-pane>
    </el-tabs>

    <el-dialog
      v-model="addDialogVisible"
      title="添加食材到库存"
      width="480px"
      :close-on-click-modal="false"
      @close="resetAddForm"
    >
      <el-form :model="addForm" label-width="80px">
        <el-form-item label="食材名称">
          <el-input v-model="addForm.name" placeholder="输入食材名称" />
        </el-form-item>
        <el-form-item label="分类">
          <el-select v-model="addForm.category" style="width: 100%">
            <el-option v-for="(label, key) in INVENTORY_CATEGORY_LABELS" :key="key" :label="label" :value="key" />
          </el-select>
        </el-form-item>
        <el-form-item label="数量">
          <el-input-number v-model="addForm.quantity" :min="1" :max="999" />
          <el-input v-model="addForm.unit" placeholder="单位" style="width: 100px; margin-left: 8px" />
        </el-form-item>
        <el-form-item label="存放位置">
          <el-radio-group v-model="addForm.location">
            <el-radio value="fridge">冷藏</el-radio>
            <el-radio value="freezer">冷冻</el-radio>
            <el-radio value="pantry">常温</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="保质期">
          <el-date-picker
            v-model="addForm.expiryDate"
            type="date"
            placeholder="选择日期"
            value-format="YYYY-MM-DD"
            style="width: 100%"
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="addDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleAddInventory">添加</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<style scoped>
.inventory-shopping-page {
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
  margin-bottom: 16px;
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

.header-actions {
  display: flex;
  gap: 8px;
}

.page-tabs {
  flex: 1;
  min-height: 0;
  display: flex;
  flex-direction: column;
  background: #fff;
  border-radius: 10px;
  overflow: hidden;
}

.page-tabs :deep(.el-tabs__content) {
  flex: 1;
  min-height: 0;
  padding: 20px;
  display: flex;
  flex-direction: column;
  overflow: hidden;
}

.page-tabs :deep(.el-tab-pane) {
  flex: 1;
  min-height: 0;
  display: flex;
  flex-direction: column;
  overflow: hidden;
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
  background: #f5f7fa;
  border-radius: 10px;
  padding: 18px 20px;
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
  background: #f5f7fa;
  border-radius: 10px;
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

.no-expiry {
  color: #c0c4cc;
}

.expiry-warning {
  color: #e6a23c;
  font-weight: 600;
}

.expiry-danger {
  color: #f56c6c;
  font-weight: 600;
}

.expiry-badge {
  font-size: 11px;
}

.page-footer {
  display: flex;
  gap: 12px;
  padding-top: 16px;
  flex-shrink: 0;
  justify-content: center;
}
</style>

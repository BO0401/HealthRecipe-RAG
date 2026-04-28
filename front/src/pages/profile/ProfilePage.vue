<script setup lang="ts">
import { onMounted, ref } from 'vue'
import { useProfileStore } from '../../stores/profile'
import {
  User,
  Plus,
  Delete,
  Check,
  Warning
} from '@element-plus/icons-vue'

const profileStore = useProfileStore()

const inventoryName = ref('')
const inventoryQuantity = ref(1)

const formRef = ref()

const rules = {
  height: [
    { required: true, message: '请输入身高', trigger: 'blur' },
    { type: 'number', min: 50, max: 250, message: '身高范围 50-250 cm', trigger: 'blur' }
  ],
  weight: [
    { required: true, message: '请输入体重', trigger: 'blur' },
    { type: 'number', min: 20, max: 300, message: '体重范围 20-300 kg', trigger: 'blur' }
  ]
}

const handleAddInventory = () => {
  const name = inventoryName.value.trim()
  if (!name) return
  if (profileStore.inventory.some(i => i.name === name)) {
    return
  }
  profileStore.addInventoryItem({ name, quantity: inventoryQuantity.value })
  inventoryName.value = ''
  inventoryQuantity.value = 1
}

const handleSave = async () => {
  if (!formRef.value) return
  try {
    await formRef.value.validate()
    await profileStore.saveProfile()
  } catch {
    // validation failed
  }
}

onMounted(() => {
  profileStore.fetchProfile()
})
</script>

<template>
  <div class="profile-page" v-loading="profileStore.loading">
    <div class="page-header">
      <div class="header-left">
        <h2 class="page-title">个人中心</h2>
        <p class="page-subtitle">管理个人信息和健康数据</p>
      </div>
      <div v-if="profileStore.lastSaved" class="last-saved">
        <el-icon :size="14" color="#67c23a"><Check /></el-icon>
        <span>上次保存：{{ profileStore.lastSaved }}</span>
      </div>
    </div>

    <div class="user-banner">
      <div class="user-avatar">
        <el-icon :size="32"><User /></el-icon>
      </div>
      <div class="user-info">
        <h3 class="user-name">用户</h3>
        <p class="user-desc">管理你的身体指标与食材库存</p>
      </div>
      <div class="user-stats">
        <div class="stat-item">
          <span class="stat-value">{{ profileStore.user.height || '-' }}</span>
          <span class="stat-label">身高 (cm)</span>
        </div>
        <div class="stat-divider" />
        <div class="stat-item">
          <span class="stat-value">{{ profileStore.user.weight || '-' }}</span>
          <span class="stat-label">体重 (kg)</span>
        </div>
        <div class="stat-divider" />
        <div class="stat-item">
          <span class="stat-value">{{ profileStore.inventory.length }}</span>
          <span class="stat-label">食材种类</span>
        </div>
      </div>
    </div>

    <div class="form-layout">
      <div class="form-section">
        <h3 class="section-title">身体指标</h3>
        <el-form
          ref="formRef"
          :model="profileStore.user"
          :rules="rules"
          label-width="80px"
          class="body-form"
        >
          <el-form-item label="身高" prop="height">
            <el-input-number
              v-model="profileStore.user.height"
              :min="50"
              :max="250"
              :step="1"
              controls-position="right"
              style="width: 100%"
            />
            <span class="form-unit">cm</span>
          </el-form-item>
          <el-form-item label="体重" prop="weight">
            <el-input-number
              v-model="profileStore.user.weight"
              :min="20"
              :max="300"
              :step="0.5"
              controls-position="right"
              style="width: 100%"
            />
            <span class="form-unit">kg</span>
          </el-form-item>
          <el-form-item label="过敏原">
            <el-checkbox-group v-model="profileStore.user.allergens">
              <el-checkbox
                v-for="opt in profileStore.allergenOptions"
                :key="opt.value"
                :label="opt.value"
                :value="opt.value"
              >
                {{ opt.label }}
              </el-checkbox>
            </el-checkbox-group>
          </el-form-item>
        </el-form>
      </div>

      <div class="form-section">
        <h3 class="section-title">库存管理</h3>
        <div class="inventory-add">
          <el-input
            v-model="inventoryName"
            placeholder="食材名称"
            style="flex: 1"
            @keydown.enter="handleAddInventory"
          />
          <el-input-number
            v-model="inventoryQuantity"
            :min="1"
            :max="999"
            controls-position="right"
            style="width: 120px"
          />
          <el-button
            type="primary"
            :icon="Plus"
            :disabled="!inventoryName.trim()"
            @click="handleAddInventory"
          >
            添加
          </el-button>
        </div>
        <div class="inventory-list">
          <div
            v-for="(item, index) in profileStore.inventory"
            :key="index"
            class="inventory-item"
          >
            <div class="inventory-info">
              <span class="inventory-name">{{ item.name }}</span>
              <span class="inventory-qty">x{{ item.quantity }}</span>
            </div>
            <el-button
              type="danger"
              size="small"
              :icon="Delete"
              circle
              @click="profileStore.removeInventoryItem(index)"
            />
          </div>
          <div v-if="profileStore.inventory.length === 0" class="inventory-empty">
            <el-icon :size="20" color="#c0c4cc"><Warning /></el-icon>
            <p>暂无库存食材，请添加</p>
          </div>
        </div>
      </div>
    </div>

    <div v-if="profileStore.error" class="error-bar">
      <el-alert
        :title="profileStore.error"
        type="error"
        show-icon
        closable
      />
    </div>

    <div class="page-footer">
      <el-button
        type="primary"
        size="large"
        :icon="Check"
        :loading="profileStore.saving"
        @click="handleSave"
      >
        保存
      </el-button>
    </div>
  </div>
</template>

<style scoped>
.profile-page {
  width: 100%;
  height: 100%;
  display: flex;
  flex-direction: column;
  padding: 24px;
  background: #f0f2f5;
  box-sizing: border-box;
  overflow-y: auto;
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

.last-saved {
  display: flex;
  align-items: center;
  gap: 4px;
  font-size: 12px;
  color: #67c23a;
}

.user-banner {
  display: flex;
  align-items: center;
  gap: 20px;
  background: linear-gradient(135deg, #409eff, #337ecc);
  border-radius: 12px;
  padding: 24px 32px;
  color: #fff;
  margin-bottom: 24px;
  flex-shrink: 0;
}

.user-avatar {
  width: 64px;
  height: 64px;
  border-radius: 50%;
  background: rgba(255, 255, 255, 0.2);
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
}

.user-info {
  flex: 1;
}

.user-name {
  font-size: 20px;
  font-weight: 700;
  margin: 0 0 4px;
}

.user-desc {
  font-size: 13px;
  margin: 0;
  opacity: 0.85;
}

.user-stats {
  display: flex;
  align-items: center;
  gap: 20px;
}

.stat-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 2px;
}

.stat-value {
  font-size: 22px;
  font-weight: 700;
}

.stat-label {
  font-size: 12px;
  opacity: 0.85;
}

.stat-divider {
  width: 1px;
  height: 36px;
  background: rgba(255, 255, 255, 0.3);
}

.form-layout {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 20px;
  flex-shrink: 0;
}

.form-section {
  background: #fff;
  border-radius: 12px;
  padding: 24px;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.06);
}

.section-title {
  display: flex;
  align-items: center;
  gap: 6px;
  font-size: 16px;
  font-weight: 600;
  color: #303133;
  margin: 0 0 20px;
  padding-bottom: 12px;
  border-bottom: 1px solid #f0f0f0;
}

.body-form {
  max-width: 360px;
}

.form-unit {
  margin-left: 8px;
  font-size: 13px;
  color: #909399;
  white-space: nowrap;
}

.inventory-add {
  display: flex;
  gap: 8px;
  margin-bottom: 16px;
}

.inventory-list {
  display: flex;
  flex-direction: column;
  gap: 8px;
  max-height: 320px;
  overflow-y: auto;
}

.inventory-item {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 10px 12px;
  border-radius: 8px;
  background: #f5f7fa;
  transition: background 0.2s;
}

.inventory-item:hover {
  background: #ecf5ff;
}

.inventory-info {
  display: flex;
  align-items: center;
  gap: 12px;
}

.inventory-name {
  font-size: 14px;
  font-weight: 500;
  color: #303133;
}

.inventory-qty {
  font-size: 13px;
  color: #909399;
}

.inventory-empty {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 8px;
  padding: 32px 0;
  color: #c0c4cc;
}

.inventory-empty p {
  margin: 0;
  font-size: 13px;
}

.error-bar {
  margin-top: 16px;
  flex-shrink: 0;
}

.page-footer {
  display: flex;
  justify-content: center;
  padding-top: 24px;
  flex-shrink: 0;
}
</style>

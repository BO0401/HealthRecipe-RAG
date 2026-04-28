import { defineStore } from 'pinia'
import { ref, reactive } from 'vue'
import { profileApi } from '../api/profile'
import type { UserProfile, InventoryItem } from '../types/api'
import { ALLERGEN_OPTIONS_PROFILE_CN } from '../constants/allergens'

export const useProfileStore = defineStore('profile', () => {
  const loading = ref(false)
  const saving = ref(false)
  const error = ref('')
  const lastSaved = ref('')

  const user = reactive<UserProfile>({
    height: 0,
    weight: 0,
    allergens: []
  })

  const inventory = ref<InventoryItem[]>([])

  const allergenOptions = ALLERGEN_OPTIONS_PROFILE_CN

  const fetchProfile = async () => {
    loading.value = true
    error.value = ''

    try {
      const data = await profileApi.get()

      user.height = Number(data.user.height || 0)
      user.weight = Number(data.user.weight || 0)
      user.allergens = Array.isArray(data.user.allergens) ? data.user.allergens.filter(Boolean) : []
      inventory.value = Array.isArray(data.inventory) ? data.inventory : []
    } catch (err: any) {
      error.value = '获取个人信息失败，请稍后重试'
      console.error('[profile] fetch error:', err)
    } finally {
      loading.value = false
    }
  }

  const saveProfile = async () => {
    saving.value = true
    error.value = ''

    try {
      await profileApi.save({
        user: {
          height: user.height,
          weight: user.weight,
          allergens: [...user.allergens]
        },
        inventory: inventory.value.map(i => ({ ...i }))
      })
      lastSaved.value = new Date().toLocaleString('zh-CN')
    } catch (err: any) {
      error.value = '保存失败，请稍后重试'
      console.error('[profile] save error:', err)
    } finally {
      saving.value = false
    }
  }

  const addInventoryItem = (item: InventoryItem) => {
    inventory.value.push({ ...item })
  }

  const removeInventoryItem = (index: number) => {
    inventory.value.splice(index, 1)
  }

  return {
    loading,
    saving,
    error,
    lastSaved,
    user,
    inventory,
    allergenOptions,
    fetchProfile,
    saveProfile,
    addInventoryItem,
    removeInventoryItem
  }
})

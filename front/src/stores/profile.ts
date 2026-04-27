import { defineStore } from 'pinia'
import { ref, reactive } from 'vue'
import { profileApi, type UserProfile, type InventoryItem } from '../api/profile'

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

  const allergenOptions = [
    { value: '牛奶', label: '牛奶' },
    { value: '鸡蛋', label: '鸡蛋' },
    { value: '花生', label: '花生' },
    { value: '大豆', label: '大豆' },
    { value: '小麦', label: '小麦' },
    { value: '鱼类', label: '鱼类' },
    { value: '海鲜', label: '海鲜' },
    { value: '坚果', label: '坚果' }
  ]

  const fetchProfile = async () => {
    loading.value = true
    error.value = ''

    try {
      const res = await profileApi.get()
      const data = res.data

      user.height = data.user.height
      user.weight = data.user.weight
      user.allergens = data.user.allergens
      inventory.value = data.inventory
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

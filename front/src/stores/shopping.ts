import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import { shoppingApi, type ShoppingListVO } from '../api/shopping'

export interface ShoppingItem {
  id: number
  name: string
  category: string
  quantity: number
  unit: string
  estimatedPrice: number
  status: 'pending' | 'purchased' | 'excluded'
  source?: string
}

const STATUS_MAP: Record<number, ShoppingItem['status']> = {
  0: 'pending',
  1: 'purchased'
}

const toItemStatus = (status: number): ShoppingItem['status'] =>
  STATUS_MAP[status] || 'pending'

const toApiStatus = (status: ShoppingItem['status']): number =>
  status === 'purchased' ? 1 : 0

export const useShoppingStore = defineStore('shopping', () => {
  const items = ref<ShoppingItem[]>([])
  const loading = ref(false)
  const error = ref('')
  const excludedCount = ref(0)

  const totalItems = computed(() => items.value.length)
  const pendingItems = computed(() => items.value.filter(i => i.status === 'pending'))
  const purchasedItems = computed(() => items.value.filter(i => i.status === 'purchased'))
  const totalEstimatedCost = computed(() =>
    items.value
      .filter(i => i.status !== 'excluded')
      .reduce((sum, i) => sum + i.estimatedPrice * i.quantity, 0)
  )

  const fetchShoppingList = async () => {
    loading.value = true
    error.value = ''

    try {
      const res = await shoppingApi.list()
      const list = res.data

      items.value = list.map(item => ({
        id: item.id,
        name: item.ingredientName,
        category: '',
        quantity: item.quantity,
        unit: item.unit,
        estimatedPrice: 0,
        status: toItemStatus(item.status)
      }))
    } catch (err: any) {
      error.value = '获取采购清单失败，请稍后重试'
      console.error('[shopping store] fetch error:', err)
    } finally {
      loading.value = false
    }
  }

  const toggleStatus = async (id: number) => {
    const item = items.value.find(i => i.id === id)
    if (!item || item.status === 'excluded') return

    const newStatus: ShoppingItem['status'] =
      item.status === 'pending' ? 'purchased' : 'pending'

    try {
      await shoppingApi.update({ id, status: toApiStatus(newStatus) })
      item.status = newStatus
    } catch (err: any) {
      console.error('[shopping store] toggle status error:', err)
    }
  }

  const removeItem = async (id: number) => {
    try {
      await shoppingApi.remove(id)
      items.value = items.value.filter(i => i.id !== id)
    } catch (err: any) {
      console.error('[shopping store] remove error:', err)
    }
  }

  const clearPurchased = async () => {
    try {
      await shoppingApi.clearPurchased()
      items.value = items.value.filter(i => i.status !== 'purchased')
    } catch (err: any) {
      console.error('[shopping store] clear purchased error:', err)
    }
  }

  const addItemsFromRecipe = async (ingredients: { name: string; quantity: number; unit: string }[]) => {
    try {
      const res = await shoppingApi.batchAdd(
        ingredients.map(ing => ({
          ingredientName: ing.name,
          quantity: ing.quantity,
          unit: ing.unit
        }))
      )
      const created = res.data
      const newItems: ShoppingItem[] = created.map(item => ({
        id: item.id,
        name: item.ingredientName,
        category: '',
        quantity: item.quantity,
        unit: item.unit,
        estimatedPrice: 0,
        status: toItemStatus(item.status)
      }))
      items.value.push(...newItems)
    } catch (err: any) {
      console.error('[shopping store] add from recipe error:', err)
    }
  }

  const generateFromRecipes = async (recipeIds: number[]) => {
    loading.value = true
    error.value = ''

    try {
      const res = await shoppingApi.generateFromRecipes(recipeIds)
      const list = res.data

      items.value = list.map(item => ({
        id: item.id,
        name: item.ingredientName,
        category: '',
        quantity: item.quantity,
        unit: item.unit,
        estimatedPrice: 0,
        status: toItemStatus(item.status)
      }))
    } catch (err: any) {
      error.value = '生成采购清单失败，请稍后重试'
      console.error('[shopping store] generate error:', err)
    } finally {
      loading.value = false
    }
  }

  return {
    items,
    loading,
    error,
    excludedCount,
    totalItems,
    pendingItems,
    purchasedItems,
    totalEstimatedCost,
    fetchShoppingList,
    toggleStatus,
    removeItem,
    clearPurchased,
    addItemsFromRecipe,
    generateFromRecipes
  }
})

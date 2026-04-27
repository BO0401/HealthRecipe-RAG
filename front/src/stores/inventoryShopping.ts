import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import { shoppingApi } from '../api/shopping'
import { inventoryApi, type InventoryVO } from '../api/inventory'

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

export interface InventoryItem {
  id: number
  name: string
  category: string
  quantity: number
  unit: string
  expiryDate?: string
  addedDate: string
  location: 'fridge' | 'freezer' | 'pantry'
}

const STATUS_MAP: Record<number, ShoppingItem['status']> = {
  0: 'pending',
  1: 'purchased'
}

const toItemStatus = (status: number): ShoppingItem['status'] =>
  STATUS_MAP[status] || 'pending'

const toApiStatus = (status: ShoppingItem['status']): number =>
  status === 'purchased' ? 1 : 0

const toInventoryItem = (vo: InventoryVO): InventoryItem => ({
  id: vo.id,
  name: vo.ingredientName,
  category: vo.category || '',
  quantity: vo.quantity,
  unit: vo.unit,
  expiryDate: vo.expireDate,
  addedDate: vo.createTime || new Date().toISOString().split('T')[0],
  location: 'pantry'
})

export const useInventoryShoppingStore = defineStore('inventoryShopping', () => {
  const shoppingItems = ref<ShoppingItem[]>([])
  const inventoryItems = ref<InventoryItem[]>([])
  const loading = ref(false)
  const error = ref('')
  const excludedCount = ref(0)

  const totalShoppingItems = computed(() => shoppingItems.value.length)
  const pendingItems = computed(() => shoppingItems.value.filter(i => i.status === 'pending'))
  const purchasedItems = computed(() => shoppingItems.value.filter(i => i.status === 'purchased'))
  const totalEstimatedCost = computed(() =>
    shoppingItems.value
      .filter(i => i.status !== 'excluded')
      .reduce((sum, i) => sum + i.estimatedPrice * i.quantity, 0)
  )

  const totalInventoryItems = computed(() => inventoryItems.value.length)
  const expiringSoonItems = computed(() =>
    inventoryItems.value.filter(item => {
      if (!item.expiryDate) return false
      const daysLeft = (new Date(item.expiryDate).getTime() - Date.now()) / (1000 * 60 * 60 * 24)
      return daysLeft >= 0 && daysLeft <= 3
    })
  )

  const fetchShoppingList = async () => {
    loading.value = true
    error.value = ''

    try {
      const res = await shoppingApi.list()
      const list = res.data

      shoppingItems.value = list.map(item => ({
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
      console.error('[inventoryShopping] fetch shopping error:', err)
    } finally {
      loading.value = false
    }
  }

  const toggleShoppingStatus = async (id: number) => {
    const item = shoppingItems.value.find(i => i.id === id)
    if (!item || item.status === 'excluded') return

    const newStatus: ShoppingItem['status'] =
      item.status === 'pending' ? 'purchased' : 'pending'

    try {
      await shoppingApi.update({ id, status: toApiStatus(newStatus) })
      item.status = newStatus
    } catch (err: any) {
      console.error('[inventoryShopping] toggle status error:', err)
    }
  }

  const removeShoppingItem = async (id: number) => {
    try {
      await shoppingApi.remove(id)
      shoppingItems.value = shoppingItems.value.filter(i => i.id !== id)
    } catch (err: any) {
      console.error('[inventoryShopping] remove error:', err)
    }
  }

  const clearPurchased = async () => {
    try {
      await shoppingApi.clearPurchased()
      shoppingItems.value = shoppingItems.value.filter(i => i.status !== 'purchased')
    } catch (err: any) {
      console.error('[inventoryShopping] clear purchased error:', err)
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
      shoppingItems.value.push(...newItems)
    } catch (err: any) {
      console.error('[inventoryShopping] add from recipe error:', err)
    }
  }

  const generateFromRecipes = async (recipeIds: number[]) => {
    loading.value = true
    error.value = ''

    try {
      const res = await shoppingApi.generateFromRecipes(recipeIds)
      const list = res.data

      shoppingItems.value = list.map(item => ({
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
      console.error('[inventoryShopping] generate error:', err)
    } finally {
      loading.value = false
    }
  }

  const fetchInventoryList = async () => {
    loading.value = true
    error.value = ''

    try {
      const res = await inventoryApi.list()
      inventoryItems.value = res.data.map(toInventoryItem)
    } catch (err: any) {
      error.value = '获取库存列表失败，请稍后重试'
      console.error('[inventoryShopping] fetch inventory error:', err)
    } finally {
      loading.value = false
    }
  }

  const addInventoryItem = async (item: Omit<InventoryItem, 'id'>) => {
    try {
      const res = await inventoryApi.add({
        ingredientName: item.name,
        quantity: item.quantity,
        unit: item.unit,
        expireDate: item.expiryDate
      })
      inventoryItems.value.push(toInventoryItem(res.data))
    } catch (err: any) {
      console.error('[inventoryShopping] add inventory error:', err)
    }
  }

  const removeInventoryItem = async (id: number) => {
    try {
      await inventoryApi.remove(id)
      inventoryItems.value = inventoryItems.value.filter(i => i.id !== id)
    } catch (err: any) {
      console.error('[inventoryShopping] remove inventory error:', err)
    }
  }

  return {
    shoppingItems,
    inventoryItems,
    loading,
    error,
    excludedCount,
    totalShoppingItems,
    pendingItems,
    purchasedItems,
    totalEstimatedCost,
    totalInventoryItems,
    expiringSoonItems,
    fetchShoppingList,
    toggleShoppingStatus,
    removeShoppingItem,
    clearPurchased,
    addItemsFromRecipe,
    generateFromRecipes,
    fetchInventoryList,
    addInventoryItem,
    removeInventoryItem
  }
})

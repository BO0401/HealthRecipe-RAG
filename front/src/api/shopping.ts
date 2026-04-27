import axios from 'axios'

const http = axios.create({
  baseURL: 'http://localhost:8080',
  timeout: 10000,
  headers: { 'Content-Type': 'application/json' }
})

export interface ShoppingListVO {
  id: number
  userId: number
  ingredientName: string
  quantity: number
  unit: string
  status: number
  createTime?: string
  updateTime?: string
}

export interface ShoppingListCreateDTO {
  ingredientName: string
  quantity: number
  unit: string
}

export interface ShoppingListUpdateDTO {
  id: number
  quantity?: number
  unit?: string
  status?: number
}

export const shoppingApi = {
  list: () =>
    http.get<ShoppingListVO[]>('/api/shopping/list'),

  add: (data: ShoppingListCreateDTO) =>
    http.post<ShoppingListVO>('/api/shopping/add', data),

  batchAdd: (items: ShoppingListCreateDTO[]) =>
    http.post<ShoppingListVO[]>('/api/shopping/batch-add', items),

  update: (data: ShoppingListUpdateDTO) =>
    http.put<ShoppingListVO>('/api/shopping/update', data),

  remove: (id: number) =>
    http.delete(`/api/shopping/remove/${id}`),

  clearPurchased: () =>
    http.delete('/api/shopping/clear-purchased'),

  generateFromRecipes: (recipeIds: number[]) =>
    http.post<ShoppingListVO[]>('/api/shopping/generate', { recipeIds })
}

import http from './http'

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
    http.get<ShoppingListVO[]>('/shopping/list'),

  add: (data: ShoppingListCreateDTO) =>
    http.post<ShoppingListVO>('/shopping/add', data),

  batchAdd: (items: ShoppingListCreateDTO[]) =>
    http.post<ShoppingListVO[]>('/shopping/batch-add', items),

  update: (data: ShoppingListUpdateDTO) =>
    http.put<ShoppingListVO>('/shopping/update', data),

  remove: (id: number) =>
    http.delete(`/shopping/remove/${id}`),

  clearPurchased: () =>
    http.delete('/shopping/clear-purchased'),

  generateFromRecipes: (recipeIds: number[]) =>
    http.post<ShoppingListVO[]>('/shopping/generate', { recipeIds })
}

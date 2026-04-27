import http from './http'

export interface InventoryVO {
  id: number
  userId: number
  ingredientId: number
  ingredientName: string
  category: string
  quantity: number
  unit: string
  expireDate?: string
  createTime?: string
  updateTime?: string
}

export interface InventoryCreateDTO {
  ingredientName: string
  quantity: number
  unit: string
  expireDate?: string
}

export interface InventoryUpdateDTO {
  id: number
  quantity?: number
  unit?: string
  expireDate?: string
}

export const inventoryApi = {
  list: () =>
    http.get<InventoryVO[]>('/inventory/list'),

  add: (data: InventoryCreateDTO) =>
    http.post<InventoryVO>('/inventory/add', data),

  update: (data: InventoryUpdateDTO) =>
    http.put<InventoryVO>('/inventory/update', data),

  remove: (id: number) =>
    http.delete(`/inventory/remove/${id}`),

  getExpiringSoon: () =>
    http.get<InventoryVO[]>('/inventory/expiring-soon')
}

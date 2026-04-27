import http from './http'
import { ShoppingListVOSchema, ShoppingListCreateDTOSchema, type ShoppingListVO, type ShoppingListCreateDTO, type ShoppingListUpdateDTO } from '../types/api'

export const shoppingApi = {
  list: async (): Promise<ShoppingListVO[]> => {
    const data = await http.get<ShoppingListVO[]>('/shopping/list')
    return data.map(item => ShoppingListVOSchema.parse(item))
  },

  add: async (dto: ShoppingListCreateDTO): Promise<ShoppingListVO> => {
    ShoppingListCreateDTOSchema.parse(dto)
    const data = await http.post<ShoppingListVO>('/shopping/add', dto)
    return ShoppingListVOSchema.parse(data)
  },

  batchAdd: async (items: ShoppingListCreateDTO[]): Promise<ShoppingListVO[]> => {
    items.forEach(item => ShoppingListCreateDTOSchema.parse(item))
    const data = await http.post<ShoppingListVO[]>('/shopping/batch-add', items)
    return data.map(item => ShoppingListVOSchema.parse(item))
  },

  update: async (dto: ShoppingListUpdateDTO): Promise<ShoppingListVO> => {
    const data = await http.put<ShoppingListVO>('/shopping/update', dto)
    return ShoppingListVOSchema.parse(data)
  },

  remove: (id: number) =>
    http.delete(`/shopping/remove/${id}`),

  clearPurchased: () =>
    http.delete('/shopping/clear-purchased'),

  generateFromRecipes: async (recipeIds: number[]): Promise<ShoppingListVO[]> => {
    const data = await http.post<ShoppingListVO[]>('/shopping/generate', { recipeIds })
    return data.map(item => ShoppingListVOSchema.parse(item))
  }
}

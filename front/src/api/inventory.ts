import http from './http'
import { InventoryVOSchema, InventoryCreateDTOSchema, type InventoryVO, type InventoryCreateDTO, type InventoryUpdateDTO } from '../types/api'

export const inventoryApi = {
  list: async (): Promise<InventoryVO[]> => {
    const data = await http.get<InventoryVO[]>('/inventory/list')
    return data.map(item => InventoryVOSchema.parse(item))
  },

  add: async (dto: InventoryCreateDTO): Promise<InventoryVO> => {
    InventoryCreateDTOSchema.parse(dto)
    const data = await http.post<InventoryVO>('/inventory/add', dto)
    return InventoryVOSchema.parse(data)
  },

  update: async (dto: InventoryUpdateDTO): Promise<InventoryVO> => {
    const data = await http.put<InventoryVO>('/inventory/update', dto)
    return InventoryVOSchema.parse(data)
  },

  remove: (id: number) =>
    http.delete(`/inventory/remove/${id}`),

  getExpiringSoon: async (): Promise<InventoryVO[]> => {
    const data = await http.get<InventoryVO[]>('/inventory/expiring-soon')
    return data.map(item => InventoryVOSchema.parse(item))
  }
}

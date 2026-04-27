import http from './http'
import { RecipeVOSchema, RecipeCreateDTOSchema, type RecipeVO, type RecipeQueryDTO, type RecipeCreateDTO } from '../types/api'

export const recipeApi = {
  list: async (params?: RecipeQueryDTO): Promise<RecipeVO[]> => {
    const data = await http.get<RecipeVO[]>('/recipe/list', { params })
    return data.map(item => RecipeVOSchema.parse(item))
  },

  recommend: async (params?: RecipeQueryDTO): Promise<RecipeVO[]> => {
    const data = await http.get<RecipeVO[]>('/recipe/recommend', { params })
    return data.map(item => RecipeVOSchema.parse(item))
  },

  getById: async (id: number): Promise<RecipeVO> => {
    const data = await http.get<RecipeVO>(`/recipe/${id}`)
    return RecipeVOSchema.parse(data)
  },

  create: async (dto: RecipeCreateDTO): Promise<RecipeVO> => {
    RecipeCreateDTOSchema.parse(dto)
    const data = await http.post<RecipeVO>('/recipe/create', dto)
    return RecipeVOSchema.parse(data)
  },

  update: async (id: number, dto: Partial<RecipeCreateDTO>): Promise<RecipeVO> => {
    const data = await http.put<RecipeVO>(`/recipe/${id}`, dto)
    return RecipeVOSchema.parse(data)
  },

  remove: (id: number) =>
    http.delete(`/recipe/${id}`),

  addToShoppingList: (recipeId: number) =>
    http.post(`/recipe/${recipeId}/add-to-shopping-list`)
}

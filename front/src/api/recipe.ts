import http from './http'

export interface RecipeVO {
  id: number
  name: string
  coverImg: string
  calories: number
  cookTime: number
  difficulty: string
  tags: string[]
  description: string
  steps: string
}

export interface RecipeQueryDTO {
  bodyType?: string
  allergens?: string[]
  keyword?: string
}

export interface RecipeCreateDTO {
  name: string
  coverImg?: string
  calories?: number
  cookTime?: number
  difficulty?: string
  tags?: string[]
  description?: string
  steps?: string
}

export const recipeApi = {
  list: (params?: RecipeQueryDTO) =>
    http.get<RecipeVO[]>('/recipe/list', { params }),

  recommend: (params?: RecipeQueryDTO) =>
    http.get<RecipeVO[]>('/recipe/recommend', { params }),

  getById: (id: number) =>
    http.get<RecipeVO>(`/recipe/${id}`),

  create: (data: RecipeCreateDTO) =>
    http.post<RecipeVO>('/recipe/create', data),

  update: (id: number, data: Partial<RecipeCreateDTO>) =>
    http.put<RecipeVO>(`/recipe/${id}`, data),

  remove: (id: number) =>
    http.delete(`/recipe/${id}`),

  addToShoppingList: (recipeId: number) =>
    http.post(`/recipe/${recipeId}/add-to-shopping-list`)
}

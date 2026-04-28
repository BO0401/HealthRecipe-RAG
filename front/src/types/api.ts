import { z } from 'zod'

export const DashboardMetricsVOSchema = z.object({
  todayCalories: z.number().optional().nullable(),
  remainingIngredients: z.number().optional().nullable(),
  expiringCount: z.number().optional().nullable(),
  pendingTasks: z.number().optional().nullable(),
  healthScore: z.number().optional().nullable()
})

export type DashboardMetricsVO = z.infer<typeof DashboardMetricsVOSchema>

export const RecipeVOSchema = z.object({
  id: z.number(),
  name: z.string(),
  coverImg: z.string().optional().nullable(),
  calories: z.number().optional().nullable(),
  cookTime: z.number().optional().nullable(),
  difficulty: z.string().optional().nullable(),
  tags: z.array(z.string()).optional().nullable(),
  description: z.string().optional().nullable(),
  steps: z.string().optional().nullable()
})

export type RecipeVO = z.infer<typeof RecipeVOSchema>

export const RecipeQueryDTOSchema = z.object({
  bodyType: z.string().optional(),
  allergens: z.array(z.string()).optional(),
  keyword: z.string().optional()
})

export type RecipeQueryDTO = z.infer<typeof RecipeQueryDTOSchema>

export const RecipeCreateDTOSchema = z.object({
  name: z.string().min(1, '食谱名称不能为空'),
  coverImg: z.string().optional(),
  calories: z.number().optional(),
  cookTime: z.number().optional(),
  difficulty: z.string().optional(),
  tags: z.array(z.string()).optional(),
  description: z.string().optional(),
  steps: z.string().optional()
})

export type RecipeCreateDTO = z.infer<typeof RecipeCreateDTOSchema>

export const InventoryVOSchema = z.object({
  id: z.number(),
  userId: z.number().optional().nullable(),
  ingredientId: z.number().optional().nullable(),
  ingredientName: z.string(),
  category: z.string().optional().nullable(),
  quantity: z.number(),
  unit: z.string(),
  expireDate: z.string().optional(),
  createTime: z.string().optional(),
  updateTime: z.string().optional()
})

export type InventoryVO = z.infer<typeof InventoryVOSchema>

export const InventoryCreateDTOSchema = z.object({
  ingredientName: z.string().min(1, '食材名称不能为空'),
  quantity: z.number().positive('数量必须大于0'),
  unit: z.string().min(1, '单位不能为空'),
  expireDate: z.string().optional()
})

export type InventoryCreateDTO = z.infer<typeof InventoryCreateDTOSchema>

export const InventoryUpdateDTOSchema = z.object({
  id: z.number(),
  quantity: z.number().optional(),
  unit: z.string().optional(),
  expireDate: z.string().optional()
})

export type InventoryUpdateDTO = z.infer<typeof InventoryUpdateDTOSchema>

export const ShoppingListVOSchema = z.object({
  id: z.number(),
  userId: z.number().optional().nullable(),
  ingredientName: z.string(),
  quantity: z.number(),
  unit: z.string(),
  status: z.number(),
  createTime: z.string().optional(),
  updateTime: z.string().optional()
})

export type ShoppingListVO = z.infer<typeof ShoppingListVOSchema>

export const ShoppingListCreateDTOSchema = z.object({
  ingredientName: z.string().min(1, '食材名称不能为空'),
  quantity: z.number().positive('数量必须大于0'),
  unit: z.string().min(1, '单位不能为空')
})

export type ShoppingListCreateDTO = z.infer<typeof ShoppingListCreateDTOSchema>

export const ShoppingListUpdateDTOSchema = z.object({
  id: z.number(),
  quantity: z.number().optional(),
  unit: z.string().optional(),
  status: z.number().optional()
})

export type ShoppingListUpdateDTO = z.infer<typeof ShoppingListUpdateDTOSchema>

export const UserProfileSchema = z.object({
  height: z.number().optional().nullable(),
  weight: z.number().optional().nullable(),
  allergens: z.array(z.string()).optional().nullable()
})

export type UserProfile = z.infer<typeof UserProfileSchema>

export const InventoryItemSchema = z.object({
  name: z.string(),
  quantity: z.number()
})

export type InventoryItem = z.infer<typeof InventoryItemSchema>

export const ProfileDataSchema = z.object({
  user: UserProfileSchema,
  inventory: z.array(InventoryItemSchema).optional().nullable()
})

export type ProfileData = z.infer<typeof ProfileDataSchema>

export const UploadedDocSchema = z.object({
  id: z.string(),
  name: z.string(),
  size: z.number(),
  uploadTime: z.string()
})

export type UploadedDoc = z.infer<typeof UploadedDocSchema>

export const ChatMessageSchema = z.object({
  role: z.enum(['system', 'user', 'assistant']),
  content: z.string()
})

export type ChatMessage = z.infer<typeof ChatMessageSchema>

export const AIConfigSchema = z.object({
  baseUrl: z.string().url('请输入有效的API地址'),
  apiKey: z.string().min(1, 'API密钥不能为空'),
  model: z.string().min(1, '模型名称不能为空'),
  temperature: z.number().min(0).max(2),
  timeout: z.number().positive()
})

export type AIConfig = z.infer<typeof AIConfigSchema>

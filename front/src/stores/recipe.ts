import { defineStore } from 'pinia'
import { ref, reactive } from 'vue'
import { recipeApi, type RecipeVO } from '../api/recipe'

export interface RecipeItem {
  id: number
  name: string
  image: string
  calories: number
  cookingTime: number
  tags: string[]
  description: string
}

export interface FilterState {
  bodyType: string
  allergens: string[]
  keyword: string
}

const toRecipeItem = (vo: RecipeVO): RecipeItem => ({
  id: vo.id,
  name: vo.name,
  image: vo.coverImg || 'https://placehold.co/400x300/409EFF/FFFFFF?text=美食',
  calories: vo.calories || 0,
  cookingTime: vo.cookTime || 0,
  tags: vo.tags || [],
  description: vo.description || ''
})

export const useRecipeStore = defineStore('recipe', () => {
  const recipes = ref<RecipeItem[]>([])
  const loading = ref(false)
  const error = ref('')

  const filter = reactive<FilterState>({
    bodyType: '',
    allergens: [],
    keyword: ''
  })

  const bodyTypeOptions = [
    { value: 'balanced', label: '均衡型' },
    { value: 'weight_loss', label: '减脂型' },
    { value: 'muscle_gain', label: '增肌型' },
    { value: 'low_carb', label: '低碳水型' },
    { value: 'high_protein', label: '高蛋白型' }
  ]

  const allergenOptions = [
    { value: 'milk', label: '牛奶' },
    { value: 'egg', label: '鸡蛋' },
    { value: 'peanut', label: '花生' },
    { value: 'soy', label: '大豆' },
    { value: 'wheat', label: '小麦' },
    { value: 'fish', label: '鱼类' },
    { value: 'shellfish', label: '贝壳类' },
    { value: 'tree_nut', label: '坚果' }
  ]

  const fetchRecipes = async () => {
    loading.value = true
    error.value = ''

    try {
      const params: any = {}
      if (filter.bodyType) params.bodyType = filter.bodyType
      if (filter.allergens.length > 0) params.allergens = filter.allergens
      if (filter.keyword) params.keyword = filter.keyword

      const res = await recipeApi.recommend(params)
      recipes.value = res.data.map(toRecipeItem)
    } catch (err) {
      error.value = '获取食谱失败，请稍后重试'
      console.error('[recipe store] fetch error:', err)
    } finally {
      loading.value = false
    }
  }

  const addRecipe = async (recipe: Omit<RecipeItem, 'id'>) => {
    try {
      const res = await recipeApi.create({
        name: recipe.name,
        coverImg: recipe.image,
        calories: recipe.calories,
        cookTime: recipe.cookingTime,
        tags: recipe.tags,
        description: recipe.description
      })
      recipes.value.push(toRecipeItem(res.data))
    } catch (err) {
      console.error('[recipe store] add error:', err)
    }
  }

  const removeRecipe = async (id: number) => {
    try {
      await recipeApi.remove(id)
      recipes.value = recipes.value.filter(r => r.id !== id)
    } catch (err) {
      console.error('[recipe store] remove error:', err)
    }
  }

  const setBodyType = (value: string) => {
    filter.bodyType = value
  }

  const setAllergens = (allergens: string[]) => {
    filter.allergens = allergens
  }

  const setKeyword = (keyword: string) => {
    filter.keyword = keyword
  }

  const filteredRecipes = () => {
    return recipes.value.filter(recipe => {
      if (filter.bodyType) {
        const bodyTagMap: Record<string, string[]> = {
          balanced: [],
          weight_loss: ['减脂', '低卡', '低碳水'],
          muscle_gain: ['增肌', '高蛋白'],
          low_carb: ['低碳水', '低卡'],
          high_protein: ['高蛋白']
        }
        const allowedTags = bodyTagMap[filter.bodyType]
        if (allowedTags && allowedTags.length > 0) {
          const hasMatch = allowedTags.some(tag => recipe.tags.includes(tag))
          if (!hasMatch) return false
        }
      }

      if (filter.allergens.length > 0) {
        const allergenTagMap: Record<string, string[]> = {
          milk: ['牛奶', '乳制品', '奶油', '奶酪'],
          egg: ['鸡蛋', '蛋'],
          peanut: ['花生'],
          soy: ['大豆', '豆腐', '豆浆', '毛豆'],
          wheat: ['小麦', '面粉', '全麦', '全谷物', '面条', '面包'],
          fish: ['鱼', '三文鱼', '金枪鱼', '鳕鱼'],
          shellfish: ['虾', '蟹', '贝', '贝壳'],
          tree_nut: ['坚果', '杏仁', '核桃', '腰果']
        }
        const hasAllergen = filter.allergens.some(allergen => {
          const tags = allergenTagMap[allergen]
          if (!tags) return false
          return tags.some(tag =>
            recipe.name.includes(tag) ||
            recipe.description.includes(tag) ||
            recipe.tags.some(t => t.includes(tag))
          )
        })
        if (hasAllergen) return false
      }

      if (filter.keyword) {
        const kw = filter.keyword.toLowerCase()
        const matchName = recipe.name.toLowerCase().includes(kw)
        const matchDesc = recipe.description.toLowerCase().includes(kw)
        const matchTag = recipe.tags.some(t => t.toLowerCase().includes(kw))
        if (!matchName && !matchDesc && !matchTag) return false
      }

      return true
    })
  }

  const addToShoppingList = async (recipe: RecipeItem) => {
    try {
      await recipeApi.addToShoppingList(recipe.id)
    } catch (err) {
      console.error('[recipe store] add to shopping list error:', err)
    }
  }

  return {
    recipes,
    loading,
    error,
    filter,
    bodyTypeOptions,
    allergenOptions,
    fetchRecipes,
    addRecipe,
    removeRecipe,
    setBodyType,
    setAllergens,
    setKeyword,
    filteredRecipes,
    addToShoppingList
  }
})

import { defineStore } from 'pinia'
import { ref, reactive } from 'vue'

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
      await new Promise(resolve => setTimeout(resolve, 600))

      const mockData: RecipeItem[] = [
        {
          id: 1,
          name: '香煎鸡胸肉配时蔬',
          image: 'https://placehold.co/400x300/409EFF/FFFFFF?text=香煎鸡胸肉',
          calories: 320,
          cookingTime: 25,
          tags: ['高蛋白', '减脂', '快手'],
          description: '低脂高蛋白的鸡胸肉搭配新鲜时蔬，简单调味即可享受健康美味。'
        },
        {
          id: 2,
          name: '藜麦牛油果沙拉',
          image: 'https://placehold.co/400x300/67C23A/FFFFFF?text=藜麦牛油果沙拉',
          calories: 280,
          cookingTime: 15,
          tags: ['素食', '低碳水', '沙拉'],
          description: '超级食物藜麦搭配 creamy 牛油果，营养均衡的清爽沙拉。'
        },
        {
          id: 3,
          name: '三文鱼烤蔬菜',
          image: 'https://placehold.co/400x300/E6A23C/FFFFFF?text=三文鱼烤蔬菜',
          calories: 420,
          cookingTime: 35,
          tags: ['高蛋白', 'Omega-3', '烤箱菜'],
          description: '富含Omega-3的三文鱼搭配烤制时蔬，外酥里嫩的健康之选。'
        },
        {
          id: 4,
          name: '番茄鸡蛋全麦面',
          image: 'https://placehold.co/400x300/F56C6C/FFFFFF?text=番茄鸡蛋面',
          calories: 380,
          cookingTime: 20,
          tags: ['家常', '快手', '全谷物'],
          description: '经典番茄鸡蛋搭配全麦面条，酸甜开胃又富含膳食纤维。'
        },
        {
          id: 5,
          name: '豆腐蔬菜煲',
          image: 'https://placehold.co/400x300/909399/FFFFFF?text=豆腐蔬菜煲',
          calories: 240,
          cookingTime: 30,
          tags: ['素食', '低卡', '暖胃'],
          description: '嫩滑豆腐搭配多种时蔬，清淡鲜美的暖身煲仔菜。'
        },
        {
          id: 6,
          name: '牛肉糙米饭',
          image: 'https://placehold.co/400x300/409EFF/FFFFFF?text=牛肉糙米饭',
          calories: 450,
          cookingTime: 40,
          tags: ['增肌', '高蛋白', '主食'],
          description: '嫩滑牛肉片搭配营养糙米，满足增肌期的能量需求。'
        }
      ]

      recipes.value = mockData
    } catch (err) {
      error.value = '获取食谱失败，请稍后重试'
      console.error('[recipe store] fetch error:', err)
    } finally {
      loading.value = false
    }
  }

  const addRecipe = (recipe: Omit<RecipeItem, 'id'>) => {
    const maxId = recipes.value.reduce((max, r) => Math.max(max, r.id), 0)
    const newRecipe: RecipeItem = {
      ...recipe,
      id: maxId + 1
    }
    recipes.value.push(newRecipe)
  }

  const removeRecipe = (id: number) => {
    recipes.value = recipes.value.filter(r => r.id !== id)
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

  const addToShoppingList = (recipe: RecipeItem) => {
    console.log('[recipe] added to shopping list:', recipe.name)
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

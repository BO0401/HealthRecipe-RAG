export interface OptionItem {
  value: string
  label: string
}

export const ALLERGEN_OPTIONS_PROFILE_CN: OptionItem[] = [
  { value: '牛奶', label: '牛奶' },
  { value: '鸡蛋', label: '鸡蛋' },
  { value: '花生', label: '花生' },
  { value: '大豆', label: '大豆' },
  { value: '小麦', label: '小麦' },
  { value: '鱼类', label: '鱼类' },
  { value: '海鲜', label: '海鲜' },
  { value: '坚果', label: '坚果' }
]

export const ALLERGEN_OPTIONS_RECIPE_CODE: OptionItem[] = [
  { value: 'milk', label: '牛奶' },
  { value: 'egg', label: '鸡蛋' },
  { value: 'peanut', label: '花生' },
  { value: 'soy', label: '大豆' },
  { value: 'wheat', label: '小麦' },
  { value: 'fish', label: '鱼类' },
  { value: 'shellfish', label: '贝壳类' },
  { value: 'tree_nut', label: '坚果' }
]


export const INVENTORY_CATEGORY_LABELS: Record<string, string> = {
  meat: '肉类',
  seafood: '海鲜',
  vegetables: '蔬菜',
  fruits: '水果',
  grains: '谷物',
  eggs: '蛋类',
  beans: '豆制品',
  dairy: '奶制品',
  mushrooms: '菌菇',
  nuts: '坚果',
  condiments: '调味品',
  other: '其他'
}

export const INVENTORY_LOCATION_LABELS: Record<string, string> = {
  fridge: '冷藏',
  freezer: '冷冻',
  pantry: '常温'
}

export const INVENTORY_LOCATION_TAG_TYPES: Record<string, string> = {
  fridge: 'primary',
  freezer: 'info',
  pantry: 'warning'
}

export const SHOPPING_STATUS_LABELS: Record<string, string> = {
  pending: '待采购',
  purchased: '已购买',
  excluded: '已剔除'
}

export const SHOPPING_STATUS_TAG_TYPES: Record<string, string> = {
  pending: 'warning',
  purchased: 'success',
  excluded: 'info'
}


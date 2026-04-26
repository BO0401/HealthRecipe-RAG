import { createRouter, createWebHistory } from 'vue-router'

const routes = [
  {
    path: '/',
    redirect: '/dashboard'
  },
  {
    path: '/dashboard',
    name: 'Dashboard',
    component: () => import('../pages/Dashboard.vue')
  },
  {
    path: '/recipe',
    name: 'Recipe',
    component: () => import('../pages/recipe/RecipePage.vue')
  },
  {
    path: '/inventory',
    name: 'Inventory',
    component: () => import('../pages/inventory/InventoryPage.vue')
  },
  {
    path: '/shopping',
    name: 'Shopping',
    component: () => import('../pages/shopping/ShoppingPage.vue')
  },
  {
    path: '/ai-chat',
    name: 'AiChat',
    component: () => import('../pages/ai-chat/AiChatPage.vue')
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

console.debug('[router] routes registered', routes.map(r => r.path))

export default router

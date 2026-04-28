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
    component: () => import('../pages/inventory-shopping/InventoryShoppingPage.vue')
  },
  {
    path: '/shopping',
    redirect: '/inventory'
  },
  {
    path: '/ai-chat',
    name: 'AiChat',
    component: () => import('../pages/ai-chat/AiChatPage.vue')
  },
  {
    path: '/profile',
    name: 'Profile',
    component: () => import('../pages/profile/ProfilePage.vue')
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

console.debug('[router] routes registered', routes.map(r => r.path))

export default router

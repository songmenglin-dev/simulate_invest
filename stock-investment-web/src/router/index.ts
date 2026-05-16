import { createRouter, createWebHistory } from 'vue-router'
import LoginView from '../views/LoginView.vue'
import AppLayout from '../layouts/AppLayout.vue'

const router = createRouter({
  history: createWebHistory(),
  routes: [
    {
      path: '/login',
      component: LoginView,
    },
    {
      path: '/',
      component: AppLayout,
      redirect: '/dashboard',
      children: [
        {
          path: 'dashboard',
          component: () => import('../views/DashboardView.vue'),
          meta: { requiresAuth: true },
        },
        {
          path: 'market',
          component: () => import('../views/MarketView.vue'),
          meta: { requiresAuth: true },
        },
        {
          path: 'trading',
          component: () => import('../views/TradingView.vue'),
          meta: { requiresAuth: true },
        },
        {
          path: 'portfolio',
          component: () => import('../views/PortfolioView.vue'),
          meta: { requiresAuth: true },
        },
        {
          path: 'analysis',
          component: () => import('../views/AnalysisView.vue'),
          meta: { requiresAuth: true },
        },
        {
          path: 'profile',
          component: () => import('../views/ProfileView.vue'),
          meta: { requiresAuth: true },
        },
      ],
    },
  ],
})

router.beforeEach((to, _from, next) => {
  const token = localStorage.getItem('token')
  if (to.matched.some(r => r.meta.requiresAuth) && !token) {
    next('/login')
  } else if (to.path === '/login' && token) {
    next('/dashboard')
  } else {
    next()
  }
})

export default router

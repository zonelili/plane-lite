import { createRouter, createWebHistory } from 'vue-router'
import { getToken } from '@/utils/auth'

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: '/login',
      name: 'Login',
      component: () => import('@/views/auth/Login.vue'),
      meta: { requiresAuth: false },
    },
    {
      path: '/register',
      name: 'Register',
      component: () => import('@/views/auth/Register.vue'),
      meta: { requiresAuth: false },
    },
    {
      path: '/',
      redirect: '/projects',
    },
    {
      path: '/projects',
      name: 'Projects',
      component: () => import('@/views/project/ProjectList.vue'),
      meta: { requiresAuth: true },
    },
    {
      path: '/projects/:id',
      name: 'ProjectDetail',
      component: () => import('@/views/project/ProjectDetail.vue'),
      meta: { requiresAuth: true },
    },
    {
      path: '/projects/:id/issues',
      name: 'IssueList',
      component: () => import('@/views/issue/IssueList.vue'),
      meta: { requiresAuth: true },
    },
    {
      path: '/projects/:id/issues/:issueId',
      name: 'IssueDetail',
      component: () => import('@/views/issue/IssueDetail.vue'),
      meta: { requiresAuth: true },
    },
  ],
})

// 路由守卫
router.beforeEach((to, from, next) => {
  const token = getToken()
  const requiresAuth = to.meta.requiresAuth !== false

  if (requiresAuth && !token) {
    // 需要认证但没有 token -> 跳转登录
    next('/login')
  } else if ((to.path === '/login' || to.path === '/register') && token) {
    // 已登录用户访问登录/注册页 -> 跳转首页
    next('/projects')
  } else {
    next()
  }
})

export default router

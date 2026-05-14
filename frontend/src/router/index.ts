import { createRouter, createWebHistory } from 'vue-router'

const router = createRouter({
  history: createWebHistory(),
  routes: [
    {
      path: '/',
      component: () => import('@/views/layout/AppLayout.vue'),
      children: [
        { path: '', name: 'Home', component: () => import('@/views/HomePage.vue') },
      ],
    },
    { path: '/login', name: 'Login', component: () => import('@/views/LoginPage.vue') },
    { path: '/register', name: 'Register', component: () => import('@/views/RegisterPage.vue') },
  ],
})

export default router

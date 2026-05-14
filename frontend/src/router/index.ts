import { createRouter, createWebHistory } from 'vue-router'

const router = createRouter({
  history: createWebHistory(),
  routes: [
    {
      path: '/',
      component: () => import('@/views/layout/AppLayout.vue'),
      children: [
        { path: '', name: 'Home', component: () => import('@/views/HomePage.vue') },
        { path: 'skills', name: 'Skills', component: () => import('@/views/SkillsPage.vue') },
        { path: 'equipment', name: 'Equipment', component: () => import('@/views/EquipmentPage.vue') },
        { path: 'modifiers', name: 'Modifiers', component: () => import('@/views/DataPage.vue'), props: { type: 'modifiers' } },
        { path: 'passives', name: 'Passives', component: () => import('@/views/DataPage.vue'), props: { type: 'passives' } },
        { path: 'monsters', name: 'Monsters', component: () => import('@/views/DataPage.vue'), props: { type: 'monsters' } },
        { path: 'currency', name: 'Currency', component: () => import('@/views/DataPage.vue'), props: { type: 'currency' } },
        { path: 'calculator', name: 'Calculator', component: () => import('@/views/CalculatorPage.vue') },
        { path: 'guides', name: 'Guides', component: () => import('@/views/GuidesPage.vue') },
        { path: 'guides/:id', name: 'GuideDetail', component: () => import('@/views/GuideDetailPage.vue') },
        { path: 'recommendations', name: 'Recommendations', component: () => import('@/views/RecommendationsPage.vue') },
      ],
    },
    { path: '/login', name: 'Login', component: () => import('@/views/LoginPage.vue') },
    { path: '/register', name: 'Register', component: () => import('@/views/RegisterPage.vue') },
  ],
})

export default router

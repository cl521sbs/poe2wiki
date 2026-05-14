import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import type { LoginParams, RegisterParams } from '@/api/modules/auth'
import { authApi } from '@/api/modules/auth'
import router from '@/router'

interface UserProfile {
  id: number
  username: string
  nickname: string
  role: string
}

export const useAuthStore = defineStore('auth', () => {
  const user = ref<UserProfile | null>(null)
  const accessToken = ref(localStorage.getItem('accessToken') || '')
  const refreshToken = ref(localStorage.getItem('refreshToken') || '')

  const isLoggedIn = computed(() => !!accessToken.value)

  async function login(params: LoginParams) {
    const resp = await authApi.login(params)
    const data = resp.data as any
    accessToken.value = data.accessToken
    refreshToken.value = data.refreshToken
    user.value = data.user
    localStorage.setItem('accessToken', data.accessToken)
    localStorage.setItem('refreshToken', data.refreshToken)
    router.push('/')
  }

  async function register(params: RegisterParams) {
    const resp = await authApi.register(params)
    const data = resp.data as any
    accessToken.value = data.accessToken
    refreshToken.value = data.refreshToken
    user.value = data.user
    localStorage.setItem('accessToken', data.accessToken)
    localStorage.setItem('refreshToken', data.refreshToken)
    router.push('/')
  }

  function logout() {
    accessToken.value = ''
    refreshToken.value = ''
    user.value = null
    localStorage.removeItem('accessToken')
    localStorage.removeItem('refreshToken')
    router.push('/')
  }

  return { user, accessToken, refreshToken, isLoggedIn, login, register, logout }
})

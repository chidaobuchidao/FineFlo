import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import type { User, LoginRequest } from '@/types'
import * as authApi from '@/api/auth'

export const useAuthStore = defineStore('auth', () => {
  const user = ref<User | null>(null)
  const token = ref<string | null>(null)
  const loading = ref(false)
  const error = ref<string | null>(null)

  const isAuthenticated = computed(() => !!token.value)
  const isApprover = computed(() => user.value?.role === 'APPROVER' || user.value?.role === 'ADMIN')
  const isEnterprise = computed(() => user.value?.role === 'ENTERPRISE')

  function loadFromStorage() {
    const stored = sessionStorage.getItem('user')
    const storedToken = sessionStorage.getItem('token')
    if (stored && storedToken) {
      user.value = JSON.parse(stored)
      token.value = storedToken
    }
  }

  async function login(credentials: LoginRequest): Promise<boolean> {
    loading.value = true
    error.value = null
    try {
      const res = await authApi.login(credentials)
      if (res.code === 200) {
        token.value = res.data.token
        user.value = res.data.user
        sessionStorage.setItem('token', res.data.token)
        sessionStorage.setItem('user', JSON.stringify(res.data.user))
        return true
      }
      error.value = res.message || '登录失败'
      return false
    } catch (e: unknown) {
      const msg = e instanceof Error ? e.message : '网络错误'
      error.value = msg
      return false
    } finally {
      loading.value = false
    }
  }

  function logout() {
    token.value = null
    user.value = null
    sessionStorage.removeItem('token')
    sessionStorage.removeItem('user')
  }

  return { user, token, loading, error, isAuthenticated, isApprover, isEnterprise, loadFromStorage, login, logout }
})

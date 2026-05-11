import { useAuthStore } from '@/stores/auth'
import { useRouter } from 'vue-router'

export function useAuth() {
  const store = useAuthStore()
  const router = useRouter()

  async function login(username: string, password: string): Promise<boolean> {
    const ok = await store.login({ username, password })
    if (ok) {
      const role = store.user?.role
      router.push(role === 'ENTERPRISE' ? '/loans' : '/dashboard')
    }
    return ok
  }

  function logout() {
    store.logout()
    router.push('/login')
  }

  return {
    user: store.user,
    token: store.token,
    loading: store.loading,
    error: store.error,
    isAuthenticated: store.isAuthenticated,
    isApprover: store.isApprover,
    isEnterprise: store.isEnterprise,
    login,
    logout,
    loadFromStorage: store.loadFromStorage,
  }
}

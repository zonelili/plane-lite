import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import { userApi } from '@/api/user.api'
import { setToken, removeToken } from '@/utils/auth'
import type { User, LoginForm, RegisterForm } from '@/types/user'

export const useUserStore = defineStore('user', () => {
  // State
  const user = ref<User | null>(null)
  const token = ref<string>('')

  // Getters
  const isLoggedIn = computed(() => !!token.value)
  const displayName = computed(() => user.value?.username || 'Guest')

  // Actions
  async function login(form: LoginForm) {
    const { token: newToken, user: userData } = await userApi.login(form)
    token.value = newToken
    user.value = userData
    setToken(newToken)
  }

  async function register(form: RegisterForm) {
    await userApi.register(form)
  }

  async function fetchUserInfo() {
    const userData = await userApi.getCurrentUser()
    user.value = userData
  }

  function logout() {
    user.value = null
    token.value = ''
    removeToken()
  }

  return {
    user,
    token,
    isLoggedIn,
    displayName,
    login,
    register,
    fetchUserInfo,
    logout,
  }
})

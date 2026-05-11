<template>
  <div class="login-page">
    <ParticleBg />
    <div ref="cardRef" class="login-card glass-card">
      <div class="login-header">
        <div class="login-icon">💰</div>
        <h1 class="login-title">普惠金融管理系统</h1>
        <p class="login-subtitle">Inclusive Finance Platform</p>
      </div>
      <form class="login-form" @submit.prevent="handleLogin">
        <div class="form-group">
          <label class="form-label">用户名</label>
          <input
            v-model="username"
            type="text"
            class="form-input focus-ring"
            placeholder="请输入用户名"
            autocomplete="username"
            @blur="validateUsername"
          />
          <p v-if="usernameError" class="field-error">{{ usernameError }}</p>
        </div>
        <div class="form-group">
          <label class="form-label">密码</label>
          <div class="password-input-wrapper">
            <input
              v-model="password"
              :type="showPassword ? 'text' : 'password'"
              class="form-input focus-ring"
              placeholder="请输入密码"
              autocomplete="current-password"
              @blur="validatePassword"
            />
            <button
              type="button"
              class="password-toggle pressable"
              :title="showPassword ? '隐藏密码' : '显示密码'"
              @click="showPassword = !showPassword"
            >
              <SvgIcon :name="showPassword ? 'eyeOff' : 'eye'" :size="18" />
            </button>
          </div>
          <p v-if="passwordError" class="field-error">{{ passwordError }}</p>
        </div>

        <label class="remember-me">
          <input type="checkbox" v-model="rememberMe" />
          <span>记住登录状态</span>
        </label>

        <p v-if="errorMsg" class="form-error">{{ errorMsg }}</p>
        <button type="submit" class="login-btn pressable" :disabled="loading">
          <span v-if="loading" class="spinner"></span>
          <span v-else>登 录</span>
        </button>
      </form>
      <p class="login-hint">测试账号：admin / admin123</p>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, nextTick } from 'vue'
import gsap from 'gsap'
import ParticleBg from '@/components/shared/ParticleBg.vue'
import SvgIcon from '@/components/shared/SvgIcon.vue'
import { useAuth } from '@/composables/useAuth'

const { login, loading, error } = useAuth()
const username = ref('')
const password = ref('')
const showPassword = ref(false)
const rememberMe = ref(false)
const errorMsg = ref('')
const usernameError = ref('')
const passwordError = ref('')
const cardRef = ref<HTMLElement | null>(null)

onMounted(() => {
  nextTick(() => {
    gsap.from(cardRef.value, {
      scale: 0.92,
      opacity: 0,
      y: 30,
      duration: 0.6,
      ease: 'power3.out',
    })
  })
})

function validateUsername() {
  usernameError.value = ''
  if (!username.value.trim()) {
    usernameError.value = '请输入用户名'
  }
}

function validatePassword() {
  passwordError.value = ''
  if (!password.value) {
    passwordError.value = '请输入密码'
  }
}

async function handleLogin() {
  errorMsg.value = ''
  usernameError.value = ''
  passwordError.value = ''

  validateUsername()
  validatePassword()

  if (usernameError.value || passwordError.value) return

  if (rememberMe.value) {
    localStorage.setItem('remembered', 'true')
  } else {
    localStorage.removeItem('remembered')
  }

  const ok = await login(username.value, password.value)
  if (!ok) {
    errorMsg.value = error.value || '登录失败'
  }
}
</script>

<style scoped>
.login-page {
  height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  position: relative;
  overflow: hidden;
}

.login-card {
  position: relative;
  z-index: 1;
  width: 420px;
  padding: var(--space-10);
}

.login-header {
  text-align: center;
  margin-bottom: var(--space-8);
}

.login-icon {
  font-size: 48px;
  margin-bottom: var(--space-4);
}

.login-title {
  font-size: var(--text-2xl);
  font-weight: 700;
  color: var(--color-text);
}

.login-subtitle {
  margin-top: var(--space-1);
  font-size: var(--text-sm);
  color: var(--color-text-muted);
}

.form-group {
  margin-bottom: var(--space-5);
}

.form-label {
  display: block;
  font-size: var(--text-sm);
  font-weight: 500;
  color: var(--color-text-secondary);
  margin-bottom: var(--space-2);
}

.password-input-wrapper {
  position: relative;
}

.form-input {
  width: 100%;
  padding: var(--space-3) var(--space-4);
  background: var(--color-surface);
  border: var(--border-glass);
  border-radius: var(--radius-md);
  color: var(--color-text);
  font-size: var(--text-base);
  outline: none;
  transition: border-color var(--duration-fast) var(--ease-out-expo);
}

.form-input::placeholder {
  color: var(--color-text-muted);
}

.form-input:focus {
  border-color: var(--color-primary);
}

.password-toggle {
  position: absolute;
  right: var(--space-2);
  top: 50%;
  transform: translateY(-50%);
  display: flex;
  align-items: center;
  justify-content: center;
  width: 32px;
  height: 32px;
  background: transparent;
  border: none;
  color: var(--color-text-muted);
  border-radius: var(--radius-sm);
  cursor: pointer;
  transition: color var(--duration-fast) var(--ease-out-expo);
}

.password-toggle:hover {
  color: var(--color-text-secondary);
}

.field-error {
  color: var(--color-danger);
  font-size: var(--text-xs);
  margin-top: var(--space-1);
}

.form-error {
  color: var(--color-danger);
  font-size: var(--text-sm);
  margin-bottom: var(--space-4);
}

.remember-me {
  display: flex;
  align-items: center;
  gap: var(--space-2);
  margin-bottom: var(--space-5);
  font-size: var(--text-sm);
  color: var(--color-text-secondary);
  cursor: pointer;
  user-select: none;
}

.remember-me input[type="checkbox"] {
  accent-color: var(--color-primary);
  width: 16px;
  height: 16px;
}

.login-btn {
  width: 100%;
  padding: var(--space-3) var(--space-6);
  background: linear-gradient(135deg, var(--color-primary), #8B5CF6);
  color: white;
  border: none;
  border-radius: var(--radius-md);
  font-size: var(--text-base);
  font-weight: 600;
  letter-spacing: 0.1em;
  transition: all var(--duration-fast) var(--ease-out-expo);
  display: flex;
  align-items: center;
  justify-content: center;
  min-height: 44px;
}

.login-btn:hover:not(:disabled) {
  box-shadow: var(--shadow-glow);
  filter: brightness(1.1);
}

.login-btn:disabled {
  opacity: 0.6;
  cursor: not-allowed;
}

.login-hint {
  margin-top: var(--space-6);
  text-align: center;
  font-size: var(--text-xs);
  color: var(--color-text-muted);
}
</style>

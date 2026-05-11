<template>
  <div class="auth-page">
    <ParticleBg />
    <div class="auth-card glass-card">
      <div class="auth-header">
        <div class="auth-icon">🏢</div>
        <h1 class="auth-title">企业用户注册</h1>
        <p class="auth-subtitle">创建您的企业账户，开启普惠金融服务</p>
      </div>
      <form class="auth-form" @submit.prevent="handleRegister">
        <div class="form-row">
          <div class="form-group">
            <label class="form-label">用户名</label>
            <input v-model="form.username" type="text" class="form-input" placeholder="登录账号" required minlength="3" />
          </div>
          <div class="form-group">
            <label class="form-label">真实姓名</label>
            <input v-model="form.realName" type="text" class="form-input" placeholder="法人/联系人" required />
          </div>
        </div>
        <div class="form-row">
          <div class="form-group">
            <label class="form-label">密码</label>
            <input v-model="form.password" type="password" class="form-input" placeholder="至少6位" required minlength="6" />
          </div>
          <div class="form-group">
            <label class="form-label">手机号</label>
            <input v-model="form.phone" type="tel" class="form-input" placeholder="13800001111" required pattern="1[3-9]\d{9}" />
          </div>
        </div>
        <p v-if="errorMsg" class="form-error">{{ errorMsg }}</p>
        <button type="submit" class="btn-primary" :disabled="submitting">
          {{ submitting ? '注册中...' : '注 册' }}
        </button>
      </form>
      <p class="auth-footer">已有账号？<router-link to="/login">立即登录</router-link></p>
    </div>
  </div>
</template>

<script setup lang="ts">
import { reactive, ref } from 'vue'
import { useRouter } from 'vue-router'
import ParticleBg from '@/components/shared/ParticleBg.vue'
import { register } from '@/api/auth'

const router = useRouter()
const form = reactive({ username: '', password: '', realName: '', phone: '' })
const errorMsg = ref('')
const submitting = ref(false)

async function handleRegister() {
  errorMsg.value = ''
  if (!/^1[3-9]\d{9}$/.test(form.phone)) {
    errorMsg.value = '请输入正确的手机号'
    return
  }
  submitting.value = true
  try {
    await register(form)
    router.push('/login')
  } catch (e: unknown) {
    errorMsg.value = e instanceof Error ? e.message : '注册失败'
  } finally {
    submitting.value = false
  }
}
</script>

<style scoped>
.auth-page { height: 100vh; display: flex; align-items: center; justify-content: center; position: relative; overflow: auto; padding: var(--space-8); }
.auth-card { position: relative; z-index: 1; width: 520px; padding: var(--space-10); }
.auth-header { text-align: center; margin-bottom: var(--space-8); }
.auth-icon { font-size: 44px; margin-bottom: var(--space-3); }
.auth-title { font-size: var(--text-2xl); font-weight: 700; color: var(--color-text); }
.auth-subtitle { margin-top: var(--space-1); font-size: var(--text-sm); color: var(--color-text-muted); }
.auth-form { display: flex; flex-direction: column; gap: var(--space-5); }
.form-row { display: grid; grid-template-columns: 1fr 1fr; gap: var(--space-4); }
.form-label { display: block; font-size: var(--text-sm); font-weight: 500; color: var(--color-text-secondary); margin-bottom: var(--space-2); }
.form-input { width: 100%; padding: var(--space-3) var(--space-4); background: var(--color-surface); border: var(--border-glass); border-radius: var(--radius-md); color: var(--color-text); font-size: var(--text-base); outline: none; }
.form-input:focus { border-color: var(--color-primary); }
.form-error { color: var(--color-danger); font-size: var(--text-sm); }
.btn-primary { width: 100%; padding: var(--space-3); background: linear-gradient(135deg, var(--color-primary), #8B5CF6); color: #fff; border: none; border-radius: var(--radius-md); font-size: var(--text-base); font-weight: 600; letter-spacing: 0.1em; cursor: pointer; min-height: 44px; }
.btn-primary:disabled { opacity: 0.6; cursor: not-allowed; }
.auth-footer { margin-top: var(--space-6); text-align: center; font-size: var(--text-sm); color: var(--color-text-muted); }
.auth-footer a { color: var(--color-accent); }
@media (max-width: 540px) { .form-row { grid-template-columns: 1fr; } .auth-card { padding: var(--space-6); } }
</style>

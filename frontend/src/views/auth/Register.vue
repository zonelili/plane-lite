<template>
  <div class="auth-shell">
    <!-- 左侧品牌区 -->
    <div class="brand-panel">
      <div class="brand-inner">
        <div class="brand-logo">
          <span class="logo-bracket">[</span>
          <span class="logo-text">PL</span>
          <span class="logo-bracket">]</span>
        </div>
        <h1 class="brand-headline">PLANE<br /><em>LITE</em></h1>
        <p class="brand-sub">开始你的<br />第一个项目</p>
        <div class="brand-tags">
          <span class="tag">// 免费注册</span>
          <span class="tag">// 30 秒上手</span>
          <span class="tag">// 无需信用卡</span>
        </div>
        <div class="paper-texture" aria-hidden="true"></div>
      </div>
    </div>

    <!-- 右侧表单区 -->
    <div class="form-panel">
      <div class="form-inner">
        <div class="form-header">
          <p class="form-label">[ 创建账户 / REGISTER ]</p>
          <h2 class="form-title">加入我们</h2>
        </div>

        <form class="auth-form" @submit.prevent="handleRegister" novalidate>
          <div class="field-group" :class="{ 'has-error': errors.username }">
            <label class="field-label">USERNAME</label>
            <div class="field-wrap">
              <input
                v-model="form.username"
                type="text"
                class="field-input"
                placeholder="your_name"
                autocomplete="username"
                @blur="validateUsername"
              />
              <span class="field-cursor">_</span>
            </div>
            <p v-if="errors.username" class="field-error">{{ errors.username }}</p>
          </div>

          <div class="field-group" :class="{ 'has-error': errors.email }">
            <label class="field-label">EMAIL_ADDR</label>
            <div class="field-wrap">
              <input
                v-model="form.email"
                type="email"
                class="field-input"
                placeholder="user@example.com"
                autocomplete="email"
                @blur="validateEmail"
              />
              <span class="field-cursor">_</span>
            </div>
            <p v-if="errors.email" class="field-error">{{ errors.email }}</p>
          </div>

          <div class="field-group" :class="{ 'has-error': errors.password }">
            <label class="field-label">PASSWORD</label>
            <div class="field-wrap">
              <input
                v-model="form.password"
                :type="showPassword ? 'text' : 'password'"
                class="field-input"
                placeholder="••••••••"
                autocomplete="new-password"
                @blur="validatePassword"
              />
              <button
                type="button"
                class="toggle-pw"
                @click="showPassword = !showPassword"
              >{{ showPassword ? 'HIDE' : 'SHOW' }}</button>
            </div>
            <p v-if="errors.password" class="field-error">{{ errors.password }}</p>
            <div v-if="form.password" class="password-strength">
              <div
                class="strength-bar"
                :class="passwordStrength.level"
                :style="{ width: passwordStrength.width }"
              ></div>
              <span class="strength-label">{{ passwordStrength.label }}</span>
            </div>
          </div>

          <div class="field-group" :class="{ 'has-error': errors.confirmPassword }">
            <label class="field-label">CONFIRM_PWD</label>
            <div class="field-wrap">
              <input
                v-model="form.confirmPassword"
                :type="showConfirm ? 'text' : 'password'"
                class="field-input"
                placeholder="••••••••"
                autocomplete="new-password"
                @blur="validateConfirm"
              />
              <button
                type="button"
                class="toggle-pw"
                @click="showConfirm = !showConfirm"
              >{{ showConfirm ? 'HIDE' : 'SHOW' }}</button>
            </div>
            <p v-if="errors.confirmPassword" class="field-error">{{ errors.confirmPassword }}</p>
          </div>

          <button
            type="submit"
            class="submit-btn"
            :class="{ loading: loading }"
            :disabled="loading"
          >
            <span class="btn-text">{{ loading ? 'CREATING ACCOUNT...' : 'CREATE ACCOUNT →' }}</span>
            <span v-if="loading" class="btn-loader" aria-hidden="true"></span>
          </button>

          <p class="form-switch">
            已有账号？
            <router-link to="/login" class="switch-link">立即登录 ↗</router-link>
          </p>
        </form>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, computed } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { useUserStore } from '@/stores/user'

const router = useRouter()
const userStore = useUserStore()

const loading = ref(false)
const showPassword = ref(false)
const showConfirm = ref(false)

const form = reactive({ username: '', email: '', password: '', confirmPassword: '' })
const errors = reactive({ username: '', email: '', password: '', confirmPassword: '' })

const passwordStrength = computed(() => {
  const p = form.password
  if (!p) return { level: '', width: '0%', label: '' }
  let score = 0
  if (p.length >= 8) score++
  if (/[A-Z]/.test(p)) score++
  if (/[0-9]/.test(p)) score++
  if (/[^A-Za-z0-9]/.test(p)) score++
  return [
    { level: 'weak',   width: '25%',  label: 'WEAK'   },
    { level: 'fair',   width: '50%',  label: 'FAIR'   },
    { level: 'good',   width: '75%',  label: 'GOOD'   },
    { level: 'strong', width: '100%', label: 'STRONG' },
  ][Math.min(score, 3)]
})

function validateUsername() {
  if (!form.username) errors.username = '用户名不能为空'
  else if (form.username.length < 2 || form.username.length > 20) errors.username = '用户名长度 2-20 位'
  else errors.username = ''
}
function validateEmail() {
  if (!form.email) errors.email = '邮箱不能为空'
  else if (!/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(form.email)) errors.email = '请输入有效的邮箱地址'
  else errors.email = ''
}
function validatePassword() {
  if (!form.password) errors.password = '密码不能为空'
  else if (form.password.length < 6) errors.password = '密码长度至少 6 位'
  else errors.password = ''
}
function validateConfirm() {
  if (!form.confirmPassword) errors.confirmPassword = '请再次输入密码'
  else if (form.confirmPassword !== form.password) errors.confirmPassword = '两次密码不一致'
  else errors.confirmPassword = ''
}

async function handleRegister() {
  validateUsername(); validateEmail(); validatePassword(); validateConfirm()
  if (errors.username || errors.email || errors.password || errors.confirmPassword) return
  loading.value = true
  try {
    await userStore.register({ username: form.username, email: form.email, password: form.password })
    ElMessage.success('注册成功，请登录')
    router.push('/login')
  } catch {
    // 错误已在拦截器中处理
  } finally {
    loading.value = false
  }
}
</script>

<style scoped lang="scss">
@import url('https://fonts.googleapis.com/css2?family=DM+Serif+Display:ital@0;1&family=JetBrains+Mono:wght@300;400;500;600&display=swap');

/* ── CSS Variables ───────────────────────────── */
:root {
  --cream:       #FAF7F2;
  --ink:         #1C1410;
  --ink-mid:     #4A3728;
  --ink-light:   #8C7B6B;
  --amber:       #D4870A;
  --amber-light: #F5A623;
  --divider:     #D8CFC4;
  --brand-bg:    #1C1410;
  --field-bg:    #EDE8E0;
  --field-focus: #E3D9CC;
}

/* ── Shell ──────────────────────────────────── */
.auth-shell {
  display: flex;
  width: 100%;
  height: 100vh;
  background: var(--cream);
  font-family: 'JetBrains Mono', monospace;
  overflow: hidden;
}

/* ── Brand Panel ────────────────────────────── */
.brand-panel {
  position: relative;
  width: 38%;
  background: var(--brand-bg);
  border-right: none;
  overflow: hidden;
  display: flex;
  align-items: center;
  justify-content: center;

  &::before {
    content: '';
    position: absolute;
    inset: 0;
    background:
      radial-gradient(ellipse 70% 50% at 20% 70%, rgba(212, 135, 10, 0.12) 0%, transparent 65%),
      radial-gradient(ellipse 50% 40% at 80% 15%, rgba(212, 135, 10, 0.06) 0%, transparent 60%);
    pointer-events: none;
  }
}

.brand-inner {
  position: relative;
  z-index: 1;
  padding: 60px 64px;
  width: 100%;
}

.brand-logo {
  display: flex;
  align-items: center;
  gap: 4px;
  margin-bottom: 48px;
  font-size: 13px;
  font-weight: 500;
  letter-spacing: 0.08em;
  color: var(--amber);

  .logo-text   { color: #E8E0D4; }
  .logo-bracket { opacity: 0.6; }
}

.brand-headline {
  font-family: 'DM Serif Display', serif;
  font-size: clamp(56px, 6vw, 96px);
  font-weight: 400;
  line-height: 0.88;
  color: #F5EDD8;
  letter-spacing: -0.02em;
  margin-bottom: 32px;

  em {
    font-style: italic;
    color: var(--amber);
  }
}

.brand-sub {
  font-size: 18px;
  font-weight: 400;
  color: #9E8E7E;
  line-height: 1.7;
  letter-spacing: 0.03em;
  margin-bottom: 48px;
}

.brand-tags {
  display: flex;
  flex-direction: column;
  gap: 12px;

  .tag {
    font-size: 13px;
    color: #5A4A3A;
    letter-spacing: 0.06em;
    transition: color 0.2s;

    &:hover { color: var(--amber); }
  }
}

.paper-texture {
  position: absolute;
  inset: 0;
  background-image: url("data:image/svg+xml,%3Csvg viewBox='0 0 256 256' xmlns='http://www.w3.org/2000/svg'%3E%3Cfilter id='noise'%3E%3CfeTurbulence type='fractalNoise' baseFrequency='0.9' numOctaves='4' stitchTiles='stitch'/%3E%3C/filter%3E%3Crect width='100%25' height='100%25' filter='url(%23noise)' opacity='0.04'/%3E%3C/svg%3E");
  background-size: 256px 256px;
  pointer-events: none;
  opacity: 0.6;
}

/* ── Form Panel ─────────────────────────────── */
.form-panel {
  flex: 1;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 40px;
  background:
    radial-gradient(ellipse 60% 50% at 90% 10%, rgba(212, 135, 10, 0.06) 0%, transparent 60%),
    radial-gradient(ellipse 50% 60% at 10% 90%, rgba(212, 135, 10, 0.04) 0%, transparent 55%),
    var(--cream);
  overflow-y: auto;
}

.form-inner {
  width: 100%;
  max-width: 600px;
  padding: 20px 0;
  animation: slideUp 0.5s cubic-bezier(0.16, 1, 0.3, 1) both;
}

@keyframes slideUp {
  from { opacity: 0; transform: translateY(20px); }
  to   { opacity: 1; transform: translateY(0); }
}

.form-header {
  margin-bottom: 44px;
}

.form-label {
  font-size: 13px;
  font-weight: 500;
  letter-spacing: 0.16em;
  color: var(--amber);
  margin-bottom: 14px;
}

.form-title {
  font-family: 'DM Serif Display', serif;
  font-size: 68px;
  font-weight: 400;
  color: var(--ink);
  letter-spacing: -0.01em;
}

/* ── Fields ─────────────────────────────────── */
.auth-form {
  display: flex;
  flex-direction: column;
  gap: 28px;
}

.field-group {
  display: flex;
  flex-direction: column;
  gap: 10px;

  &.has-error .field-input { border-color: #C0392B; }
  &.has-error .field-label { color: #C0392B; }
}

.field-label {
  font-size: 13px;
  font-weight: 500;
  letter-spacing: 0.14em;
  color: var(--ink-light);
  transition: color 0.2s;
}

.field-group:focus-within .field-label { color: var(--amber); }

.field-wrap {
  position: relative;
  display: flex;
  align-items: center;
}

.field-input {
  width: 100%;
  background: var(--field-bg);
  border: 1.5px solid transparent;
  border-radius: 3px;
  color: var(--ink);
  font-family: 'JetBrains Mono', monospace;
  font-size: 16px;
  padding: 14px 16px;
  padding-right: 72px;
  outline: none;
  transition: border-color 0.2s, background 0.2s;
  letter-spacing: 0.02em;

  &::placeholder { color: #B8A898; }
  &:focus {
    background: var(--field-focus);
    border-color: var(--amber);
  }
}

.field-group.has-error .field-input {
  border-color: #C0392B;
  background: #FDF0EE;
}

.field-cursor {
  position: absolute;
  right: 16px;
  color: var(--amber);
  font-size: 20px;
  animation: blink 1.1s step-end infinite;
  pointer-events: none;
  opacity: 0;
  transition: opacity 0.15s;

  .field-group:focus-within & { opacity: 1; }
}

@keyframes blink {
  0%, 100% { opacity: 1; }
  50%       { opacity: 0; }
}

.toggle-pw {
  position: absolute;
  right: 12px;
  background: rgba(212, 135, 10, 0.12);
  border: 1px solid rgba(212, 135, 10, 0.3);
  border-radius: 2px;
  color: var(--amber);
  font-family: 'JetBrains Mono', monospace;
  font-size: 10px;
  font-weight: 600;
  letter-spacing: 0.1em;
  cursor: pointer;
  padding: 3px 7px;
  transition: background 0.2s, color 0.2s;

  &:hover {
    background: var(--amber);
    color: #fff;
  }
}

.field-error {
  font-size: 13px;
  color: #C0392B;
  letter-spacing: 0.03em;
}

/* ── Password Strength ───────────────────────── */
.password-strength {
  display: flex;
  align-items: center;
  gap: 10px;
  margin-top: 2px;
}

.strength-bar {
  height: 2px;
  background: var(--divider);
  transition: width 0.3s, background 0.3s;
  max-width: 120px;

  &.weak   { background: #C0392B; }
  &.fair   { background: var(--amber); }
  &.good   { background: #7CB342; }
  &.strong { background: #2E7D32; }
}

.strength-label {
  font-size: 11px;
  font-weight: 500;
  letter-spacing: 0.12em;
  color: var(--ink-light);
}

/* ── Submit Button ───────────────────────────── */
.submit-btn {
  position: relative;
  width: 100%;
  padding: 18px 24px;
  background: rgba(212, 135, 10, 0.06);
  border: 2.5px solid var(--amber);
  border-radius: 3px;
  color: var(--amber);
  font-family: 'JetBrains Mono', monospace;
  font-size: 15px;
  font-weight: 600;
  letter-spacing: 0.12em;
  cursor: pointer;
  overflow: hidden;
  transition: background 0.2s, color 0.2s, transform 0.1s, box-shadow 0.2s;
  margin-top: 12px;

  &::before {
    content: '';
    position: absolute;
    top: 0; left: -100%;
    width: 100%; height: 100%;
    background: linear-gradient(90deg, transparent, rgba(255,255,255,0.1), transparent);
    transition: left 0.4s;
  }

  &:hover:not(:disabled)::before { left: 100%; }
  &:hover:not(:disabled) {
    background: #C07809;
    border-color: #C07809;
    color: #fff;
    transform: translateY(-1px);
    box-shadow: 0 4px 20px rgba(212, 135, 10, 0.35);
  }
  &:active:not(:disabled) {
    transform: translateY(0);
    background: #A86A08;
    border-color: #A86A08;
    color: #fff;
  }
  &:disabled { cursor: not-allowed; opacity: 0.5; }
  &.loading {
    background: #C07809;
    border-color: #C07809;
    color: #fff;
    .btn-text { opacity: 0.7; }
  }
}

.btn-loader {
  position: absolute;
  right: 20px;
  top: 50%;
  transform: translateY(-50%);
  width: 14px;
  height: 14px;
  border: 2px solid rgba(255,255,255,0.3);
  border-top-color: #fff;
  border-radius: 50%;
  animation: spin 0.7s linear infinite;
}

@keyframes spin {
  to { transform: translateY(-50%) rotate(360deg); }
}

/* ── Switch ─────────────────────────────────── */
.form-switch {
  font-size: 14px;
  color: var(--ink-light);
  text-align: center;
  letter-spacing: 0.02em;
}

.switch-link {
  color: var(--amber);
  text-decoration: underline;
  text-underline-offset: 3px;
  font-weight: 600;
  margin-left: 4px;
  transition: color 0.2s;

  &:hover { color: #C07809; }
}

/* ── Responsive ─────────────────────────────── */
@media (max-width: 768px) {
  .brand-panel { display: none; }
  .form-panel  { padding: 24px; }
}
</style>

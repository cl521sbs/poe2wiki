<template>
  <div class="auth-page">
    <el-card class="auth-card">
      <h2>注册 PoE2Wiki</h2>
      <el-form :model="form" ref="formRef" :rules="rules" @submit.prevent="handleRegister">
        <el-form-item prop="username">
          <el-input v-model="form.username" placeholder="用户名（3-20位字母数字下划线）" />
        </el-form-item>
        <el-form-item prop="email">
          <el-input v-model="form.email" placeholder="邮箱" />
        </el-form-item>
        <el-form-item prop="password">
          <el-input v-model="form.password" type="password" placeholder="密码（6-50位）" show-password />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" native-type="submit" :loading="loading" class="full-width">
            注册
          </el-button>
        </el-form-item>
      </el-form>
      <p class="switch-link">
        已有账号？<router-link to="/login">立即登录</router-link>
      </p>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { reactive, ref } from 'vue'
import type { FormInstance, FormRules } from 'element-plus'
import { useAuthStore } from '@/stores/auth'

const authStore = useAuthStore()
const formRef = ref<FormInstance>()
const loading = ref(false)

const form = reactive({ username: '', email: '', password: '' })

const rules: FormRules = {
  username: [
    { required: true, message: '请输入用户名', trigger: 'blur' },
    { min: 3, max: 20, message: '用户名长度 3-20 位', trigger: 'blur' },
    { pattern: /^[a-zA-Z0-9_]+$/, message: '只允许字母、数字、下划线', trigger: 'blur' },
  ],
  email: [
    { required: true, message: '请输入邮箱', trigger: 'blur' },
    { type: 'email', message: '邮箱格式不正确', trigger: 'blur' },
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 6, max: 50, message: '密码长度 6-50 位', trigger: 'blur' },
  ],
}

async function handleRegister() {
  const valid = await formRef.value?.validate().catch(() => false)
  if (!valid) return
  loading.value = true
  try {
    await authStore.register(form)
  } catch {
    // error shown by interceptor
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
.auth-page {
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: 100vh;
  background-color: var(--bg-primary);
}
.auth-card {
  width: 400px;
}
.auth-card h2 {
  text-align: center;
  color: var(--accent);
  margin-bottom: 24px;
}
.switch-link {
  text-align: center;
  color: var(--text-secondary);
}
.switch-link a {
  color: var(--accent);
}
</style>

<template>
  <div class="auth-page">
    <el-card class="auth-card">
      <h2>注册 PoE2Wiki</h2>
      <el-form :model="form" ref="formRef" @submit.prevent="handleRegister">
        <el-form-item prop="username">
          <el-input v-model="form.username" placeholder="用户名（4-20位字母数字下划线）" />
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
import { useAuthStore } from '@/stores/auth'

const authStore = useAuthStore()
const formRef = ref()
const loading = ref(false)

const form = reactive({
  username: '',
  email: '',
  password: '',
})

async function handleRegister() {
  loading.value = true
  try {
    await authStore.register(form)
  } catch {
    // 错误已在拦截器处理
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

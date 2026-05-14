<template>
  <div class="profile-page">
    <h2>个人设置</h2>

    <el-card style="max-width: 500px">
      <div style="text-align: center; margin-bottom: 24px">
        <el-avatar :size="80" style="font-size: 32px">{{ (form.nickname || form.username || '?')[0] }}</el-avatar>
      </div>

      <el-form :model="form" label-width="80px">
        <el-form-item label="用户名">
          <el-input :model-value="form.username" disabled />
        </el-form-item>
        <el-form-item label="角色">
          <el-tag>{{ roleLabel(form.role) }}</el-tag>
        </el-form-item>
        <el-form-item label="昵称">
          <el-input v-model="form.nickname" placeholder="设置昵称" maxlength="20" show-word-limit />
        </el-form-item>
        <el-form-item label="简介">
          <el-input v-model="form.bio" type="textarea" :rows="3" placeholder="介绍一下自己..." maxlength="200" show-word-limit />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="saveProfile" :loading="saving">保存设置</el-button>
        </el-form-item>
      </el-form>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { reactive, ref } from 'vue'
import { useAuthStore } from '@/stores/auth'
import { authApi } from '@/api/modules/auth'
import { ElMessage } from 'element-plus'

const authStore = useAuthStore()
const saving = ref(false)

const form = reactive({
  username: authStore.user?.username || '',
  nickname: authStore.user?.nickname || '',
  role: authStore.user?.role || 'user',
  bio: '',
})

const roleLabels: Record<string, string> = { user: '普通用户', editor: '编辑', admin: '管理员' }
function roleLabel(v: string) { return roleLabels[v] || v }

async function saveProfile() {
  saving.value = true
  try {
    await authApi.updateProfile({ nickname: form.nickname, bio: form.bio })
    if (authStore.user) {
      authStore.user.nickname = form.nickname
    }
    ElMessage.success('设置已保存')
  } catch {
    // error shown by interceptor
  } finally {
    saving.value = false
  }
}
</script>

<style scoped>
.profile-page { padding: 20px; }
.profile-page h2 { color: var(--accent); margin-bottom: 20px; }
</style>

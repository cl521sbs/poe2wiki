<template>
  <el-container class="app-layout">
    <el-header class="app-header">
      <div class="logo" @click="$router.push('/')">
        <span>PoE2Wiki</span>
      </div>
      <el-menu mode="horizontal" :default-active="activeMenu" router class="app-menu">
        <el-sub-menu index="data">
          <template #title>数据库</template>
          <el-menu-item index="/skills">技能</el-menu-item>
          <el-menu-item index="/equipment">装备</el-menu-item>
          <el-menu-item index="/modifiers">词缀</el-menu-item>
          <el-menu-item index="/passives">天赋</el-menu-item>
          <el-menu-item index="/monsters">怪物</el-menu-item>
          <el-menu-item index="/currency">通货</el-menu-item>
        </el-sub-menu>
        <el-menu-item index="/calculator">计算器</el-menu-item>
        <el-menu-item index="/recommendations">推荐</el-menu-item>
        <el-menu-item index="/guides">攻略</el-menu-item>
      </el-menu>
      <div class="user-area">
        <template v-if="authStore.isLoggedIn">
          <el-button v-if="authStore.user?.role === 'admin'" size="small" type="warning" text @click="$router.push('/admin')">管理</el-button>
          <el-dropdown>
            <span class="user-name">{{ authStore.user?.nickname || authStore.user?.username }}</span>
            <template #dropdown>
              <el-dropdown-item @click="authStore.logout()">退出登录</el-dropdown-item>
            </template>
          </el-dropdown>
        </template>
        <template v-else>
          <el-button @click="$router.push('/login')">登录</el-button>
          <el-button type="primary" @click="$router.push('/register')">注册</el-button>
        </template>
      </div>
    </el-header>
    <el-main>
      <router-view />
    </el-main>
    <el-footer class="app-footer">
      PoE2Wiki &copy; 2026 — 流放之路2中文攻略站
    </el-footer>
  </el-container>
</template>

<script setup lang="ts">
import { computed } from 'vue'
import { useRoute } from 'vue-router'
import { useAuthStore } from '@/stores/auth'

const authStore = useAuthStore()
const route = useRoute()
const activeMenu = computed(() => '/' + route.path.split('/')[1])
</script>

<style scoped>
.app-layout {
  min-height: 100vh;
}
.app-header {
  display: flex;
  align-items: center;
  border-bottom: 1px solid var(--border);
  padding: 0 24px;
}
.logo {
  font-size: 20px;
  font-weight: bold;
  color: var(--accent);
  cursor: pointer;
  margin-right: 32px;
  white-space: nowrap;
}
.app-menu {
  flex: 1;
  border-bottom: none !important;
  overflow: hidden;
}
.user-area {
  display: flex;
  align-items: center;
  gap: 10px;
  white-space: nowrap;
}
.user-name {
  color: var(--text-primary);
  cursor: pointer;
}
.app-footer {
  text-align: center;
  color: var(--text-secondary);
  font-size: 13px;
  padding: 16px;
  border-top: 1px solid var(--border);
}
</style>

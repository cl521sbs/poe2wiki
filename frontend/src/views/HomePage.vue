<template>
  <div class="home-page">
    <div class="hero">
      <h1>PoE2Wiki</h1>
      <p>流放之路2 中文攻略站 — 数据查询 · DPS计算 · Build推荐</p>
    </div>
    <el-row :gutter="20" class="quick-links">
      <el-col :span="6" v-for="item in links" :key="item.path">
        <el-card shadow="hover" @click="$router.push(item.path)" class="link-card">
          <h3>{{ item.title }}</h3>
          <p>{{ item.desc }}</p>
        </el-card>
      </el-col>
    </el-row>
    <div class="hero-actions" v-if="!authStore.isLoggedIn">
      <el-button size="large" @click="$router.push('/login')">登录</el-button>
      <el-button size="large" type="primary" @click="$router.push('/register')">注册账号</el-button>
    </div>
    <div class="hero-sub" v-if="authStore.isLoggedIn && authStore.user?.role === 'admin'">
      <el-button type="warning" @click="$router.push('/admin')">进入管理后台</el-button>
    </div>
  </div>
</template>

<script setup lang="ts">
import { useAuthStore } from '@/stores/auth'

const authStore = useAuthStore()

const links = [
  { title: '游戏数据库', desc: '技能 · 装备 · 天赋 · 怪物 · 通货', path: '/skills' },
  { title: 'DPS 计算器', desc: '配置技能和装备，精确计算伤害', path: '/calculator' },
  { title: '推荐 Build', desc: '前中后期职业推荐配置', path: '/recommendations' },
  { title: '攻略中心', desc: 'Build攻略 · BOSS打法 · 开荒指南', path: '/guides' },
]
</script>

<style scoped>
.hero {
  text-align: center;
  padding: 60px 20px 40px;
}
.hero h1 {
  font-size: 48px;
  color: var(--accent);
  margin: 0 0 12px;
}
.hero p {
  font-size: 18px;
  color: var(--text-secondary);
}
.hero-actions {
  text-align: center;
  padding: 0 0 20px;
}
.hero-actions .el-button {
  margin: 0 8px;
}
.hero-sub {
  text-align: center;
  padding: 0 0 30px;
}
.quick-links {
  padding: 0 40px;
}
.link-card {
  cursor: pointer;
  text-align: center;
  padding: 20px;
}
.link-card h3 { color: var(--text-primary); margin: 0 0 8px; }
.link-card p { color: var(--text-secondary); font-size: 14px; margin: 0; }
</style>

<template>
  <div class="guides-page">
    <h2>攻略中心</h2>
    <el-row :gutter="12" class="filters">
      <el-col :span="4">
        <el-select v-model="filters.category" placeholder="分类" clearable @change="search">
          <el-option label="新手" value="beginner" />
          <el-option label="Build" value="build" />
          <el-option label="Farming" value="farming" />
        </el-select>
      </el-col>
      <el-col :span="6">
        <el-input v-model="filters.keyword" placeholder="搜索攻略..." clearable @keyup.enter="search" @clear="search">
          <template #append><el-button @click="search">搜索</el-button></template>
        </el-input>
      </el-col>
    </el-row>

    <el-row :gutter="16" style="margin-top: 16px">
      <el-col v-for="guide in tableData" :key="guide.id" :span="8" style="margin-bottom: 16px">
        <el-card shadow="hover" @click="$router.push(`/guides/${guide.id}`)" class="guide-card">
          <template #header>
            <div class="guide-title">{{ guide.titleCn }}</div>
          </template>
          <div class="guide-meta">
            <el-tag size="small">{{ guide.category }}</el-tag>
            <span style="margin-left: 8px; color: var(--text-secondary); font-size: 13px">
              👁 {{ guide.viewCount }} · ❤️ {{ guide.likeCount }} · ⭐ {{ guide.favoriteCount }}
            </span>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <el-pagination
      v-if="total > 0"
      v-model:current-page="pagination.page"
      :page-size="pagination.size"
      :total="total"
      layout="total, prev, pager, next"
      @current-change="search"
      style="margin-top: 16px; justify-content: center"
    />
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { communityApi, type Guide } from '@/api/modules/community'

const tableData = ref<Guide[]>([])
const total = ref(0)
const loading = ref(false)
const pagination = reactive({ page: 1, size: 9 })
const filters = reactive({ category: '', keyword: '' })

async function search() {
  loading.value = true
  try {
    const res = await communityApi.getGuides({
      page: pagination.page,
      size: pagination.size,
      category: filters.category || undefined,
      keyword: filters.keyword || undefined,
    })
    const data = (res as any).data
    tableData.value = data.records || []
    total.value = data.total || 0
  } finally {
    loading.value = false
  }
}

onMounted(() => search())
</script>

<style scoped>
.guides-page { padding: 20px; }
.guides-page h2 { color: var(--accent); margin-bottom: 16px; }
.guide-card { cursor: pointer; transition: transform 0.2s; }
.guide-card:hover { transform: translateY(-2px); }
.guide-title { font-weight: bold; color: var(--text-primary); }
.guide-meta { display: flex; align-items: center; }
</style>

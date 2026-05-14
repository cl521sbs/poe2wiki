<template>
  <div class="guides-page">
    <h2>攻略中心</h2>
    <el-row :gutter="12" class="filters">
      <el-col :xs="24" :sm="8" :md="6">
        <el-select v-model="filters.category" placeholder="分类" clearable @change="search" style="width: 100%">
          <el-option label="新手入门" value="beginner" />
          <el-option label="Build攻略" value="build" />
          <el-option label="刷图 farming" value="farming" />
        </el-select>
      </el-col>
      <el-col :xs="24" :sm="8" :md="8">
        <el-input v-model="filters.keyword" placeholder="搜索攻略..." clearable @keyup.enter="search" @clear="search">
          <template #append><el-button @click="search">搜索</el-button></template>
        </el-input>
      </el-col>
    </el-row>

    <div v-loading="loading" style="min-height: 200px">
      <el-row :gutter="16" style="margin-top: 16px">
        <el-col v-for="guide in tableData" :key="guide.id" :xs="24" :sm="12" :md="8" style="margin-bottom: 16px">
          <el-card shadow="hover" @click="$router.push(`/guides/${guide.id}`)" class="guide-card">
            <template #header>
              <div class="guide-title">{{ guide.titleCn }}</div>
            </template>
            <div class="guide-meta">
              <el-tag size="small">{{ catLabel(guide.category) }}</el-tag>
              <span style="margin-left: 8px; color: var(--text-secondary); font-size: 13px">
                👁 {{ guide.viewCount }} · ❤️ {{ guide.likeCount }} · ⭐ {{ guide.favoriteCount }}
              </span>
            </div>
          </el-card>
        </el-col>
        <el-col v-if="!loading && tableData.length === 0" :span="24">
          <el-empty description="暂无攻略" />
        </el-col>
      </el-row>
    </div>

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

const catLabels: Record<string, string> = {
  beginner: '新手入门', build: 'Build攻略', farming: '刷图 farming',
}
function catLabel(v: string) { return catLabels[v] || v }

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
.filters .el-col { margin-bottom: 8px; }
</style>

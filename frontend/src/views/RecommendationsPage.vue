<template>
  <div class="recommendations-page">
    <h2>推荐 Build</h2>
    <el-row :gutter="12" class="filters">
      <el-col :span="4">
        <el-select v-model="filters.className" placeholder="职业" clearable @change="search">
          <el-option label="野蛮人" value="Marauder" />
          <el-option label="游侠" value="Ranger" />
          <el-option label="女巫" value="Witch" />
          <el-option label="暗影" value="Shadow" />
          <el-option label="决斗者" value="Duelist" />
          <el-option label="圣堂武僧" value="Templar" />
        </el-select>
      </el-col>
      <el-col :span="4">
        <el-select v-model="filters.stage" placeholder="阶段" clearable @change="search">
          <el-option label="开荒" value="starter" />
          <el-option label="刷图" value="mapping" />
          <el-option label="打Boss" value="bossing" />
          <el-option label="终局" value="endgame" />
        </el-select>
      </el-col>
    </el-row>

    <el-table :data="tableData" v-loading="loading" stripe style="margin-top: 16px">
      <el-table-column prop="buildName" label="Build名称" width="180" />
      <el-table-column prop="className" label="职业" width="100" />
      <el-table-column prop="ascendancy" label="升华" width="120" />
      <el-table-column prop="stage" label="阶段" width="90">
        <template #default="{ row }">
          <el-tag size="small">{{ row.stage }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="notesCn" label="说明" min-width="300" show-overflow-tooltip />
    </el-table>

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
import { communityApi } from '@/api/modules/community'

const tableData = ref<any[]>([])
const total = ref(0)
const loading = ref(false)
const pagination = reactive({ page: 1, size: 20 })
const filters = reactive({ className: '', stage: '' })

async function search() {
  loading.value = true
  try {
    const res = await communityApi.getRecommendations({
      page: pagination.page,
      size: pagination.size,
      className: filters.className || undefined,
      stage: filters.stage || undefined,
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
.recommendations-page { padding: 20px; }
.recommendations-page h2 { color: var(--accent); margin-bottom: 16px; }
</style>

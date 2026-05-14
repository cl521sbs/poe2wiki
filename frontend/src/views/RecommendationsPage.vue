<template>
  <div class="recommendations-page">
    <h2>推荐 Build</h2>
    <el-row :gutter="12" class="filters">
      <el-col :xs="24" :sm="8" :md="6">
        <el-select v-model="filters.className" placeholder="职业" clearable @change="search" style="width: 100%">
          <el-option v-for="[val, label] in classOptions" :key="val" :label="label" :value="val" />
        </el-select>
      </el-col>
      <el-col :xs="24" :sm="8" :md="6">
        <el-select v-model="filters.stage" placeholder="阶段" clearable @change="search" style="width: 100%">
          <el-option label="开荒" value="starter" />
          <el-option label="刷图" value="mapping" />
          <el-option label="打Boss" value="bossing" />
          <el-option label="终局" value="endgame" />
        </el-select>
      </el-col>
    </el-row>

    <el-table :data="tableData" v-loading="loading" stripe style="margin-top: 16px">
      <el-table-column prop="buildName" label="Build名称" width="180" />
      <el-table-column label="职业" width="100">
        <template #default="{ row }">{{ classLabel(row.className) }}</template>
      </el-table-column>
      <el-table-column prop="ascendancy" label="升华" width="120" />
      <el-table-column label="阶段" width="90">
        <template #default="{ row }">
          <el-tag size="small">{{ stageLabel(row.stage) }}</el-tag>
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

const classOptions: [string, string][] = [
  ['Marauder', '野蛮人'], ['Ranger', '游侠'], ['Witch', '女巫'],
  ['Shadow', '暗影'], ['Duelist', '决斗者'], ['Templar', '圣堂武僧'],
]
const classLabelMap: Record<string, string> = {}
for (const [v, l] of classOptions) classLabelMap[v] = l
function classLabel(v: string) { return classLabelMap[v] || v }

const stageLabels: Record<string, string> = {
  starter: '开荒', mapping: '刷图', bossing: '打Boss', endgame: '终局',
}
function stageLabel(v: string) { return stageLabels[v] || v }

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
.filters .el-col { margin-bottom: 8px; }
</style>

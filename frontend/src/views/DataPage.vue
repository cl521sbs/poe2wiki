<template>
  <div class="data-page">
    <h2>{{ title }}</h2>
    <el-row :gutter="12" class="filters">
      <el-col :xs="24" :sm="8" :md="6" v-if="typeOptions.length > 0">
        <el-select v-model="filters.type" placeholder="类型" clearable @change="search" style="width: 100%">
          <el-option v-for="[val, label] in typeOptions" :key="val" :label="label" :value="val" />
        </el-select>
      </el-col>
      <el-col :xs="24" :sm="typeOptions.length > 0 ? 8 : 12" :md="typeOptions.length > 0 ? 8 : 10">
        <el-input v-model="filters.keyword" placeholder="搜索名称..." clearable @keyup.enter="search" @clear="search">
          <template #append><el-button @click="search">搜索</el-button></template>
        </el-input>
      </el-col>
    </el-row>

    <el-table :data="tableData" v-loading="loading" stripe style="margin-top: 16px">
      <el-table-column prop="id" label="ID" width="60" />
      <el-table-column prop="nameCn" label="中文名" width="160" />
      <el-table-column prop="nameEn" label="英文名" width="200" />
      <el-table-column prop="type" label="类型" width="120">
        <template #default="{ row }">
          {{ typeLabel(row.type) }}
        </template>
      </el-table-column>
      <el-table-column v-if="props.type === 'monsters'" prop="location" label="位置" width="120" />
      <el-table-column v-if="props.type === 'currency'" prop="stackSize" label="堆叠" width="70" />
      <el-table-column prop="effectCn" label="效果" min-width="250" show-overflow-tooltip />
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
import { ref, reactive, computed, onMounted, watch } from 'vue'
import { gameDataApi } from '@/api/modules/game-data'

const props = defineProps<{ type: string }>()

const titleMap: Record<string, string> = {
  modifiers: '词缀数据库',
  passives: '天赋数据库',
  monsters: '怪物数据库',
  currency: '通货数据库',
}
// [value, label] pairs for select options and display
const typeMap: Record<string, [string, string][]> = {
  modifiers: [['prefix', '前缀'], ['suffix', '后缀']],
  passives: [['keystone', '基石天赋'], ['notable', '显著天赋'], ['mastery', '精通']],
  monsters: [['boss', 'Boss'], ['rare', '稀有'], ['magic', '魔法'], ['normal', '普通']],
  currency: [['core', '核心通货'], ['quality', '品质'], ['crafting', '工艺'], ['map', '地图']],
}

const title = computed(() => titleMap[props.type] || props.type)
const typeOptions = computed(() => typeMap[props.type] || [])

// Display map for table column
const typeLabelMap = computed(() => {
  const map: Record<string, string> = {}
  for (const [val, label] of typeOptions.value) {
    map[val] = label
  }
  return map
})
function typeLabel(val: string) {
  return typeLabelMap.value[val] || val
}

const tableData = ref<any[]>([])
const total = ref(0)
const loading = ref(false)
const pagination = reactive({ page: 1, size: 20 })
const filters = reactive({ type: '', keyword: '' })

async function search() {
  loading.value = true
  try {
    const res = await gameDataApi.getList(props.type, {
      page: pagination.page,
      size: pagination.size,
      type: filters.type || undefined,
      keyword: filters.keyword || undefined,
    })
    const data = (res as any).data
    tableData.value = data.records || []
    total.value = data.total || 0
  } finally {
    loading.value = false
  }
}

// CRITICAL FIX: re-fetch when route changes (switching database tabs)
watch(() => props.type, () => {
  filters.type = ''
  filters.keyword = ''
  pagination.page = 1
  search()
})

onMounted(() => search())
</script>

<style scoped>
.data-page { padding: 20px; }
.data-page h2 { color: var(--accent); margin-bottom: 16px; }
.filters .el-col { margin-bottom: 8px; }
</style>

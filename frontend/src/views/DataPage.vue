<template>
  <div class="data-page">
    <h2>{{ title }}</h2>
    <el-row :gutter="12" class="filters">
      <el-col :xs="24" :sm="8" :md="6" v-if="typeOptions.length > 0">
        <el-select v-model="filters.type" placeholder="类型" clearable @change="search" style="width: 100%">
          <el-option v-for="t in typeOptions" :key="t" :label="t" :value="t" />
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
      <el-table-column prop="type" label="类型" width="100" />
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
import { ref, reactive, computed, onMounted } from 'vue'
import { gameDataApi } from '@/api/modules/game-data'

const props = defineProps<{ type: string }>()

const titleMap: Record<string, string> = {
  modifiers: '词缀数据库',
  passives: '天赋数据库',
  monsters: '怪物数据库',
  currency: '通货数据库',
}
const typeMap: Record<string, string[]> = {
  modifiers: ['prefix', 'suffix'],
  passives: ['keystone', 'notable', 'mastery'],
  monsters: ['boss', 'rare', 'magic', 'normal'],
  currency: ['core', 'quality', 'crafting', 'map'],
}

const title = computed(() => titleMap[props.type] || props.type)
const typeOptions = computed(() => typeMap[props.type] || [])

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

onMounted(() => search())
</script>

<style scoped>
.data-page { padding: 20px; }
.data-page h2 { color: var(--accent); margin-bottom: 16px; }
.filters .el-col { margin-bottom: 8px; }
</style>

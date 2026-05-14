<template>
  <div class="skills-page">
    <h2>技能数据库</h2>
    <el-row :gutter="12" class="filters">
      <el-col :xs="24" :sm="8" :md="6">
        <el-select v-model="filters.type" placeholder="类型" clearable @change="search" style="width: 100%">
          <el-option label="主动技能" value="active" />
          <el-option label="辅助技能" value="support" />
        </el-select>
      </el-col>
      <el-col :xs="24" :sm="8" :md="6">
        <el-select v-model="filters.damageType" placeholder="伤害类型" clearable @change="search" style="width: 100%">
          <el-option label="物理" value="physical" />
          <el-option label="火焰" value="fire" />
          <el-option label="冰冷" value="cold" />
          <el-option label="闪电" value="lightning" />
          <el-option label="混沌" value="chaos" />
        </el-select>
      </el-col>
      <el-col :xs="24" :sm="8" :md="8">
        <el-input v-model="filters.keyword" placeholder="搜索技能名..." clearable @keyup.enter="search" @clear="search">
          <template #append>
            <el-button @click="search">搜索</el-button>
          </template>
        </el-input>
      </el-col>
    </el-row>

    <el-table :data="tableData" v-loading="loading" stripe style="margin-top: 16px">
      <el-table-column prop="id" label="ID" width="60" />
      <el-table-column prop="nameCn" label="中文名" width="140" />
      <el-table-column prop="nameEn" label="英文名" width="180" />
      <el-table-column label="类型" width="90">
        <template #default="{ row }">
          <el-tag :type="row.type === 'active' ? 'success' : 'info'" size="small">{{ row.type === 'active' ? '主动' : '辅助' }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="伤害" width="90">
        <template #default="{ row }">
          <el-tag size="small">{{ damageLabel(row.damageType) }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="manaCost" label="魔力" width="70" />
      <el-table-column prop="castTime" label="施法" width="70" />
      <el-table-column label="倍率">
        <template #default="{ row }">{{ row.damageMultiplier }}%</template>
      </el-table-column>
      <el-table-column prop="level" label="等级" width="70" />
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
import { ref, reactive, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import { gameDataApi, type Skill } from '@/api/modules/game-data'

const route = useRoute()

const tableData = ref<Skill[]>([])
const total = ref(0)
const loading = ref(false)
const pagination = reactive({ page: 1, size: 20 })
const filters = reactive({ type: '', damageType: '', keyword: '' })

const damageLabels: Record<string, string> = {
  physical: '物理', fire: '火焰', cold: '冰冷', lightning: '闪电', chaos: '混沌',
}
function damageLabel(val: string): string {
  return damageLabels[val] || val || '—'
}

async function search() {
  loading.value = true
  try {
    const res = await gameDataApi.getSkills({
      page: pagination.page,
      size: pagination.size,
      type: filters.type || undefined,
      damageType: filters.damageType || undefined,
      keyword: filters.keyword || undefined,
    })
    const data = (res as any).data
    tableData.value = data.records || []
    total.value = data.total || 0
  } finally {
    loading.value = false
  }
}

onMounted(() => {
  const kw = route.query.keyword as string
  if (kw) {
    filters.keyword = kw
  }
  search()
})
</script>

<style scoped>
.skills-page { padding: 20px; }
.skills-page h2 { color: var(--accent); margin-bottom: 16px; }
.filters .el-col { margin-bottom: 8px; }
</style>

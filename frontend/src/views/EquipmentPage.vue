<template>
  <div class="equipment-page">
    <h2>装备数据库</h2>
    <el-row :gutter="12" class="filters">
      <el-col :xs="24" :sm="8" :md="6">
        <el-select v-model="filters.category" placeholder="部位" clearable @change="search" style="width: 100%">
          <el-option label="武器" value="weapon" />
          <el-option label="头盔" value="helmet" />
          <el-option label="胸甲" value="body_armour" />
          <el-option label="手套" value="gloves" />
          <el-option label="鞋子" value="boots" />
          <el-option label="盾牌" value="shield" />
          <el-option label="戒指" value="ring" />
          <el-option label="护符" value="amulet" />
          <el-option label="腰带" value="belt" />
        </el-select>
      </el-col>
      <el-col :xs="24" :sm="8" :md="6">
        <el-select v-model="filters.rarity" placeholder="稀有度" clearable @change="search" style="width: 100%">
          <el-option label="暗金" value="unique" />
          <el-option label="稀有" value="rare" />
          <el-option label="魔法" value="magic" />
          <el-option label="普通" value="normal" />
        </el-select>
      </el-col>
      <el-col :xs="24" :sm="8" :md="8">
        <el-input v-model="filters.keyword" placeholder="搜索装备名..." clearable @keyup.enter="search" @clear="search">
          <template #append>
            <el-button @click="search">搜索</el-button>
          </template>
        </el-input>
      </el-col>
    </el-row>

    <el-table :data="tableData" v-loading="loading" stripe style="margin-top: 16px">
      <el-table-column prop="id" label="ID" width="60" />
      <el-table-column prop="nameCn" label="中文名" width="140" />
      <el-table-column prop="nameEn" label="英文名" width="200" />
      <el-table-column prop="category" label="部位" width="100" />
      <el-table-column prop="rarity" label="稀有度" width="80">
        <template #default="{ row }">
          <el-tag :type="row.rarity === 'unique' ? 'warning' : 'info'" size="small">{{ row.rarity }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="levelRequired" label="等级" width="70" />
      <el-table-column prop="armour" label="护甲" width="70" />
      <el-table-column prop="evasion" label="闪避" width="70" />
      <el-table-column prop="energyShield" label="护盾" width="70" />
      <el-table-column prop="flavorTextCn" label="描述" min-width="200" show-overflow-tooltip />
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
import { gameDataApi } from '@/api/modules/game-data'

const tableData = ref<any[]>([])
const total = ref(0)
const loading = ref(false)
const pagination = reactive({ page: 1, size: 20 })
const filters = reactive({ category: '', rarity: '', keyword: '' })

async function search() {
  loading.value = true
  try {
    const res = await gameDataApi.getEquipment({
      page: pagination.page,
      size: pagination.size,
      category: filters.category || undefined,
      rarity: filters.rarity || undefined,
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
.equipment-page { padding: 20px; }
.equipment-page h2 { color: var(--accent); margin-bottom: 16px; }
.filters .el-col { margin-bottom: 8px; }
</style>

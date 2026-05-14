<template>
  <div class="admin-page">
    <h2>管理后台</h2>
    <el-tabs v-model="activeTab">
      <el-tab-pane v-for="entity in entities" :key="entity.key" :label="entity.label" :name="entity.key">
        <el-table :data="entity.data" stripe>
          <el-table-column prop="id" label="ID" width="60" />
          <el-table-column prop="nameCn" label="中文名" width="140" />
          <el-table-column prop="nameEn" label="英文名" width="180" />
          <el-table-column prop="type" label="类型" width="100" />
          <el-table-column label="操作" width="120">
            <template #default>
              <el-button size="small" type="primary" text>编辑</el-button>
              <el-button size="small" type="danger" text>删除</el-button>
            </template>
          </el-table-column>
        </el-table>
        <el-button type="primary" style="margin-top: 12px">+ 新增 {{ entity.label }}</el-button>
      </el-tab-pane>
    </el-tabs>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { gameDataApi } from '@/api/modules/game-data'

const activeTab = ref('skills')
const entities = reactive([
  { key: 'skills', label: '技能', data: [] as any[] },
  { key: 'equipment', label: '装备', data: [] as any[] },
  { key: 'modifiers', label: '词缀', data: [] as any[] },
  { key: 'passives', label: '天赋', data: [] as any[] },
  { key: 'monsters', label: '怪物', data: [] as any[] },
  { key: 'currency', label: '通货', data: [] as any[] },
])

async function loadData(key: string) {
  const entity = entities.find(e => e.key === key)
  if (!entity || entity.data.length > 0) return
  try {
    const res = await gameDataApi.getList(key, { page: 1, size: 100 })
    entity.data = ((res as any).data?.records || []).slice(0, 10)
  } catch (e) { /* noop */ }
}

onMounted(() => loadData('skills'))
</script>

<style scoped>
.admin-page { padding: 20px; }
.admin-page h2 { color: var(--accent); margin-bottom: 16px; }
</style>

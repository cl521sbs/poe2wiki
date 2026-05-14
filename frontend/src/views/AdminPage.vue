<template>
  <div class="admin-page">
    <h2>管理后台</h2>
    <el-tabs v-model="activeTab" @tab-change="onTabChange">
      <el-tab-pane v-for="entity in entities" :key="entity.key" :label="entity.label" :name="entity.key">
        <div style="margin-bottom: 12px">
          <el-button type="primary" @click="openDialog(entity.key)">+ 新增 {{ entity.label }}</el-button>
        </div>
        <el-table :data="entity.data" v-loading="entity.loading" stripe>
          <el-table-column prop="id" label="ID" width="60" />
          <el-table-column prop="nameCn" label="中文名" width="140" />
          <el-table-column prop="nameEn" label="英文名" width="180" />
          <el-table-column prop="type" label="类型" width="100" />
          <el-table-column label="操作" width="160">
            <template #default="{ row }">
              <el-button size="small" type="primary" text @click="editRow(entity.key, row)">编辑</el-button>
              <el-button size="small" type="danger" text @click="deleteRow(entity.key, row.id)">删除</el-button>
            </template>
          </el-table-column>
        </el-table>
      </el-tab-pane>
    </el-tabs>

    <!-- Edit/Create Dialog -->
    <el-dialog v-model="dialogVisible" :title="dialogTitle" width="500px">
      <el-form :model="form" label-width="80px">
        <el-form-item label="中文名">
          <el-input v-model="form.nameCn" />
        </el-form-item>
        <el-form-item label="英文名">
          <el-input v-model="form.nameEn" />
        </el-form-item>
        <el-form-item label="类型">
          <el-input v-model="form.type" placeholder="active / weapon / prefix..." />
        </el-form-item>
        <el-form-item label="效果/描述">
          <el-input v-model="form.effectCn" type="textarea" :rows="3" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="save" :loading="saving">保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { gameDataApi } from '@/api/modules/game-data'

const activeTab = ref('skills')
const dialogVisible = ref(false)
const dialogTitle = ref('')
const saving = ref(false)
const editingEntity = ref('')
const editingId = ref<number | null>(null)

const form = reactive({ nameCn: '', nameEn: '', type: '', effectCn: '' })

const entities = reactive<{ key: string; label: string; data: any[]; loading: boolean }[]>([
  { key: 'skills', label: '技能', data: [], loading: false },
  { key: 'equipment', label: '装备', data: [], loading: false },
  { key: 'modifiers', label: '词缀', data: [], loading: false },
  { key: 'passives', label: '天赋', data: [], loading: false },
  { key: 'monsters', label: '怪物', data: [], loading: false },
  { key: 'currency', label: '通货', data: [], loading: false },
])

async function loadData(key: string) {
  const entity = entities.find(e => e.key === key)
  if (!entity || entity.data.length > 0) return
  entity.loading = true
  try {
    const res = await gameDataApi.getList(key, { page: 1, size: 100 })
    entity.data = ((res as any).data?.records || []).slice(0, 20)
  } catch (e) { /* noop */ } finally {
    entity.loading = false
  }
}

function onTabChange(key: string) {
  loadData(key as string)
}

function openDialog(entityKey: string) {
  editingEntity.value = entityKey
  editingId.value = null
  dialogTitle.value = '新增 ' + (entities.find(e => e.key === entityKey)?.label || '')
  form.nameCn = ''
  form.nameEn = ''
  form.type = ''
  form.effectCn = ''
  dialogVisible.value = true
}

function editRow(entityKey: string, row: any) {
  editingEntity.value = entityKey
  editingId.value = row.id
  dialogTitle.value = '编辑 ' + (entities.find(e => e.key === entityKey)?.label || '')
  form.nameCn = row.nameCn || ''
  form.nameEn = row.nameEn || ''
  form.type = row.type || ''
  form.effectCn = row.effectCn || ''
  dialogVisible.value = true
}

async function deleteRow(entityKey: string, id: number) {
  try {
    await (gameDataApi as any).delete(entityKey, id)
    const entity = entities.find(e => e.key === entityKey)
    if (entity) entity.data = entity.data.filter(r => r.id !== id)
  } catch (e) { /* noop */ }
}

async function save() {
  saving.value = true
  try {
    const api = gameDataApi as any
    if (editingId.value) {
      await api.update(editingEntity.value, editingId.value, { ...form })
    } else {
      await api.create(editingEntity.value, { ...form })
    }
    dialogVisible.value = false
    // Reload data
    const entity = entities.find(e => e.key === editingEntity.value)
    if (entity) {
      entity.data = []
      loadData(editingEntity.value)
    }
  } catch (e) { /* noop */ } finally {
    saving.value = false
  }
}

onMounted(() => loadData('skills'))
</script>

<style scoped>
.admin-page { padding: 20px; }
.admin-page h2 { color: var(--accent); margin-bottom: 16px; }
</style>

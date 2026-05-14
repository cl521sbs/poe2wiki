<template>
  <div class="guide-detail">
    <el-button @click="$router.back()" text style="margin-bottom: 16px">← 返回</el-button>
    <div v-if="guide" v-loading="loading">
      <h2>{{ guide.titleCn }}</h2>
      <p class="subtitle">{{ guide.titleEn }}</p>
      <div class="meta">
        <el-tag>{{ catLabel(guide.category) }}</el-tag>
        <span style="margin: 0 12px; color: var(--text-secondary); font-size: 14px">
          👁 {{ guide.viewCount }} · ❤️ {{ guide.likeCount }} · ⭐ {{ guide.favoriteCount }}
        </span>
        <el-tag v-if="guide.classRestriction" type="warning" size="small" style="margin-left: 8px">{{ guide.classRestriction }}限定</el-tag>
      </div>
      <el-divider />
      <div class="content" v-html="renderMarkdown(guide.contentCn)" />
    </div>
    <el-empty v-else-if="!loading" description="攻略不存在" />
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import { communityApi, type Guide } from '@/api/modules/community'

const route = useRoute()
const guide = ref<Guide | null>(null)
const loading = ref(false)

const catLabels: Record<string, string> = {
  beginner: '新手入门', build: 'Build攻略', farming: '刷图 farming',
}
function catLabel(v: string) { return catLabels[v] || v }

function renderMarkdown(md: string): string {
  if (!md) return ''
  return md
    .replace(/&/g, '&amp;')
    .replace(/</g, '&lt;')
    .replace(/>/g, '&gt;')
    .replace(/^### (.+)$/gm, '<h3>$1</h3>')
    .replace(/^## (.+)$/gm, '<h2>$1</h2>')
    .replace(/^# (.+)$/gm, '<h1>$1</h1>')
    .replace(/\*\*(.+?)\*\*/g, '<strong>$1</strong>')
    .replace(/\*(.+?)\*/g, '<em>$1</em>')
    .replace(/\\n/g, '<br>')
    .replace(/\n\n/g, '</p><p>')
    .replace(/\n/g, '<br>')
}

onMounted(async () => {
  loading.value = true
  try {
    const id = Number(route.params.id)
    const res = await communityApi.getGuide(id)
    guide.value = (res as any).data
  } finally {
    loading.value = false
  }
})
</script>

<style scoped>
.guide-detail { padding: 20px; max-width: 900px; }
.guide-detail h2 { color: var(--accent); margin-bottom: 4px; }
.subtitle { color: var(--text-secondary); font-size: 14px; margin: 0 0 8px; }
.meta { display: flex; align-items: center; margin-bottom: 8px; }
.content { line-height: 1.9; color: var(--text-primary); }
.content :deep(h2) { color: var(--accent); margin: 24px 0 12px; }
.content :deep(h3) { color: var(--text-primary); margin: 18px 0 8px; }
.content :deep(strong) { color: var(--accent); }
</style>

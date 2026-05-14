<template>
  <div class="guide-detail">
    <el-button @click="$router.back()" text style="margin-bottom: 16px">← 返回</el-button>
    <div v-if="guide">
      <h2>{{ guide.titleCn }}</h2>
      <div class="meta">
        <el-tag>{{ guide.category }}</el-tag>
        <span style="margin: 0 12px">👁 {{ guide.viewCount }} · ❤️ {{ guide.likeCount }} · ⭐ {{ guide.favoriteCount }}</span>
      </div>
      <el-divider />
      <div class="content" v-html="renderMarkdown(guide.contentCn)" />
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import { communityApi, type Guide } from '@/api/modules/community'

const route = useRoute()
const guide = ref<Guide | null>(null)

function renderMarkdown(md: string): string {
  return md.replace(/^### (.+)$/gm, '<h3>$1</h3>').replace(/^## (.+)$/gm, '<h2>$1</h2>').replace(/\n/g, '<br>')
}

onMounted(async () => {
  const id = Number(route.params.id)
  const res = await communityApi.getGuide(id)
  guide.value = (res as any).data
})
</script>

<style scoped>
.guide-detail { padding: 20px; max-width: 900px; }
.guide-detail h2 { color: var(--accent); }
.meta { display: flex; align-items: center; color: var(--text-secondary); }
.content { line-height: 1.8; color: var(--text-primary); }
</style>

<template>
  <div class="calculator-page">
    <h2>DPS 计算器</h2>

    <el-tabs v-model="activeTab">
      <el-tab-pane label="单Build计算" name="single">
        <el-form :model="form" label-width="100px" class="calc-form">
          <el-form-item label="技能ID">
            <el-input-number v-model="form.skillId" :min="1" />
          </el-form-item>
          <el-form-item label="技能等级">
            <el-input-number v-model="form.skillLevel" :min="1" :max="30" />
          </el-form-item>
          <el-form-item label="物理伤害">
            <el-row :gutter="8" style="width: 100%">
              <el-col :span="11">
                <el-input-number v-model="form.weapon.physMin" placeholder="最小值" :min="0" controls-position="right" style="width: 100%" />
              </el-col>
              <el-col :span="2" style="text-align: center; line-height: 32px">—</el-col>
              <el-col :span="11">
                <el-input-number v-model="form.weapon.physMax" placeholder="最大值" :min="0" controls-position="right" style="width: 100%" />
              </el-col>
            </el-row>
          </el-form-item>
          <el-form-item label="攻击速度">
            <el-input-number v-model="form.weapon.attackSpeed" :min="0.1" :step="0.1" :precision="1" />
          </el-form-item>
          <el-form-item label="暴击率%">
            <el-input-number v-model="form.weapon.critChance" :min="0" :max="100" :step="0.1" />
          </el-form-item>
          <el-form-item label="暴击倍率%">
            <el-input-number v-model="form.weapon.critMultiplier" :min="100" :step="1" />
          </el-form-item>
          <el-divider content-position="left">天赋加成</el-divider>
          <el-form-item label="伤害加成%">
            <el-input-number v-model="form.passives.damageInc" :min="0" />
          </el-form-item>
          <el-form-item label="攻速加成%">
            <el-input-number v-model="form.passives.attackSpeedInc" :min="0" />
          </el-form-item>
          <el-form-item label="穿透%">
            <el-input-number v-model="form.passives.penetration" :min="0" :max="100" />
          </el-form-item>
          <el-divider content-position="left">目标抗性</el-divider>
          <el-form-item label="闪电抗性%">
            <el-input-number v-model="form.target.lightningRes" :min="0" :max="100" />
          </el-form-item>
          <el-form-item>
            <el-button type="primary" @click="calculate" :loading="calculating">计算 DPS</el-button>
          </el-form-item>
        </el-form>

        <el-descriptions v-if="result" title="计算结果" :column="2" border class="calc-result">
          <el-descriptions-item label="平均伤害">{{ result.avgDamage }}</el-descriptions-item>
          <el-descriptions-item label="DPS">{{ result.dps }}</el-descriptions-item>
          <el-descriptions-item label="暴击DPS">{{ result.critDps }}</el-descriptions-item>
          <el-descriptions-item label="有效DPS">
            <el-tag type="success" size="large">{{ result.effectiveDps }}</el-tag>
          </el-descriptions-item>
        </el-descriptions>
      </el-tab-pane>

      <el-tab-pane label="Build对比" name="compare">
        <el-row :gutter="20">
          <el-col :span="12">
            <el-card shadow="hover">
              <template #header><strong>Build A</strong></template>
              <el-form label-width="80px" size="small">
                <el-form-item label="技能ID"><el-input-number v-model="cmpA.skillId" :min="1" size="small" /></el-form-item>
                <el-form-item label="物理 Min"><el-input-number v-model="cmpA.physMin" :min="0" size="small" /></el-form-item>
                <el-form-item label="物理 Max"><el-input-number v-model="cmpA.physMax" :min="0" size="small" /></el-form-item>
                <el-form-item label="攻速"><el-input-number v-model="cmpA.attackSpeed" :min="0.1" :step="0.1" size="small" /></el-form-item>
                <el-form-item label="暴击率%"><el-input-number v-model="cmpA.critChance" :min="0" :max="100" size="small" /></el-form-item>
              </el-form>
            </el-card>
          </el-col>
          <el-col :span="12">
            <el-card shadow="hover">
              <template #header><strong>Build B</strong></template>
              <el-form label-width="80px" size="small">
                <el-form-item label="技能ID"><el-input-number v-model="cmpB.skillId" :min="1" size="small" /></el-form-item>
                <el-form-item label="物理 Min"><el-input-number v-model="cmpB.physMin" :min="0" size="small" /></el-form-item>
                <el-form-item label="物理 Max"><el-input-number v-model="cmpB.physMax" :min="0" size="small" /></el-form-item>
                <el-form-item label="攻速"><el-input-number v-model="cmpB.attackSpeed" :min="0.1" :step="0.1" size="small" /></el-form-item>
                <el-form-item label="暴击率%"><el-input-number v-model="cmpB.critChance" :min="0" :max="100" size="small" /></el-form-item>
              </el-form>
            </el-card>
          </el-col>
        </el-row>
        <div style="text-align:center; margin: 20px 0">
          <el-button type="primary" @click="compareBuilds" :loading="comparing">对比两个 Build</el-button>
        </div>
        <el-table v-if="cmpResult" :data="cmpTableData" stripe style="max-width: 500px; margin: 0 auto">
          <el-table-column prop="label" label="指标" width="120" />
          <el-table-column prop="a" label="Build A" />
          <el-table-column prop="b" label="Build B" />
        </el-table>
      </el-tab-pane>
    </el-tabs>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, computed } from 'vue'
import { calculatorApi, type DpsResponse } from '@/api/modules/calculator'

const activeTab = ref('single')
const calculating = ref(false)
const result = ref<DpsResponse | null>(null)

const form = reactive({
  skillId: 21,
  skillLevel: 20,
  weapon: { physMin: 50, physMax: 100, attackSpeed: 1.5, critChance: 6.0, critMultiplier: 150.0 },
  passives: { damageInc: 100, attackSpeedInc: 10, critChanceInc: 50, critMultiInc: 30, penetration: 10 },
  target: { fireRes: 30, coldRes: 30, lightningRes: 30, chaosRes: 10 },
})

async function calculate() {
  calculating.value = true
  try {
    const res = await calculatorApi.calculate({
      skills: [{ skillId: form.skillId, level: form.skillLevel, supportGems: [] }],
      equipment: { weapon: form.weapon },
      passives: form.passives,
      target: form.target,
      buffs: [],
    })
    result.value = (res as any).data
  } finally {
    calculating.value = false
  }
}

// Build comparison
const comparing = ref(false)
const cmpA = reactive({ skillId: 21, physMin: 50, physMax: 100, attackSpeed: 1.5, critChance: 6.0 })
const cmpB = reactive({ skillId: 22, physMin: 30, physMax: 60, attackSpeed: 1.2, critChance: 7.0 })
const cmpResult = ref<{ avgDamage: { a: number; b: number }; dps: { a: number; b: number }; effectiveDps: { a: number; b: number } } | null>(null)

const cmpTableData = computed(() => {
  if (!cmpResult.value) return []
  return [
    { label: '平均伤害', a: cmpResult.value.avgDamage.a, b: cmpResult.value.avgDamage.b },
    { label: 'DPS', a: cmpResult.value.dps.a, b: cmpResult.value.dps.b },
    { label: '有效DPS', a: cmpResult.value.effectiveDps.a, b: cmpResult.value.effectiveDps.b },
  ]
})

async function compareBuilds() {
  comparing.value = true
  try {
    const res = await calculatorApi.compare({
      buildA: {
        skills: [{ skillId: cmpA.skillId, level: 20, supportGems: [] }],
        equipment: { weapon: { physMin: cmpA.physMin, physMax: cmpA.physMax, attackSpeed: cmpA.attackSpeed, critChance: cmpA.critChance, critMultiplier: 150 } },
        passives: { damageInc: 0, attackSpeedInc: 0, critChanceInc: 0, critMultiInc: 0, penetration: 0 },
        target: { fireRes: 0, coldRes: 0, lightningRes: 0, chaosRes: 0 },
        buffs: [],
      },
      buildB: {
        skills: [{ skillId: cmpB.skillId, level: 20, supportGems: [] }],
        equipment: { weapon: { physMin: cmpB.physMin, physMax: cmpB.physMax, attackSpeed: cmpB.attackSpeed, critChance: cmpB.critChance, critMultiplier: 150 } },
        passives: { damageInc: 0, attackSpeedInc: 0, critChanceInc: 0, critMultiInc: 0, penetration: 0 },
        target: { fireRes: 0, coldRes: 0, lightningRes: 0, chaosRes: 0 },
        buffs: [],
      },
    })
    const data = (res as any).data
    cmpResult.value = {
      avgDamage: { a: data.buildA.avgDamage, b: data.buildB.avgDamage },
      dps: { a: data.buildA.dps, b: data.buildB.dps },
      effectiveDps: { a: data.buildA.effectiveDps, b: data.buildB.effectiveDps },
    }
  } finally {
    comparing.value = false
  }
}
</script>

<style scoped>
.calculator-page { padding: 20px; }
.calculator-page h2 { color: var(--accent); margin-bottom: 16px; }
.calc-form { max-width: 520px; }
.calc-result { max-width: 520px; margin-top: 24px; }
</style>

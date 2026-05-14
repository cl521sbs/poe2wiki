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
        <el-empty description="Build对比功能开发中">
          <el-button type="primary" @click="activeTab = 'single'">先去计算单Build</el-button>
        </el-empty>
      </el-tab-pane>
    </el-tabs>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive } from 'vue'
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
</script>

<style scoped>
.calculator-page { padding: 20px; }
.calculator-page h2 { color: var(--accent); margin-bottom: 16px; }
.calc-form { max-width: 520px; }
.calc-result { max-width: 520px; margin-top: 24px; }
</style>

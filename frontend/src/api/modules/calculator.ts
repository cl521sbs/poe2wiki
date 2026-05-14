import request from '../request'

export interface DpsRequest {
  skills: { skillId: number; level: number; supportGems: number[] }[]
  equipment: {
    weapon: WeaponConfig
    offhand?: WeaponConfig
  }
  passives: PassiveConfig
  target: { fireRes: number; coldRes: number; lightningRes: number; chaosRes: number }
  buffs: string[]
}

interface WeaponConfig {
  physMin: number
  physMax: number
  fireMin?: number
  fireMax?: number
  coldMin?: number
  coldMax?: number
  lightningMin?: number
  lightningMax?: number
  chaosMin?: number
  chaosMax?: number
  attackSpeed: number
  critChance: number
  critMultiplier: number
}

interface PassiveConfig {
  damageInc: number
  attackSpeedInc: number
  critChanceInc: number
  critMultiInc: number
  penetration: number
}

export interface DpsResponse {
  avgDamage: number
  dps: number
  critDps: number
  effectiveDps: number
  breakdown: any
}

export const calculatorApi = {
  calculate(req: DpsRequest) {
    return request.post<DpsResponse>('/calculator/dps', req)
  },
  compare(buildA: DpsRequest, buildB: DpsRequest) {
    return request.post<Record<string, DpsResponse>>('/calculator/compare', { buildA, buildB })
  },
}

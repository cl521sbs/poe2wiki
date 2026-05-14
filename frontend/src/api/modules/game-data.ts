import request from '../request'

export interface Skill {
  id: number
  nameCn: string
  nameEn: string
  type: string
  tags: string
  level: number
  attrRequirements: string
  manaCost: number
  cooldown: number
  castTime: number
  damageMultiplier: number
  damageType: string
  effectCn: string
  effectEn: string
  iconUrl: string
}

export interface Equipment {
  id: number
  nameCn: string
  nameEn: string
  category: string
  subcategory: string
  rarity: string
  levelRequired: number
  attrRequirements: string
  armour: number
  evasion: number
  energyShield: number
  flavorTextCn: string
  flavorTextEn: string
  iconUrl: string
}

export interface PageResult<T> {
  records: T[]
  total: number
  page: number
  size: number
}

export const gameDataApi = {
  // Skills
  getSkills(params: { page?: number; size?: number; type?: string; damageType?: string; keyword?: string }) {
    return request.get<PageResult<Skill>>('/game-data/skills', { params })
  },
  getSkill(id: number) {
    return request.get<Skill>(`/game-data/skills/${id}`)
  },

  // Equipment
  getEquipment(params: { page?: number; size?: number; category?: string; rarity?: string; keyword?: string }) {
    return request.get<PageResult<Equipment>>('/game-data/equipment', { params })
  },
  getEquipmentById(id: number) {
    return request.get<Equipment>(`/game-data/equipment/${id}`)
  },

  // Generic paginated list
  getList(type: string, params: Record<string, any>) {
    return request.get<PageResult<any>>(`/game-data/${type}`, { params })
  },

  // Admin CRUD (proxied to /api/admin/data)
  create(type: string, data: Record<string, any>) {
    return request.post(`/admin/data/${type}`, data)
  },
  update(type: string, id: number, data: Record<string, any>) {
    return request.put(`/admin/data/${type}/${id}`, data)
  },
  delete(type: string, id: number) {
    return request.delete(`/admin/data/${type}/${id}`)
  },
}

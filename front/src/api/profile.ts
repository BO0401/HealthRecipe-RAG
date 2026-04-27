import http from './http'

export interface UserProfile {
  height: number
  weight: number
  allergens: string[]
}

export interface InventoryItem {
  name: string
  quantity: number
}

export interface ProfileData {
  user: UserProfile
  inventory: InventoryItem[]
}

export const profileApi = {
  get: () =>
    http.get<ProfileData>('/profile'),

  save: (data: ProfileData) =>
    http.put<ProfileData>('/profile', data)
}

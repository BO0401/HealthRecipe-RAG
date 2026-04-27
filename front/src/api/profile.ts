import http from './http'
import { ProfileDataSchema, type ProfileData } from '../types/api'

export const profileApi = {
  get: async (): Promise<ProfileData> => {
    const data = await http.get<ProfileData>('/profile')
    return ProfileDataSchema.parse(data)
  },

  save: async (data: ProfileData): Promise<ProfileData> => {
    ProfileDataSchema.parse(data)
    const result = await http.put<ProfileData>('/profile', data)
    return ProfileDataSchema.parse(result)
  }
}

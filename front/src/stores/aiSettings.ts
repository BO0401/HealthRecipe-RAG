import { defineStore } from 'pinia'
import { reactive } from 'vue'

const STORAGE_KEY = 'health-recipe-ai-settings'

export interface AIConfig {
  baseUrl: string
  apiKey: string
  model: string
  temperature: number
  timeout: number
}

const getEnv = (key: string, fallback = ''): string => {
  try {
    return (import.meta as any)?.env?.[key] ?? fallback
  } catch {
    return fallback
  }
}

const defaultConfig = (): AIConfig => ({
  baseUrl: getEnv('VITE_AI_BASE_URL'),
  apiKey: getEnv('VITE_AI_API_KEY'),
  model: getEnv('VITE_AI_MODEL'),
  temperature: parseFloat(getEnv('VITE_AI_TEMPERATURE', '0.7')) || 0.7,
  timeout: parseInt(getEnv('VITE_AI_TIMEOUT', '300000'), 10) || 300000
})

const loadConfig = (): AIConfig => {
  try {
    const saved = localStorage.getItem(STORAGE_KEY)
    if (saved) {
      const parsed = JSON.parse(saved)
      const def = defaultConfig()
      return { ...def, ...parsed }
    }
  } catch {
    console.warn('[aiSettings] failed to load saved config')
  }
  return defaultConfig()
}

export const useAiSettingsStore = defineStore('aiSettings', () => {
  const config = reactive<AIConfig>(loadConfig())

  const save = () => {
    try {
      localStorage.setItem(STORAGE_KEY, JSON.stringify(config))
    } catch {
      console.warn('[aiSettings] failed to save config')
    }
  }

  const updateConfig = (partial: Partial<AIConfig>) => {
    Object.assign(config, partial)
    save()
  }

  const resetConfig = () => {
    const def = defaultConfig()
    Object.assign(config, def)
    save()
  }

  return {
    config,
    updateConfig,
    resetConfig
  }
})

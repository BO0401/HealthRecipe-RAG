import axios, { type AxiosInstance, type AxiosError, type InternalAxiosRequestConfig, type AxiosRequestConfig } from 'axios'
import { ElMessage } from 'element-plus'

const MAX_RETRIES = parseInt(import.meta.env.VITE_API_MAX_RETRIES || '3', 10)
const RETRY_DELAY = 1000

interface RetryConfig extends InternalAxiosRequestConfig {
  _retryCount?: number
}

const instance: AxiosInstance = axios.create({
  baseURL: import.meta.env.VITE_API_BASE_URL || '/api',
  timeout: parseInt(import.meta.env.VITE_API_TIMEOUT || '30000', 10),
  headers: { 'Content-Type': 'application/json' }
})

interface HttpClient {
  get<T>(url: string, config?: AxiosRequestConfig): Promise<T>
  post<T>(url: string, data?: unknown, config?: AxiosRequestConfig): Promise<T>
  put<T>(url: string, data?: unknown, config?: AxiosRequestConfig): Promise<T>
  delete<T = void>(url: string, config?: AxiosRequestConfig): Promise<T>
}

const http: HttpClient = {
  get: <T>(url: string, config?: AxiosRequestConfig) => instance.get<any>(url, config).then(res => res.data) as Promise<T>,
  post: <T>(url: string, data?: unknown, config?: AxiosRequestConfig) => instance.post<any>(url, data, config).then(res => res.data) as Promise<T>,
  put: <T>(url: string, data?: unknown, config?: AxiosRequestConfig) => instance.put<any>(url, data, config).then(res => res.data) as Promise<T>,
  delete: <T = void>(url: string, config?: AxiosRequestConfig) => instance.delete<any>(url, config).then(res => res.data) as Promise<T>
}

let isRefreshing = false
let pendingRequests: Array<(token: string) => void> = []

function getToken(): string | null {
  return localStorage.getItem('token')
}

function setToken(token: string): void {
  localStorage.setItem('token', token)
}

function getRefreshToken(): string | null {
  return localStorage.getItem('refreshToken')
}

async function refreshToken(): Promise<string> {
  const refresh = getRefreshToken()
  const response = await axios.post('/api/auth/refresh', { refreshToken: refresh })
  const newToken = response.data.data?.token || response.data.token
  setToken(newToken)
  return newToken
}

instance.interceptors.request.use(
  (config: InternalAxiosRequestConfig) => {
    const token = getToken()
    if (token && config.headers) {
      config.headers.Authorization = `Bearer ${token}`
    }
    return config
  },
  (error) => Promise.reject(error)
)

instance.interceptors.response.use(
  (response) => {
    const data = response.data
    if (data && data.code !== undefined && data.code !== 200) {
      ElMessage.error(data.message || '请求失败')
      return Promise.reject(new Error(data.message || '请求失败'))
    }
    return data
  },
  async (error: AxiosError) => {
    const config = error.config as RetryConfig | undefined
    const status = error.response?.status
    const msg = (error.response?.data as any)?.message || error.message

    if (status === 401 && config && !config._retryCount) {
      if (!isRefreshing) {
        isRefreshing = true
        try {
          const newToken = await refreshToken()
          isRefreshing = false
          pendingRequests.forEach((cb) => cb(newToken))
          pendingRequests = []
          if (config.headers) {
            config.headers.Authorization = `Bearer ${newToken}`
          }
          return instance(config)
        } catch {
          isRefreshing = false
          pendingRequests = []
          localStorage.removeItem('token')
          localStorage.removeItem('refreshToken')
          ElMessage.error('登录已过期，请重新登录')
          return Promise.reject(error)
        }
      } else {
        return new Promise((resolve) => {
          pendingRequests.push((token: string) => {
            if (config.headers) {
              config.headers.Authorization = `Bearer ${token}`
            }
            resolve(instance(config))
          })
        })
      }
    }

    if (config && config._retryCount !== undefined && config._retryCount < MAX_RETRIES) {
      config._retryCount = (config._retryCount || 0) + 1
      await new Promise((resolve) => setTimeout(resolve, RETRY_DELAY * config._retryCount!))
      return instance(config)
    }

    switch (status) {
      case 400:
        ElMessage.error(`请求参数错误: ${msg}`)
        break
      case 403:
        ElMessage.error('没有权限执行此操作')
        break
      case 404:
        ElMessage.error(`请求的资源不存在: ${error.config?.url}`)
        break
      case 500:
        ElMessage.error(`服务器内部错误: ${msg}`)
        break
      default:
        if (error.code === 'ECONNABORTED') {
          ElMessage.error('请求超时，请检查网络连接')
        } else if (!error.response) {
          ElMessage.error('网络连接失败，请检查后端服务是否启动')
        } else {
          ElMessage.error(`请求失败: ${msg}`)
        }
    }

    return Promise.reject(error)
  }
)

export { getToken, setToken }
export default http

import axios, { type AxiosInstance, type AxiosError, type InternalAxiosRequestConfig, type AxiosRequestConfig } from 'axios'
import { ElMessage } from 'element-plus'

const MAX_RETRIES = parseInt(import.meta.env.VITE_API_MAX_RETRIES || '3', 10)
const RETRY_DELAY = 1000

interface RetryConfig extends InternalAxiosRequestConfig {
  _retryCount?: number
  _authHandled?: boolean
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
  get: <T>(url: string, config?: AxiosRequestConfig) => instance.get<any, T>(url, config),
  post: <T>(url: string, data?: unknown, config?: AxiosRequestConfig) => instance.post<any, T>(url, data, config),
  put: <T>(url: string, data?: unknown, config?: AxiosRequestConfig) => instance.put<any, T>(url, data, config),
  delete: <T = void>(url: string, config?: AxiosRequestConfig) => instance.delete<any, T>(url, config)
}

function getToken(): string | null {
  return localStorage.getItem('token')
}

function setToken(token: string): void {
  localStorage.setItem('token', token)
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
    const body = response.data
    if (body && body.code !== undefined) {
      if (body.code !== 200) {
        ElMessage.error(body.message || '请求失败')
        return Promise.reject(new Error(body.message || '请求失败'))
      }
      return body.data
    }
    return body
  },
  async (error: AxiosError) => {
    const config = error.config as RetryConfig | undefined
    const status = error.response?.status
    const msg = (error.response?.data as any)?.message || error.message

    if (status === 401 && config && !config._authHandled) {
      config._authHandled = true
      localStorage.removeItem('token')
      localStorage.removeItem('refreshToken')
      ElMessage.error('会话已失效，请重新登录')
      return Promise.reject(error)
    }

    const method = String(config?.method || 'get').toLowerCase()
    const canRetry =
      Boolean(config) &&
      MAX_RETRIES > 0 &&
      ['get', 'head', 'options'].includes(method) &&
      (status === undefined || status >= 500 || error.code === 'ECONNABORTED')

    if (canRetry) {
      config!._retryCount = config!._retryCount ?? 0
      if (config!._retryCount < MAX_RETRIES) {
        config!._retryCount += 1
        await new Promise((resolve) => setTimeout(resolve, RETRY_DELAY * config!._retryCount!))
        return instance(config!)
      }
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

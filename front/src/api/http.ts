import axios, { type AxiosInstance, type AxiosError } from 'axios'
import { ElMessage } from 'element-plus'

const http: AxiosInstance = axios.create({
  baseURL: '/api',
  timeout: 30000,
  headers: { 'Content-Type': 'application/json' }
})

http.interceptors.response.use(
  response => response,
  (error: AxiosError) => {
    const status = error.response?.status
    const msg = (error.response?.data as any)?.message || error.message

    switch (status) {
      case 400:
        ElMessage.error(`请求参数错误: ${msg}`)
        break
      case 401:
        ElMessage.error('登录已过期，请重新登录')
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

export default http

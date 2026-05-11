import axios from 'axios'
import type { ApiResponse } from '@/types'

const api = axios.create({
  baseURL: '/api',
  timeout: 15000,
  headers: { 'Content-Type': 'application/json' },
})

api.interceptors.request.use((config) => {
  const token = sessionStorage.getItem('token')
  if (token) {
    config.headers.Authorization = `Bearer ${token}`
  }
  return config
})

api.interceptors.response.use(
  (response) => response,
  (error) => {
    if (error.response?.status === 401) {
      sessionStorage.removeItem('token')
      sessionStorage.removeItem('user')
      window.location.href = '/login'
    }
    return Promise.reject(error)
  }
)

export async function get<T>(url: string, params?: Record<string, unknown>): Promise<ApiResponse<T>> {
  const res = await api.get<ApiResponse<T>>(url, { params })
  return res.data
}

export async function post<T>(url: string, data?: unknown): Promise<ApiResponse<T>> {
  const res = await api.post<ApiResponse<T>>(url, data)
  return res.data
}

export async function put<T>(url: string, data?: unknown): Promise<ApiResponse<T>> {
  const res = await api.put<ApiResponse<T>>(url, data)
  return res.data
}

export default api

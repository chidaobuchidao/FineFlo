import { post } from './index'
import type { LoginRequest, LoginResponse, RegisterRequest } from '@/types'

export function login(data: LoginRequest) {
  return post<LoginResponse>('/auth/login', data)
}

export function register(data: RegisterRequest) {
  return post<null>('/auth/register', data)
}

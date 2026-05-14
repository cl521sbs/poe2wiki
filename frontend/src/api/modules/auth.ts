import request from '../request'

export interface LoginParams {
  username: string
  password: string
}

export interface RegisterParams {
  username: string
  email: string
  password: string
}

export const authApi = {
  login: (params: LoginParams) =>
    request.post('/auth/login', params),
  register: (params: RegisterParams) =>
    request.post('/auth/register', params),
  refreshToken: (refreshToken: string) =>
    request.post('/auth/refresh', { refreshToken }),
  getProfile: () =>
    request.get('/auth/profile'),
  updateProfile: (data: { nickname?: string; bio?: string }) =>
    request.put('/auth/profile', data),
}

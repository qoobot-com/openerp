import request from '../index';

export const login = (data: { username: string; password: string }) => {
  return request.post('/auth/login', data);
};

export const logout = () => {
  return request.post('/auth/logout');
};

export const getUserInfo = () => {
  return request.get('/auth/user');
};

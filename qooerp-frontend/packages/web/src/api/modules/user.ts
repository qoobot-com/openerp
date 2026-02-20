import request from '../index';

export const getUserList = (params: any) => {
  return request.get('/user/list', { params });
};

export const getUserById = (id: string) => {
  return request.get(`/user/${id}`);
};

export const createUser = (data: any) => {
  return request.post('/user', data);
};

export const updateUser = (id: string, data: any) => {
  return request.put(`/user/${id}`, data);
};

export const deleteUser = (id: string) => {
  return request.delete(`/user/${id}`);
};

import request from '../index';

export const getDictList = (params: any) => {
  return request.get('/dict/list', { params });
};

export const getConfig = () => {
  return request.get('/config');
};

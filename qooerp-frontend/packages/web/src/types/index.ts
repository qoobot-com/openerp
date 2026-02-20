export interface ApiResponse<T = any> {
  code: number;
  message: string;
  data: T;
}

export interface User {
  id: string;
  username: string;
  email?: string;
  avatar?: string;
}

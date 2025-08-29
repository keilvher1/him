import axios from 'axios';
import { Article, Category, LoginRequest, RegisterRequest, Page } from '../types';

const API_BASE_URL = process.env.NODE_ENV === 'production' 
  ? '/api' 
  : 'http://localhost:8081/api';

const api = axios.create({
  baseURL: API_BASE_URL,
  withCredentials: true,
  headers: {
    'Content-Type': 'application/json',
  },
});

// Auth API
export const authAPI = {
  login: (credentials: LoginRequest) => 
    api.post('/auth/login', credentials),
  
  logout: () => 
    api.post('/auth/logout'),
  
  register: (userData: RegisterRequest) => 
    api.post('/auth/register', userData),
  
  getCurrentUser: () => 
    api.get('/auth/me'),
};

// Articles API
export const articlesAPI = {
  getAll: (page: number = 0, size: number = 10) => 
    api.get<Page<Article>>(`/articles?page=${page}&size=${size}`),
  
  getById: (id: number) => 
    api.get<Article>(`/articles/${id}`),
  
  getByCategory: (categoryName: string, page: number = 0, size: number = 10) => 
    api.get<Page<Article>>(`/articles/category/${categoryName}?page=${page}&size=${size}`),
  
  getFeatured: () => 
    api.get<Article[]>('/articles/featured'),
  
  search: (keyword: string, page: number = 0, size: number = 10) => 
    api.get<Page<Article>>(`/articles/search?keyword=${keyword}&page=${page}&size=${size}`),
  
  create: (articleData: FormData) => 
    api.post('/articles', articleData, {
      headers: { 'Content-Type': 'multipart/form-data' }
    }),
  
  update: (id: number, articleData: FormData) => 
    api.put(`/articles/${id}`, articleData, {
      headers: { 'Content-Type': 'multipart/form-data' }
    }),
  
  delete: (id: number) => 
    api.delete(`/articles/${id}`),
};

// Categories API
export const categoriesAPI = {
  getAll: () => 
    api.get<Category[]>('/categories'),
  
  getByName: (name: string) => 
    api.get<Category>(`/categories/${name}`),
};

export default api;
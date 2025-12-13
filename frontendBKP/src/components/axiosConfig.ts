import axios from 'axios';
import { logout } from "../authService";

const api = axios.create({
  baseURL: 'http://localhost:8080/api',
});

// Interceptor de requisição para adicionar o token
api.interceptors.request.use((config) => {
  const token = localStorage.getItem('token');
  if (!config.headers) {
    config.headers = {};
  }
  if (token && !config.headers.Authorization) {
    config.headers.Authorization = `Bearer ${token}`;
  }
  return config;
});

// Interceptor de resposta para tratar erros de autenticação
api.interceptors.response.use(
  response => response,
  error => {
    if (error.response?.status === 401 || error.response?.status === 403) {
      if (import.meta.env.DEV) {
        console.warn('Autenticação falhou, realizando logout...');
      }
      logout();
    } else if (import.meta.env.DEV) {
      console.error('Erro de API:', error);
    }
    return Promise.reject(error);
  }
);

export default api;

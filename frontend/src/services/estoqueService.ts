import api from './api';

export const listarEstoque = () => api.get('/estoque');
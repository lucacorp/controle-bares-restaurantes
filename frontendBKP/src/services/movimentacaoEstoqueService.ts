import api from './api';

export const movimentarEstoque = (movimentacao: { produtoId: number; tipo: string; quantidade: number }) =>
  api.post('/movimentacoes', movimentacao);
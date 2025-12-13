// services/comandaService.ts
import api from './api';
import { ComandaDTO } from '../types/ComandaDTO';
import { ComandaResumo } from "@/types/ComandaResumo";
const API = '/comandas';

export async function buscarResumoComanda(comandaId: number): Promise<ComandaResumo[]> {
  const response = await api.get(`${API}/${comandaId}/resumo`);
  return response.data;
}

export async function listarComandas(): Promise<ComandaDTO[]> {
  const response = await api.get(API);
  return response.data;
}

export async function criarComanda(mesaId: number): Promise<ComandaDTO> {
  const response = await api.post(`${API}/criar/${mesaId}`);
  return response.data;
}

export async function fecharComanda(id: number): Promise<ComandaDTO> {
  const response = await api.put(`${API}/${id}/fechar`);
  return response.data;
}

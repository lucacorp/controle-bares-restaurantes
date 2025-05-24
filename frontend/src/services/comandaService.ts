// services/comandaService.ts
import axios from 'axios';
import { ComandaDTO } from '../types/ComandaDTO';
import { ComandaResumo } from "@/types/ComandaResumo";
const API = '/api/comandas';



export async function buscarResumoComanda(comandaId: number): Promise<ComandaResumo[]> {
  const response = await axios.get(`/api/comandas/${comandaId}/resumo`);
  return response.data;
}


export async function listarComandas(): Promise<ComandaDTO[]> {
  const response = await axios.get(API);
  return response.data;
}

export async function criarComanda(mesaId: number): Promise<ComandaDTO> {
  const response = await axios.post(API, { mesaId });
  return response.data;
}

export async function fecharComanda(id: number): Promise<ComandaDTO> {
  const response = await axios.put(`${API}/${id}/fechar`);
  return response.data;
}

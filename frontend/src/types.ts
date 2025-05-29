export interface ReceitaItemDTO {
  produtoId: number;
  quantidade: number;
}

export interface ReceitaDTO {
  id: number;
  nome: string;
  adicional: number;
  produtoFinalId: number;
  itens: ReceitaItemDTO[];
}

export interface Produto {
  id: number;
  nome: string;
  precoVenda: number;
}


export interface ItemComanda {
  id?: number;
  comanda: { id: number };
  produto: { id: number; nome?: string };
  quantidade: number;
  precoUnitario: number;
}

// types/ComandaDTO.ts
export interface ComandaDTO {
  id?: number;
  mesaId: number;
  status?: 'ABERTA' | 'FECHADA';
  dataAbertura?: string;
  dataFechamento?: string | null;
}

interface ComandaResumo {
  id: number;
  comandaId: number;
  total: number;
  dataFechamento: string;
  nomeCliente: string;
  observacoes: string;
}

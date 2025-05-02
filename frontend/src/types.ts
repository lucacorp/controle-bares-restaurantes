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
}

// types/MovimentacaoEstoqueDTO.ts
export interface MovimentacaoEstoqueDTO {
  produtoId: number;
  quantidade: number;
  tipo: "ENTRADA" | "SAIDA";
  observacao: string;
  dataMovimentacao?: string; // opcional
}

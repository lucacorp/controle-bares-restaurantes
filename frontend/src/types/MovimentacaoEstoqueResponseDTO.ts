// types/MovimentacaoEstoqueResponseDTO.ts
export interface MovimentacaoEstoqueResponseDTO {
  id: number;
  quantidade: number;
  tipo: "ENTRADA" | "SAIDA";
  observacao?: string;
  dataMovimentacao: string;
}

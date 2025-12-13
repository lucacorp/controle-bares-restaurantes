export interface ComandaResumo {
  id: number;
  dataFechamento: string;
  total: number;
  nomeCliente: string;
  observacoes: string;
  statusSat?: string;
  statusFiscal?: string;
}

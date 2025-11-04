// src/components/MovimentacoesPage.tsx
import { useEffect, useState } from "react";
import { format } from "date-fns";
import { ptBR } from "date-fns/locale";
import api from "../services/api";
import * as XLSX from "xlsx";
// REMOVA ESTA LINHA: import { useOutletContext } from 'react-router-dom'; // Se estiver presente

interface MovimentacaoEstoque {
  id: number;
  dataMovimentacao: string;
  tipo: "ENTRADA" | "SAIDA";
  quantidade: number;
  observacao: string;
}

// ADICIONE a interface para as props
interface MovimentacoesPageProps {
  productId: number;
}

// ATUALIZE a assinatura da função para receber 'productId' como prop
export default function MovimentacoesPage({ productId }: MovimentacoesPageProps) {
  // REMOVA ESTA LINHA: const { productId } = useOutletContext<OutletContext>(); // Se estiver presente

  const [movimentacoes, setMovimentacoes] = useState<MovimentacaoEstoque[]>([]);
  const [filtroTipo, setFiltroTipo] = useState<string>("TODOS");
  const [filtroInicio, setFiltroInicio] = useState<string>("");
  const [filtroFim, setFiltroFim] = useState<string>("");

  useEffect(() => {
    fetchMovimentacoes();
  }, [productId]); // Adicione productId como dependência do useEffect

  const fetchMovimentacoes = async () => {
    try {
      // Confirme o endpoint com '/api'
      const res = await api.get(`/movimentacoes-estoque/${productId}`); // Corrigido para /api/movimentacoes-estoque/{productId}
      setMovimentacoes(res.data);
    } catch (error) {
      console.error("Erro ao buscar movimentações:", error);
    }
  };

  // ... (restante do seu código, exportarExcel, aplicarFiltros, e o JSX de renderização) ...
  const exportarExcel = () => {
    const dadosFiltrados = aplicarFiltros();
    const planilha = XLSX.utils.json_to_sheet(
      dadosFiltrados.map((m) => ({
        Data: format(new Date(m.dataMovimentacao), "dd/MM/yyyy HH:mm", { locale: ptBR }),
        Tipo: m.tipo,
        Quantidade: m.quantidade,
        Observacao: m.observacao,
      }))
    );
    const wb = XLSX.utils.book_new();
    XLSX.utils.book_append_sheet(wb, planilha, "Movimentacoes");
    XLSX.writeFile(wb, `movimentacoes_produto_${productId}.xlsx`);
  };

  const aplicarFiltros = (): MovimentacaoEstoque[] => {
    return movimentacoes.filter((m) => {
      const data = new Date(m.dataMovimentacao);
      const inicio = filtroInicio ? new Date(filtroInicio) : null;
      const fim = filtroFim ? new Date(filtroFim) : null;
      const tipoOK = filtroTipo === "TODOS" || m.tipo === filtroTipo;
      const dataOK =
        (!inicio || data >= inicio) &&
        (!fim || data <= new Date(fim.getTime() + 24 * 60 * 60 * 1000));
      return tipoOK && dataOK;
    });
  };

  return (
    <div className="space-y-4">
      <div className="flex flex-wrap gap-2 items-end">
        <div>
          <label>Início:</label>
          <input
            type="date"
            value={filtroInicio}
            onChange={(e) => setFiltroInicio(e.target.value)}
            className="border rounded p-1"
          />
        </div>
        <div>
          <label>Fim:</label>
          <input
            type="date"
            value={filtroFim}
            onChange={(e) => setFiltroFim(e.target.value)}
            className="border rounded p-1"
          />
        </div>
        <div>
          <label>Tipo:</label>
          <select
            value={filtroTipo}
            onChange={(e) => setFiltroTipo(e.target.value)}
            className="border rounded p-1"
          >
            <option value="TODOS">Todos</option>
            <option value="ENTRADA">Entrada</option>
            <option value="SAIDA">Saída</option>
          </select>
        </div>
        <button
          onClick={exportarExcel}
          className="bg-green-600 text-white px-4 py-2 rounded ml-auto"
        >
          Exportar Excel
        </button>
      </div>

      <div className="overflow-x-auto">
        <table className="w-full border border-gray-300 text-sm">
          <thead className="bg-gray-100">
            <tr>
              <th className="p-2 border">Data</th>
              <th className="p-2 border">Tipo</th>
              <th className="p-2 border">Quantidade</th>
              <th className="p-2 border">Observação</th>
            </tr>
          </thead>
          <tbody>
            {aplicarFiltros().map((m) => (
              <tr key={m.id}>
                <td className="p-2 border">
                  {format(new Date(m.dataMovimentacao), "dd/MM/yyyy HH:mm", { locale: ptBR })}
                </td>
                <td className="p-2 border">{m.tipo}</td>
                <td className="p-2 border">{m.quantidade}</td>
                <td className="p-2 border">{m.observacao}</td>
              </tr>
            ))}
            {aplicarFiltros().length === 0 && (
              <tr>
                <td colSpan={4} className="text-center p-4 text-gray-500">
                  Nenhuma movimentação encontrada.
                </td>
              </tr>
            )}
          </tbody>
        </table>
      </div>
    </div>
  );
}
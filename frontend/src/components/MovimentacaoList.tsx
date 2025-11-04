// src/components/MovimentacaoList.tsx
import { useEffect, useState } from "react";
import api from "../services/api";
import { MovimentacaoEstoqueResponseDTO } from "../types/MovimentacaoEstoqueResponseDTO";
// Remova: import { useOutletContext } from 'react-router-dom';

// Volte a receber produtoId como prop
export default function MovimentacaoList({ productId }: { productId: number }) {
  // Remova: const { productId } = useOutletContext<OutletContext>();
  // Remova: interface OutletContext { productId: number; }

  const [movimentacoes, setMovimentacoes] = useState<MovimentacaoEstoqueResponseDTO[]>([]);
  const [loading, setLoading] = useState<boolean>(true);
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    if (!productId || isNaN(productId)) {
        setError("ID do produto inválido ou não fornecido.");
        setLoading(false);
        return;
    }

    setLoading(true);
    setError(null);
    api
      .get(`/movimentacoes-estoque/${productId}`) // Use o productId da prop
      .then((response) => {
        setMovimentacoes(response.data);
      })
      .catch((err) => {
        console.error("Erro ao carregar movimentações:", err);
        setError("Erro ao carregar movimentações. Tente novamente.");
      })
      .finally(() => {
        setLoading(false);
      });
  }, [productId]);

  if (loading) {
    return <div className="text-center py-4">Carregando movimentações...</div>;
  }

  if (error) {
    return <div className="text-center py-4 text-red-600">{error}</div>;
  }

  if (movimentacoes.length === 0) {
    return <div className="text-center py-4 text-gray-500">Nenhuma movimentação encontrada para este produto.</div>;
  }

  return (
    <div className="overflow-x-auto">
      <h3 className="text-xl font-semibold mb-4">Histórico de Movimentações</h3>
      <table className="min-w-full bg-white border border-gray-200 rounded-lg shadow-sm">
        <thead className="bg-gray-100">
          <tr>
            <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
              ID Mov.
            </th>
            <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
              Produto ID
            </th>
            <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
              Quantidade
            </th>
            <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
              Tipo
            </th>
            <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
              Observação
            </th>
            <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
              Data/Hora
            </th>
          </tr>
        </thead>
        <tbody className="divide-y divide-gray-200">
          {movimentacoes.map((mov) => (
            <tr key={mov.id}>
              <td className="px-6 py-4 whitespace-nowrap text-sm text-gray-900">
                {mov.id}
              </td>
              <td className="px-6 py-4 whitespace-nowrap text-sm text-gray-900">
                {mov.produtoId}
              </td>
              <td className="px-6 py-4 whitespace-nowrap text-sm text-gray-900">
                {mov.quantidade}
              </td>
              <td className="px-6 py-4 whitespace-nowrap text-sm">
                <span
                  className={`px-2 inline-flex text-xs leading-5 font-semibold rounded-full ${
                    mov.tipo === "ENTRADA"
                      ? "bg-green-100 text-green-800"
                      : "bg-red-100 text-red-800"
                  }`}
                >
                  {mov.tipo}
                </span>
              </td>
              <td className="px-6 py-4 whitespace-nowrap text-sm text-gray-900">
                {mov.observacao || "-"}
              </td>
              <td className="px-6 py-4 whitespace-nowrap text-sm text-gray-900">
                {new Date(mov.dataHora).toLocaleString()}
              </td>
            </tr>
          ))}
        </tbody>
      </table>
    </div>
  );
}
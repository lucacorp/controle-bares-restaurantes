// src/components/ProductList.tsx

import { useEffect, useState } from 'react';
import api from '../services/api';
import { toast } from 'react-toastify';

export default function ProductList({
  onEdit,
  refreshTrigger
}: {
  onEdit: (id: number) => void;
  refreshTrigger: number;
}) {
  const [produtos, setProdutos] = useState<any[]>([]);

  useEffect(() => {
    const fetchProdutos = async () => {
      try {
        const response = await api.get('/produtos');
        setProdutos(response.data);
      } catch (error) {
        console.error('Erro ao carregar produtos:', error);
        toast.error('Erro ao carregar produtos');
      }
    };

    fetchProdutos();
  }, [refreshTrigger]);

  const handleDelete = async (id: number) => {
    if (confirm('Tem certeza que deseja excluir este produto?')) {
      try {
        await api.delete(`/produtos/${id}`);
        toast.success('Produto excluído com sucesso!');
        // atualiza lista após excluir:
        setProdutos((prev) => prev.filter((p) => p.id !== id));
      } catch (error) {
        console.error('Erro ao excluir produto:', error);
        toast.error('Erro ao excluir produto');
      }
    }
  };

  return (
    <div>
      <h2 className="text-lg font-bold mb-4">Lista de Produtos</h2>
      <div className="overflow-x-auto">
        <table className="w-full text-sm text-left border border-gray-300">
          <thead className="bg-gray-100 text-gray-700">
            <tr>
              <th className="p-2 border">Código</th>
              <th className="p-2 border">Nome</th>
              <th className="p-2 border">Grupo</th>
              <th className="p-2 border">Unidade</th>
              <th className="p-2 border">Estoque</th>
              <th className="p-2 border text-center">Ações</th>
            </tr>
          </thead>
          <tbody>
            {produtos.map((produto) => (
              <tr
                key={produto.id}
                className="hover:bg-gray-100 cursor-pointer"
                onDoubleClick={() => onEdit(produto.id)}
              >
                <td className="p-2 border">{produto.codigo}</td>
                <td className="p-2 border">{produto.nome}</td>
                <td className="p-2 border">{produto.grupo}</td>
                <td className="p-2 border">{produto.unidade}</td>
                <td className="p-2 border text-center">{produto.estoqueAtual ?? 0}</td>
                <td className="p-2 border text-center">
                  <button
                    onClick={(e) => {
                      e.stopPropagation(); // evita disparar onDoubleClick
                      handleDelete(produto.id);
                    }}
                    className="bg-red-500 text-white px-2 py-1 rounded hover:bg-red-600 text-sm"
                  >
                    Excluir
                  </button>
                </td>
              </tr>
            ))}
            {produtos.length === 0 && (
              <tr>
                <td colSpan={6} className="p-4 text-center text-gray-500">
                  Nenhum produto encontrado.
                </td>
              </tr>
            )}
          </tbody>
        </table>
      </div>
    </div>
  );
}

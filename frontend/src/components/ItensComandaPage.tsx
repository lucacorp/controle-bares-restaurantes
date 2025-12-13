// src/pages/ItensComandaPage.tsx
import React, { useEffect, useState } from "react";
import { useNavigate, useParams, useSearchParams } from "react-router-dom";
import api from "../services/api";

interface Produto {
  id: number;
  nome: string;
  precoVenda: number;
}

interface ItemComanda {
  id: number;
  quantidade: number;
  produtoId?: number;
  produtoNome?: string;
  precoVenda?: number;
  total?: number;
  status?: string;
}

interface Comanda {
  id: number;
  ativa?: boolean;
  status?: string;
  itens: ItemComanda[];
  numeroMesa?: number;
}

export default function ItensComandaPage() {
  const { id } = useParams<{ id: string }>();
  const [searchParams] = useSearchParams();
  const mesaId = searchParams.get("mesaId");
  const navigate = useNavigate();

  const [comanda, setComanda] = useState<Comanda | null>(null);
  const [produtos, setProdutos] = useState<Produto[]>([]);
  const [produtoSelecionado, setProdutoSelecionado] = useState<number | "">("");
  const [quantidade, setQuantidade] = useState<number>(1);
  const [mensagem, setMensagem] = useState<string>("");

  const comandaId = Number(id);

  const carregarComanda = async () => {
    if (!id || isNaN(comandaId)) {
      setMensagem("ID da comanda inválido.");
      return;
    }
    try {
      const { data } = await api.get(`/comandas/${id}`);
      if (!data.itens) data.itens = [];
      setComanda(data);
      setMensagem("");
    } catch (e: any) {
      console.error("Erro ao carregar comanda:", e);
      setMensagem(e?.response?.data?.message || "Erro ao carregar comanda.");
    }
  };

  const carregarProdutos = async () => {
    try {
      const { data } = await api.get("/produtos");
      setProdutos(Array.isArray(data) ? data : []);
    } catch (e) {
      console.error("Erro ao carregar produtos:", e);
    }
  };

  useEffect(() => {
    carregarComanda();
    carregarProdutos();
  }, [id]);

  const adicionarItem = async () => {
    if (!produtoSelecionado || quantidade <= 0) {
      setMensagem("Selecione um produto e quantidade válida.");
      return;
    }
    try {
      await api.post("/itens-comanda", {
        comanda: { id: comanda?.id },
        produto: { id: produtoSelecionado },
        quantidade,
      });
      setMensagem("Item adicionado com sucesso!");
      setQuantidade(1);
      setProdutoSelecionado("");
      carregarComanda();
    } catch (e: any) {
      console.error("Erro ao adicionar item:", e);
      setMensagem(e?.response?.data?.message || "Erro ao adicionar item.");
    }
  };

  const removerItem = async (itemId: number) => {
    if (!confirm("Remover item?")) return;
    try {
      await api.delete(`/itens-comanda/${itemId}`);
      setMensagem("Item removido com sucesso!");
      carregarComanda();
    } catch (e: any) {
      console.error("Erro ao remover item:", e);
      setMensagem(e?.response?.data?.message || "Erro ao remover item.");
    }
  };

  const irParaFechamento = () => {
    // Navega para a página de fechamento
    const queryParam = mesaId ? `?mesaId=${mesaId}` : '';
    navigate(`/comandas/${id}/fechar${queryParam}`);
  };

  const excluirComanda = async () => {
    if (!confirm("Deseja realmente excluir a comanda?")) return;
    try {
      await api.delete(`/comandas/${id}`);
      setMensagem("Comanda excluída com sucesso!");
      if (mesaId) navigate(`/mesa/${mesaId}`);
      else setComanda(null);
    } catch (e: any) {
      console.error("Erro ao excluir comanda:", e);
      setMensagem(e?.response?.data?.message || "Erro ao excluir comanda.");
    }
  };

  const calcularTotal = () =>
    comanda?.itens?.reduce((sum, i) => sum + (i.total ?? 0), 0) ?? 0;

  if (!comanda) return <div className="p-4">Carregando comanda...</div>;

  return (
    <div className="p-4 max-w-4xl mx-auto">
      <div className="flex justify-between items-center mb-4">
        <h2 className="text-2xl font-bold">Itens da Comanda {comanda.id}</h2>
        <div className="text-sm text-gray-600">
          Mesa: {comanda.numeroMesa ?? mesaId}
        </div>
      </div>

      {mensagem && <div className="mb-3 text-blue-600">{mensagem}</div>}

      {comanda.ativa && (
        <div className="flex items-center gap-2 mb-4">
          <select
            value={produtoSelecionado}
            onChange={(e) => setProdutoSelecionado(Number(e.target.value))}
            className="border rounded p-2"
          >
            <option value="">Selecione um produto</option>
            {produtos.map((p) => (
              <option key={p.id} value={p.id}>
                {p.nome} — R$ {(p.precoVenda ?? 0).toFixed(2)}
              </option>
            ))}
          </select>

          <input
            type="number"
            min={1}
            value={quantidade}
            onChange={(e) => setQuantidade(Number(e.target.value))}
            className="border rounded p-2 w-20"
          />

          <button
            onClick={adicionarItem}
            className="bg-green-600 text-white px-4 py-2 rounded"
          >
            Adicionar
          </button>
        </div>
      )}

      <table className="w-full border-collapse mb-4">
        <thead>
          <tr className="border-b font-semibold">
            <th className="text-left p-2">Descrição</th>
            <th className="text-center p-2">Qtd</th>
            <th className="text-right p-2">Preço</th>
            <th className="text-right p-2">Subtotal</th>
            {comanda.ativa && <th className="p-2 text-center">Ação</th>}
          </tr>
        </thead>
        <tbody>
          {comanda.itens?.length > 0 ? (
            comanda.itens.map((item) => (
              <tr key={item.id} className="border-b">
                <td className="p-2">{item.produtoNome ?? "—"}</td>
                <td className="text-center p-2">{item.quantidade ?? 0}</td>
                <td className="text-right p-2">
                  R$ {(item.precoVenda ?? 0).toFixed(2)}
                </td>
                <td className="text-right p-2">
                  R$ {(item.total ?? 0).toFixed(2)}
                </td>
                {comanda.ativa && (
                  <td className="text-center p-2">
                    <button
                      onClick={() => removerItem(item.id)}
                      className="bg-red-500 text-white px-3 py-1 rounded"
                    >
                      Remover
                    </button>
                  </td>
                )}
              </tr>
            ))
          ) : (
            <tr>
              <td
                colSpan={comanda.ativa ? 5 : 4}
                className="text-center p-3 text-gray-500"
              >
                Nenhum item.
              </td>
            </tr>
          )}
        </tbody>

        <tfoot>
          <tr className="font-bold">
            <td colSpan={3} className="text-right p-2">
              Total:
            </td>
            <td className="text-right p-2">
              R$ {calcularTotal().toFixed(2)}
            </td>
            {comanda.ativa && <td />}
          </tr>
        </tfoot>
      </table>

      <div className="flex gap-3">
        {comanda.ativa ? (
          <>
            <button
              onClick={irParaFechamento}
              className="bg-green-600 text-white px-6 py-3 rounded-lg font-medium hover:bg-green-700 transition-colors"
            >
              💰 Fechar e Pagar Comanda
            </button>
            <button
              onClick={excluirComanda}
              className="bg-red-600 text-white px-4 py-2 rounded hover:bg-red-700 transition-colors"
            >
              🗑️ Excluir Comanda
            </button>
          </>
        ) : (
          <button
            onClick={() => navigate(-1)}
            className="px-3 py-2 border rounded"
          >
            Voltar
          </button>
        )}
      </div>
    </div>
  );
}

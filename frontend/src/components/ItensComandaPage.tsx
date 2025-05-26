import { useEffect, useState } from "react";
import api from "../services/api";
import { ItemComanda } from "@/types/ItemComanda";
import { Produto } from "@/types/Produto";
import { Trash2 } from "lucide-react";

interface Props {
  comandaId: number;
}

export default function ItensComandaPage({ comandaId }: Props) {
  const [itens, setItens] = useState<ItemComanda[]>([]);
  const [total, setTotal] = useState<string>("0.00");
  const [produtos, setProdutos] = useState<Produto[]>([]);
  const [produtoId, setProdutoId] = useState<number>(0);
  const [quantidade, setQuantidade] = useState<number>(1);
  const [mensagem, setMensagem] = useState<string>("");

  const loadItens = async () => {
    try {
      const resp = await api.get(`/itens-comanda/${comandaId}`);
      const itensData = Array.isArray(resp.data) ? resp.data : [];
      setItens(itensData);
    } catch (err: any) {
      console.error("Erro ao carregar itens:", err);
      setMensagem("Erro ao carregar itens.");
    }
  };

  const loadProdutos = async () => {
    try {
      const resp = await api.get('/produtos');
      setProdutos(Array.isArray(resp.data) ? resp.data : []);
    } catch (err: any) {
      console.error("Erro ao carregar produtos:", err);
      setMensagem("Erro ao carregar produtos.");
    }
  };

  const adicionarItem = async () => {
    if (!produtoId) {
      setMensagem("Selecione um produto válido.");
      return;
    }

    const produto = produtos.find(p => p.id === produtoId);
    const precoUnitario = produto?.precoVenda ?? produto?.preco ?? 0;

    try {
      await api.post('/itens-comanda', {
        comandaId,
        produtoId,
        quantidade,
        precoUnitario
      });
      setMensagem("✅ Item adicionado com sucesso.");
      setQuantidade(1);
      await loadItens();
    } catch (err: any) {
      console.error("Erro ao adicionar item:", err);
      const backendMsg = err.response?.data?.message || "Erro ao adicionar item.";
      setMensagem(backendMsg);
    }
  };

  const removerItem = async (itemId: number) => {
    try {
      await api.delete(`/itens-comanda/${itemId}`);
      setMensagem("🗑️ Item removido.");
      await loadItens();
    } catch (err: any) {
      console.error("Erro ao remover item:", err);
      setMensagem("Erro ao remover item.");
    }
  };

  const finalizarComanda = async () => {
    try {
      await api.post(`/comandas/${comandaId}/fechar`);
      setMensagem("✅ Comanda finalizada com sucesso!");
      await loadItens();
    } catch (err: any) {
      console.error("Erro ao finalizar comanda:", err);
      const backendMsg = err.response?.data?.message || "Erro ao finalizar comanda.";
      setMensagem(backendMsg);
    }
  };

  // Atualiza o total toda vez que itens mudarem
  useEffect(() => {
    const newTotal = itens
      .reduce((acc, item) => acc + item.precoUnitario * item.quantidade, 0)
      .toFixed(2);
    setTotal(newTotal);
  }, [itens]);

  useEffect(() => {
    loadItens();
    loadProdutos();
  }, [comandaId]);

  return (
    <div className="p-6 bg-white rounded shadow-md">
      <h2 className="text-2xl font-bold mb-4">Itens da Comanda #{comandaId}</h2>

      {mensagem && (
        <div className={`mb-4 p-3 rounded ${mensagem.includes("Erro") ? "bg-red-100 text-red-700" : "bg-green-100 text-green-700"}`}>
          {mensagem}
        </div>
      )}

      <div className="flex flex-wrap gap-2 mb-6">
        <select
          value={produtoId}
          onChange={e => setProdutoId(Number(e.target.value))}
          className="border p-2 rounded w-60"
        >
          <option value={0}>Selecione um produto</option>
          {produtos.map(p => (
            <option key={p.id} value={p.id}>
              {p.nome}
            </option>
          ))}
        </select>

        <input
          type="number"
          min={1}
          value={quantidade}
          onChange={e => setQuantidade(Number(e.target.value))}
          className="border p-2 w-24 rounded"
        />

        <button
          onClick={adicionarItem}
          className="bg-green-600 text-white px-4 py-2 rounded hover:bg-green-700 transition"
        >
          Adicionar
        </button>
      </div>

      <table className="w-full border-collapse">
        <thead>
          <tr className="bg-gray-100">
            <th className="p-2 border">Descrição</th>
            <th className="p-2 border">Qtd</th>
            <th className="p-2 border">Preço Unitário</th>
            <th className="p-2 border">Subtotal</th>
            <th className="p-2 border">Ação</th>
          </tr>
        </thead>
        <tbody>
          {itens.map(item => (
            <tr key={item.id} className="hover:bg-gray-50">
              <td className="p-2 border">{item.produtoDescricao}</td>
              <td className="p-2 border">{item.quantidade}</td>
              <td className="p-2 border">R$ {item.precoUnitario.toFixed(2)}</td>
              <td className="p-2 border">R$ {(item.precoUnitario * item.quantidade).toFixed(2)}</td>
              <td className="p-2 border text-center">
                <button
                  onClick={() => removerItem(item.id)}
                  className="text-red-500 hover:text-red-700"
                  title="Remover item"
                >
                  <Trash2 size={16} />
                </button>
              </td>
            </tr>
          ))}
        </tbody>
      </table>

      <div className="text-right mt-6 text-xl font-bold">
        Total: R$ {total}
      </div>

      <div className="flex justify-end mt-4">
        <button
          onClick={finalizarComanda}
          className="bg-purple-600 text-white px-6 py-2 rounded hover:bg-purple-700 transition"
        >
          Finalizar Comanda
        </button>
      </div>
    </div>
  );
}

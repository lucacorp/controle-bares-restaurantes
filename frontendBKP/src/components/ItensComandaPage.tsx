import { useEffect, useState } from "react";
import { useParams } from "react-router-dom";
import api from "../services/api";

interface Produto {
  id: number;
  nome: string;
  precoVenda: number;
}

interface ItemComanda {
  id: number;
  quantidade: number;
  produto: Produto;
}

interface Comanda {
  id: number;
  ativa: boolean;
  itens: ItemComanda[];
}

export default function ItensComandaPage() {
  const { id } = useParams<{ id: string }>();
  const [comanda, setComanda] = useState<Comanda | null>(null);
  const [produtos, setProdutos] = useState<Produto[]>([]);
  const [produtoSelecionado, setProdutoSelecionado] = useState<number | "">("");
  const [quantidade, setQuantidade] = useState<number>(1);
  const [mensagem, setMensagem] = useState<string>("");

  // 🔹 Carrega comanda e produtos
  useEffect(() => {
    carregarComanda();
    carregarProdutos();
  }, [id]);

  const carregarComanda = async () => {
    try {
      const { data } = await api.get(`/comandas/${id}`);
      setComanda(data);
      setMensagem("");
    } catch (error: any) {
      console.error("Erro ao carregar comanda:", error);
      setMensagem("Erro ao carregar comanda.");
    }
  };

  const carregarProdutos = async () => {
    try {
      const { data } = await api.get("/produtos");
      setProdutos(data);
    } catch (error) {
      console.error("Erro ao carregar produtos:", error);
    }
  };

  // 🔹 Adiciona item à comanda
  const adicionarItem = async () => {
    if (!produtoSelecionado || quantidade <= 0) {
      setMensagem("Selecione um produto e uma quantidade válida.");
      return;
    }

    try {
      await api.post("/itens-comanda", {
        comanda: { id: comanda?.id },
        produto: { id: produtoSelecionado },
        quantidade,
      });
      setMensagem("Item adicionado com sucesso!");
      setProdutoSelecionado("");
      setQuantidade(1);
      carregarComanda();
    } catch (error: any) {
      console.error("Erro ao adicionar item:", error);
      setMensagem("Erro ao adicionar item à comanda.");
    }
  };

  // 🔹 Excluir item da comanda
  const removerItem = async (itemId: number) => {
    try {
      await api.delete(`/itens-comanda/${itemId}`);
      setMensagem("Item removido com sucesso!");
      carregarComanda();
    } catch (error) {
      console.error("Erro ao remover item:", error);
      setMensagem("Erro ao remover item.");
    }
  };

  // 🔹 Fechar a comanda
  const fecharComanda = async () => {
    try {
      await api.put(`/comandas/${id}/fechar`);
      setMensagem("Comanda fechada com sucesso!");
      carregarComanda();
    } catch (error) {
      console.error("Erro ao fechar comanda:", error);
      setMensagem("Erro ao fechar comanda.");
    }
  };

  // 🔹 Excluir a comanda
  const excluirComanda = async () => {
    try {
      await api.delete(`/comandas/${id}`);
      setMensagem("Comanda excluída com sucesso!");
      setComanda(null);
    } catch (error) {
      console.error("Erro ao excluir comanda:", error);
      setMensagem("Erro ao excluir comanda.");
    }
  };

  // 🔹 Calcula o total
  const calcularTotal = () =>
    comanda?.itens?.reduce(
      (soma, item) =>
        soma + (item.produto?.precoVenda || 0) * (item.quantidade || 0),
      0
    ) || 0;

  if (!comanda) return <div>Carregando comanda...</div>;

  return (
    <div className="p-4">
      <h2 className="text-2xl font-bold mb-4">Itens da Comanda {comanda.id}</h2>

      {mensagem && <div className="mb-3 text-blue-600">{mensagem}</div>}

      {/* Formulário de adição */}
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
                {p.nome} — R$ {p.precoVenda.toFixed(2)}
              </option>
            ))}
          </select>

          <input
            type="number"
            value={quantidade}
            onChange={(e) => setQuantidade(Number(e.target.value))}
            className="border rounded p-2 w-16"
            min={1}
          />

          <button
            onClick={adicionarItem}
            className="bg-green-600 text-white px-4 py-2 rounded"
          >
            Adicionar
          </button>
        </div>
      )}

      {/* Tabela de itens */}
      <table className="w-full border-collapse">
        <thead>
          <tr className="border-b font-semibold">
            <th className="text-left p-2">Descrição</th>
            <th>Qtd</th>
            <th>Preço</th>
            <th>Subtotal</th>
            {comanda.ativa && <th>Ação</th>}
          </tr>
        </thead>
        <tbody>
          {comanda.itens?.length > 0 ? (
            comanda.itens.map((item) => (
              <tr key={item.id} className="border-b">
                <td className="p-2">{item.produto?.nome || "—"}</td>
                <td className="text-center">{item.quantidade}</td>
                <td className="text-center">
                  R$ {(item.produto?.precoVenda || 0).toFixed(2)}
                </td>
                <td className="text-center">
                  R${" "}
                  {((item.produto?.precoVenda || 0) * item.quantidade).toFixed(2)}
                </td>
                {comanda.ativa && (
                  <td className="text-center">
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
              <td colSpan={5} className="text-center p-3">
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
            <td colSpan={2}>R$ {calcularTotal().toFixed(2)}</td>
          </tr>
        </tfoot>
      </table>

      {/* Botões da comanda */}
      {comanda.ativa && (
        <div className="mt-4 flex gap-3">
          <button
            onClick={fecharComanda}
            className="bg-blue-600 text-white px-4 py-2 rounded"
          >
            Fechar Comanda
          </button>
          <button
            onClick={excluirComanda}
            className="bg-red-600 text-white px-4 py-2 rounded"
          >
            Excluir Comanda
          </button>
        </div>
      )}
    </div>
  );
}

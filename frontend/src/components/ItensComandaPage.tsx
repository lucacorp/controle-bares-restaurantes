import { useEffect, useState } from "react";
import { useParams, useSearchParams } from "react-router-dom";
import api from "../services/api";
import { Trash2 } from "lucide-react";

interface ItemComanda {
  id: number;
  nomeItem: string;
  precoUnitario?: number | null;
  quantidade?: number | null;
}

interface Produto {
  id: number;
  nome: string;
  preco?: number;
  precoVenda?: number;
  fabricacaoPropria: boolean;
}

export default function ItensComandaPage() {
  const { id } = useParams<{ id: string }>();
  const [searchParams] = useSearchParams();
  const mesaId = searchParams.get("mesaId");

  const [itens, setItens] = useState<ItemComanda[]>([]);
  const [produtos, setProdutos] = useState<Produto[]>([]);
  const [produtoId, setProdutoId] = useState(0);
  const [quantidade, setQuantidade] = useState(1);
  const [mensagem, setMensagem] = useState("");

  const comandaId = Number(id);

  const loadItens = async () => {
    if (!id || isNaN(comandaId)) {
      setMensagem("ID da comanda inválido. Verifique o URL.");
      return;
    }
    try {
      const { data } = await api.get(`/itens-comanda/comanda/${id}`);
      setItens(Array.isArray(data) ? data : []);
      setMensagem("");
    } catch (e: any) {
      setMensagem(e.response?.data?.message || "Erro ao carregar itens.");
    }
  };

  const loadProdutos = async () => {
    try {
      const { data } = await api.get("/produtos");
      setProdutos(Array.isArray(data) ? data : []);
    } catch {
      setMensagem("Erro ao carregar produtos.");
    }
  };

  const adicionarItem = async () => {
    if (!id || isNaN(comandaId) || !produtoId) {
      setMensagem("Selecione um produto válido.");
      return;
    }

    try {
      await api.post("/itens-comanda", {
        comandaId,
        produtoId,
        quantidade,
      });
      setMensagem("Item adicionado com sucesso.");
      setQuantidade(1);
      loadItens();
    } catch (e: any) {
      setMensagem(e.response?.data?.message || "Erro ao adicionar item.");
    }
  };

  const removerItem = async (itemId: number) => {
    try {
      await api.delete(`/itens-comanda/${itemId}`);
      setMensagem("Item removido com sucesso.");
      loadItens();
    } catch (e: any) {
      setMensagem(e.response?.data?.message || "Erro ao remover item.");
    }
  };

  const calcularTotal = () =>
    itens
      .reduce(
        (sum, i) =>
          sum + (i.precoUnitario ?? 0) * (i.quantidade ?? 0),
        0
      )
      .toFixed(2);

  return (
    <div className="p-6">
      <h2 className="text-xl font-semibold mb-2">Itens da Comanda {id}</h2>

      {mensagem && <p className="mb-2 text-red-600">{mensagem}</p>}

      <div className="flex flex-wrap gap-2 mb-4">
        <select
          value={produtoId}
          onChange={(e) => setProdutoId(Number(e.target.value))}
          className="border p-2 rounded min-w-[200px]"
        >
          <option value={0}>Selecione um produto</option>
          {produtos.map((p) => (
            <option key={p.id} value={p.id}>
              {p.nome} {p.fabricacaoPropria ? "⚙️" : ""}
            </option>
          ))}
        </select>

        <input
          type="number"
          min={1}
          value={quantidade}
          onChange={(e) => setQuantidade(Number(e.target.value))}
          className="border p-2 w-24 rounded"
        />

        <button
          onClick={adicionarItem}
          className="bg-green-600 text-white px-4 rounded"
        >
          Adicionar
        </button>
      </div>

      <table className="w-full border-collapse text-sm">
        <thead>
          <tr className="bg-gray-100">
            <th className="border p-2 text-left">Descrição</th>
            <th className="border p-2">Qtd</th>
            <th className="border p-2">Preço</th>
            <th className="border p-2">Subtotal</th>
            <th className="border p-2">Ação</th>
          </tr>
        </thead>
        <tbody>
          {itens.map((it) => (
            <tr key={it.id} className="hover:bg-gray-50">
              <td className="border p-2">{it.nomeItem ?? "Produto"}</td>
              <td className="border p-2 text-center">{it.quantidade ?? 0}</td>
              <td className="border p-2 text-right">
                {(it.precoUnitario ?? 0).toFixed(2)}
              </td>
              <td className="border p-2 text-right">
                {((it.precoUnitario ?? 0) * (it.quantidade ?? 0)).toFixed(2)}
              </td>
              <td className="border p-2 text-center">
                <button
                  onClick={() => removerItem(it.id)}
                  className="text-red-600 hover:text-red-800"
                >
                  <Trash2 size={14} />
                </button>
              </td>
            </tr>
          ))}
          {itens.length === 0 && (
            <tr>
              <td
                colSpan={5}
                className="border p-2 text-center text-gray-500"
              >
                Nenhum item.
              </td>
            </tr>
          )}
        </tbody>
      </table>

      <div className="text-right mt-2 font-bold">
        Total: R$ {calcularTotal()}
      </div>
    </div>
  );
}

import { useEffect, useState } from "react";
import axios from "axios";
import { ItemComanda } from "@/types/ItemComanda";
import { Produto } from "@/types/Produto";
// index.tsx ou main.tsx

interface Props {
  comandaId: number;
}

export default function ItensComandaPage({ comandaId }: Props) {
  const [itens, setItens] = useState<ItemComanda[]>([]);
  const [produtos, setProdutos] = useState<Produto[]>([]);
  const [produtoId, setProdutoId] = useState<number>(0);
  const [quantidade, setQuantidade] = useState<number>(1);

  const loadItens = async () => {
    const resp = await axios.get(`/api/itens-comanda/${comandaId}`);
    setItens(resp.data);
  };

  const loadProdutos = async () => {
    const resp = await axios.get("/api/produtos");
    setProdutos(resp.data);
  };

  const adicionarItem = async () => {
    const produto = produtos.find(p => p.id === produtoId);
    if (!produto) return;

    await axios.post("/api/itens-comanda", {
      comanda: { id: comandaId },
      produto: { id: produtoId },
      quantidade,
      precoUnitario: produto.precoVenda // ou preco, conforme sua entidade
    });

    setQuantidade(1);
    loadItens();
  };

  const calcularTotal = () => {
    return itens.reduce((acc, item) => acc + item.precoUnitario * item.quantidade, 0).toFixed(2);
  };

  useEffect(() => {
    loadItens();
    loadProdutos();
  }, [comandaId]);

  return (
    <div className="p-4">
      <h2 className="text-xl font-bold mb-4">Itens da Comanda #{comandaId}</h2>

      <div className="flex gap-2 mb-4">
        <select
          value={produtoId}
          onChange={e => setProdutoId(Number(e.target.value))}
          className="border p-2 rounded"
        >
          <option value={0}>Selecione um produto</option>
          {produtos.map(p => (
            <option key={p.id} value={p.id}>{p.nome}</option>
          ))}
        </select>

        <input
          type="number"
          min={1}
          value={quantidade}
          onChange={e => setQuantidade(Number(e.target.value))}
          className="border p-2 w-20 rounded"
        />

        <button
          onClick={adicionarItem}
          className="bg-blue-600 text-white px-4 py-2 rounded"
        >
          Adicionar
        </button>
      </div>

      <table className="w-full border">
        <thead>
          <tr className="bg-gray-100">
            <th>Produto</th>
            <th>Quantidade</th>
            <th>Preço Unitário</th>
            <th>Subtotal</th>
          </tr>
        </thead>
        <tbody>
          {itens.map(item => (
            <tr key={item.id}>
              <td>{item.produto?.nome || "Produto"}</td>
              <td>{item.quantidade}</td>
              <td>R$ {item.precoUnitario.toFixed(2)}</td>
              <td>R$ {(item.precoUnitario * item.quantidade).toFixed(2)}</td>
            </tr>
          ))}
        </tbody>
      </table>

      <div className="text-right mt-4 font-bold">
        Total: R$ {calcularTotal()}
      </div>
    </div>
  );
}

// src/pages/garcom/GarcomComandaPage.tsx
import { useEffect, useState, useContext } from "react";
import { useParams } from "react-router-dom";
import axios from "axios";
import { ShoppingCart, Trash2 } from "lucide-react";


interface Produto {
  id: number;
  nome: string;
  precoVenda: number;
}

interface ItemComandaResumoDTO {
  id: number;
  produtoNome: string;
  quantidade: number;
  valorUnitario: number;
  valorTotal: number;
}

interface ComandaResumoDTO {
  mesaNome: string;
  itens: ItemComandaResumoDTO[];
  total: number;
}

const API_BASE = "http://192.168.200.107:8080";

export default function GarcomComandaPage() {
  const { id } = useParams();
  const { usuario, token } = useContext(AuthContext);
  const [comanda, setComanda] = useState<ComandaResumoDTO | null>(null);
  const [produtos, setProdutos] = useState<Produto[]>([]);
  const [carregando, setCarregando] = useState(false);

  const carregarComanda = () => {
    axios.get(`${API_BASE}/comanda/${id}`, {
      headers: { Authorization: `Bearer ${token}` },
    })
      .then(res => setComanda(res.data))
      .catch(err => console.error("Erro ao carregar comanda:", err));
  };

  useEffect(() => {
    carregarComanda();

    axios.get(`${API_BASE}/api/produtos/publicos`, {
      headers: { Authorization: `Bearer ${token}` },
    })
      .then(res => setProdutos(res.data))
      .catch(err => console.error("Erro ao buscar produtos:", err));
  }, [id, token]);

  const adicionarItem = (produtoId: number) => {
    setCarregando(true);
    axios.post(`${API_BASE}/comanda/${id}/itens`, {
      produtoId,
      quantidade: 1,
      garcom: usuario?.nome || "garcom",
    }, {
      headers: { Authorization: `Bearer ${token}` },
    })
      .then(() => carregarComanda())
      .finally(() => setCarregando(false));
  };

  const removerItem = (itemId: number) => {
    if (!confirm("Deseja remover este item da comanda?")) return;
    axios.delete(`${API_BASE}/comanda/${id}/itens/${itemId}`, {
      headers: { Authorization: `Bearer ${token}` },
    })
      .then(() => carregarComanda())
      .catch(err => console.error("Erro ao excluir item:", err));
  };

  if (!comanda) {
    return <div className="p-4 text-center text-gray-600">Carregando comanda...</div>;
  }

  return (
    <div className="p-4 max-w-4xl mx-auto space-y-6">
      <div className="flex items-center gap-2 text-blue-700">
        <ShoppingCart className="w-6 h-6" />
        <h1 className="text-2xl font-semibold">Mesa: {comanda.mesaNome}</h1>
        <span className="ml-auto text-sm text-gray-500">Garçom: {usuario?.nome}</span>
      </div>

      {/* Lista de Produtos */}
      <div>
        <h2 className="font-semibold text-lg mb-2">Adicionar Produto</h2>
        <div className="grid grid-cols-2 sm:grid-cols-4 gap-4">
          {produtos.map((produto) => (
            <button
              key={produto.id}
              onClick={() => adicionarItem(produto.id)}
              className="bg-white p-3 shadow rounded hover:bg-gray-100 flex flex-col items-center"
              disabled={carregando}
            >
              <span className="font-medium text-center">{produto.nome}</span>
              <span className="text-sm text-gray-500">R$ {produto.precoVenda?.toFixed(2)}</span>
              <span className="text-blue-600 text-xs mt-1">Adicionar</span>
            </button>
          ))}
        </div>
      </div>

      {/* Itens da comanda */}
      <div className="bg-white p-4 shadow rounded">
        <h2 className="font-semibold mb-3">Itens da Comanda</h2>
        {comanda.itens.length === 0 ? (
          <p className="text-gray-500">Nenhum item ainda.</p>
        ) : (
          <ul className="divide-y">
            {comanda.itens.map((item) => (
              <li key={item.id} className="py-2 flex justify-between items-center">
                <div>
                  <span className="font-medium">{item.quantidade}x {item.produtoNome}</span>
                  <span className="ml-2 text-sm text-gray-500">R$ {item.valorTotal.toFixed(2)}</span>
                </div>
                <button onClick={() => removerItem(item.id)} className="text-red-500 hover:text-red-700">
                  <Trash2 className="w-4 h-4" />
                </button>
              </li>
            ))}
          </ul>
        )}
        <div className="mt-4 font-bold text-right text-lg">
          Total: R$ {comanda.total.toFixed(2)}
        </div>
      </div>
    </div>
  );
}

import { useEffect, useState } from "react";
import { useParams } from "react-router-dom";
import axios from "axios";
import { ShoppingCart, Check } from "lucide-react";

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

export default function ComandaPublicPage() {
  const { mesaId } = useParams();
  const [comanda, setComanda] = useState<ComandaResumoDTO | null>(null);
  const [produtos, setProdutos] = useState<Produto[]>([]);
  const [adicionado, setAdicionado] = useState<number | null>(null);

  const carregarComanda = () => {
    axios.get(`${API_BASE}/comanda/publica/mesa/${mesaId}`)
      .then(res => setComanda(res.data))
      .catch(err => console.error("Erro ao buscar comanda:", err));
  };

  useEffect(() => {
    carregarComanda();
    axios.get(`${API_BASE}/api/produtos/publicos`)
      .then(res => setProdutos(res.data))
      .catch(err => console.error("Erro ao buscar produtos:", err));

    const interval = setInterval(() => {
      carregarComanda();
    }, 5000); // atualiza a cada 5s

    return () => clearInterval(interval);
  }, [mesaId]);

  const adicionarItemDireto = (produtoId: number) => {
    axios.post(`${API_BASE}/comanda/publica/mesa/${mesaId}/itens`, {
      produtoId,
      quantidade: 1,
    })
      .then(() => {
        carregarComanda();
        setAdicionado(produtoId);
        setTimeout(() => setAdicionado(null), 1000);
      })
      .catch(err => console.error("Erro ao adicionar item:", err));
  };

  if (!comanda) {
    return <div className="p-4 text-center text-gray-600">Carregando comanda...</div>;
  }

  return (
    <div className="p-4 max-w-2xl mx-auto space-y-6">
      <div className="flex items-center gap-2 text-blue-700 mb-2">
        <ShoppingCart className="w-6 h-6" />
        <h1 className="text-xl font-semibold">Comanda da Mesa: {comanda.mesaNome}</h1>
      </div>

      {/* Produtos */}
      <div>
        <h2 className="font-semibold text-lg mb-2">Escolha um produto</h2>
        <div className="grid grid-cols-2 sm:grid-cols-3 gap-4">
          {produtos.map((produto) => (
            <button
              key={produto.id}
              onClick={() => adicionarItemDireto(produto.id)}
              className="bg-white shadow rounded p-3 flex flex-col items-center hover:bg-gray-100 active:scale-95 transition"
            >
              <span className="text-center font-medium">{produto.nome}</span>
              <span className="text-sm text-gray-500">
                R$ {(produto.precoVenda ?? 0).toFixed(2)}
              </span>
              <span className="text-blue-600 text-sm mt-2 flex items-center gap-1">
                {adicionado === produto.id ? <><Check className="w-4 h-4" /> Adicionado!</> : <>Adicionar</>}
              </span>
            </button>
          ))}
        </div>
      </div>

      {/* Itens da Comanda */}
      <div className="bg-white p-4 shadow rounded">
        <h2 className="font-semibold mb-2">Itens da Comanda</h2>
        {comanda.itens.length === 0 ? (
          <p className="text-gray-500">Nenhum item ainda.</p>
        ) : (
          <ul>
            {comanda.itens.map((item) => (
              <li key={item.id} className="border-b py-2">
                {item.quantidade}x {item.produtoNome} — R$ {(item.valorTotal ?? 0).toFixed(2)}
              </li>
            ))}
          </ul>
        )}

        <div className="mt-4 font-bold text-right text-lg">
          Total: R$ {(comanda.total ?? 0).toFixed(2)}
        </div>
      </div>
    </div>
  );
}

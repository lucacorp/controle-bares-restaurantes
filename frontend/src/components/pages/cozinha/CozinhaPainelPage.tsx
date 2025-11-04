import { useEffect, useState } from "react";
import axios from "axios";
import { CheckCircle, Clock } from "lucide-react";
import { useAuth } from "../../../AuthContext";

interface ItemCozinhaDTO {
  id: number;
  produtoNome: string;
  quantidade: number;
  mesaNome: string;
  status: string;
}

const API_BASE = "http://192.168.200.107:8080";

export default function CozinhaPainelPage() {
  const { token } = useAuth();
  console.log("Token atual:", token); // adicione isso

  const [itens, setItens] = useState<ItemCozinhaDTO[]>([]);
  const [erro, setErro] = useState<string | null>(null);

  const headers = {
    headers: {
      Authorization: `Bearer ${token}`,
    },
  };

  const carregarItens = () => {
    if (!token) {
      setErro("Usuário não autenticado.");
      return;
    }

    axios
      .get(`${API_BASE}/cozinha/pendentes`, headers)
      .then((res) => {
        setItens(res.data);
        setErro(null);
      })
      .catch((err) => {
        console.error("Erro ao buscar itens pendentes:", err);
        setErro("Acesso negado. Verifique seu login ou permissões.");
      });
  };

  const marcarComoPreparado = (id: number) => {
    axios
      .put(`${API_BASE}/cozinha/item/${id}/preparado`, {}, headers)
      .then(carregarItens)
      .catch((err) => {
        console.error("Erro ao atualizar status:", err);
        setErro("Erro ao marcar como preparado.");
      });
  };

  useEffect(() => {
    carregarItens();
    const intervalo = setInterval(carregarItens, 5000);
    return () => clearInterval(intervalo);
  }, [token]);

  const agrupadoPorMesa = itens.reduce<Record<string, ItemCozinhaDTO[]>>(
    (acc, item) => {
      acc[item.mesaNome] = acc[item.mesaNome] || [];
      acc[item.mesaNome].push(item);
      return acc;
    },
    {}
  );

  return (
    <div className="p-4 max-w-6xl mx-auto space-y-6">
      <h1 className="text-2xl font-bold text-center text-blue-700 mb-4 flex items-center justify-center gap-2">
        <Clock className="w-6 h-6" /> Painel da Cozinha / Bar
      </h1>

      {erro && (
        <div className="text-center text-red-600 font-medium">{erro}</div>
      )}

      {Object.entries(agrupadoPorMesa).map(([mesa, itensMesa]) => (
        <div key={mesa} className="bg-white shadow rounded p-4">
          <h2 className="text-lg font-semibold text-blue-600 mb-3">
            Mesa {mesa}
          </h2>
          <ul className="divide-y">
            {itensMesa.map((item) => (
              <li
                key={item.id}
                className="py-2 flex justify-between items-center"
              >
                <div>
                  <span className="font-medium">
                    {item.quantidade}x {item.produtoNome}
                  </span>
                </div>
                <button
                  onClick={() => marcarComoPreparado(item.id)}
                  className="text-green-600 hover:text-green-800 flex items-center gap-1"
                >
                  <CheckCircle className="w-4 h-4" /> Preparado
                </button>
              </li>
            ))}
          </ul>
        </div>
      ))}

      {itens.length === 0 && !erro && (
        <div className="text-center text-gray-500">
          Nenhum item pendente no momento.
        </div>
      )}
    </div>
  );
}

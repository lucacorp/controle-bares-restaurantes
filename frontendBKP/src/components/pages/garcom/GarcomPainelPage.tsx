import { useEffect, useState } from "react";
import axios from "axios";
import { Link } from "react-router-dom";
import { ClipboardList } from "lucide-react";
import { useAuth } from "@/AuthContext"; // ✅ correto

interface ComandaResumo {
  id: number;
  mesaNome: string;
  total: number;
  itens: number;
}

const API_BASE = "http://192.168.200.107:8080";

export default function GarcomPainelPage() {
  const { token } = useAuth(); // ✅ uso correto
  const [comandas, setComandas] = useState<ComandaResumo[]>([]);
  const [busca, setBusca] = useState("");

  const carregarComandas = () => {
    axios.get(`${API_BASE}/comanda/abertas`, {
      headers: { Authorization: `Bearer ${token}` },
    })
      .then(res => setComandas(res.data))
      .catch(err => console.error("Erro ao carregar comandas:", err));
  };

  useEffect(() => {
    carregarComandas();
    const intervalo = setInterval(carregarComandas, 5000);
    return () => clearInterval(intervalo);
  }, [token]);

  const comandasFiltradas = comandas.filter(c =>
    c.mesaNome.toLowerCase().includes(busca.toLowerCase())
  );

  return (
    <div className="p-4 max-w-5xl mx-auto space-y-6">
      <div className="flex items-center gap-2 text-blue-700">
        <ClipboardList className="w-6 h-6" />
        <h1 className="text-2xl font-semibold">Comandas Abertas</h1>
      </div>

      <input
        type="text"
        placeholder="Buscar por mesa..."
        value={busca}
        onChange={e => setBusca(e.target.value)}
        className="w-full sm:w-72 px-3 py-2 border rounded shadow-sm"
      />

      <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 gap-4">
        {comandasFiltradas.map(comanda => (
          <Link
            key={comanda.id}
            to={`/garcom/comanda/${comanda.id}`}
            className="bg-white p-4 rounded shadow hover:bg-gray-50 transition flex flex-col justify-between"
          >
            <div>
              <h2 className="text-lg font-semibold mb-1">Mesa {comanda.mesaNome}</h2>
              <p className="text-sm text-gray-600">{comanda.itens} itens</p>
            </div>
            <div className="mt-2 font-bold text-right text-blue-600">
              R$ {comanda.total.toFixed(2)}
            </div>
          </Link>
        ))}
      </div>
    </div>
  );
}

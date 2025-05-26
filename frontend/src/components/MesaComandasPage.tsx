import { useEffect, useState } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import api from '../services/api';
import { Button } from "@/components/ui/button";
import { Loader2 } from "lucide-react";

interface Comanda {
  id: number;
  status: string;
  dataAbertura: string;
  dataFechamento: string | null;
}

export default function MesaComandasPage() {
  const { mesaId } = useParams<{ mesaId: string }>();
  const navigate = useNavigate();
  const [comandas, setComandas] = useState<Comanda[]>([]);
  const [loading, setLoading] = useState(false);
  const [criando, setCriando] = useState(false);
  const [erro, setErro] = useState<string | null>(null);

  const loadComandas = async () => {
    setLoading(true);
    try {
      const resp = await api.get(`/comandas/mesa/${mesaId}`);
      setComandas(Array.isArray(resp.data) ? resp.data : []);
      setErro(null);
    } catch (err) {
      console.error('Erro ao carregar comandas:', err);
      setErro('Falha ao carregar comandas. Tente novamente.');
    } finally {
      setLoading(false);
    }
  };

  const criarComanda = async () => {
    setCriando(true);
    try {
      const resp = await api.post('/comandas', { mesaId: Number(mesaId) });
      alert('Comanda criada com sucesso!');
      navigate(`/comandas/${resp.data.id}/itens`);
    } catch (err) {
      console.error('Erro ao criar comanda:', err);
      alert('Falha ao criar comanda.');
    } finally {
      setCriando(false);
    }
  };

  useEffect(() => {
    loadComandas();
  }, [mesaId]);

  return (
    <div className="p-4 max-w-3xl mx-auto">
      <h2 className="text-2xl font-bold mb-6 text-gray-800">
        Comandas da Mesa #{mesaId}
      </h2>

      <Button
        onClick={criarComanda}
        disabled={criando}
        className="mb-6 bg-green-600 hover:bg-green-700 text-white"
      >
        {criando && <Loader2 className="animate-spin mr-2" size={16} />}
        Criar nova comanda
      </Button>

      {loading ? (
        <div className="flex items-center space-x-2 text-gray-600">
          <Loader2 className="animate-spin" /> <span>Carregando comandas...</span>
        </div>
      ) : erro ? (
        <p className="text-red-600">{erro}</p>
      ) : comandas.length === 0 ? (
        <p className="text-gray-500">Nenhuma comanda nesta mesa.</p>
      ) : (
        <ul className="space-y-4">
          {comandas.map(comanda => (
            <li
              key={comanda.id}
              className="border border-gray-200 p-4 rounded-xl shadow-sm flex justify-between items-center bg-white hover:shadow-md transition"
            >
              <div>
                <p className="font-semibold text-lg">
                  Comanda #{comanda.id}
                </p>
                <p className="text-sm text-gray-600">
                  Status: <span className="font-medium">{comanda.status}</span>
                </p>
                <p className="text-xs text-gray-400">
                  Abertura: {comanda.dataAbertura}
                </p>
              </div>

              <Button
                onClick={() => navigate(`/comandas/${comanda.id}/itens`)}
                className="bg-blue-600 hover:bg-blue-700 text-white"
              >
                Ver Itens
              </Button>
            </li>
          ))}
        </ul>
      )}
    </div>
  );
}

import { useEffect, useState } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import axios from 'axios';
import { Button } from "@/components/ui/button";
import { Loader2 } from "lucide-react";

interface Comanda {
  id: number;
  status: string;
  dataAbertura: string;
  dataFechamento: string | null;
}

const API_BASE_URL = 'http://localhost:8080/api';

export default function MesaComandasPage() {
  const { mesaId } = useParams<{ mesaId: string }>();
  const navigate = useNavigate();
  const [comandas, setComandas] = useState<Comanda[]>([]);
  const [loading, setLoading] = useState(false);
  const [criando, setCriando] = useState(false);
  const [erro, setErro] = useState<string | null>(null);

  const loadComandas = async () => {
    if (!mesaId) return;
    setLoading(true);
    setErro(null);
    try {
      const resp = await axios.get(`${API_BASE_URL}/comandas/mesa/${mesaId}`);
      if (Array.isArray(resp.data)) {
        setComandas(resp.data);
      } else {
        setComandas([]);
      }
    } catch (err: any) {
      setErro(err.response?.data?.message || 'Falha ao carregar comandas.');
    } finally {
      setLoading(false);
    }
  };

  const criarComanda = async () => {
    if (!mesaId) return;
    setCriando(true);
    try {
      const resp = await axios.post(`${API_BASE_URL}/comandas/criar/${mesaId}`);
      navigate(`/comandas/${resp.data.id}/itens?mesaId=${mesaId}`);
    } catch (err: any) {
      alert(err.response?.data?.message || 'Falha ao criar comanda.');
    } finally {
      setCriando(false);
      loadComandas();
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

      {erro && <p className="text-red-500 mb-4">{erro}</p>}

      <div className="flex items-center gap-2 mb-6">
        <Button onClick={criarComanda} disabled={criando}>
          {criando && <Loader2 className="w-4 h-4 mr-2 animate-spin" />}
          Nova Comanda
        </Button>
        <Button variant="outline" onClick={loadComandas} disabled={loading}>
          {loading && <Loader2 className="w-4 h-4 mr-2 animate-spin" />}
          Atualizar
        </Button>
      </div>

      {loading ? (
        <p>Carregando comandas...</p>
      ) : comandas.length === 0 ? (
        <p className="text-gray-500">Nenhuma comanda encontrada para esta mesa.</p>
      ) : (
        <ul className="space-y-3">
          {comandas.map((c) => (
            <li
              key={c.id}
              className="p-3 border rounded-lg flex justify-between items-center hover:bg-gray-50"
            >
              <div>
                <p className="font-semibold">Comanda #{c.id}</p>
                <p className="text-sm text-gray-500">
                  Status: {c.status} | Abertura: {new Date(c.dataAbertura).toLocaleString()}
                </p>
              </div>
              <Button onClick={() => navigate(`/comandas/${c.id}/itens?mesaId=${mesaId}`)}>
                Ver Itens
              </Button>
            </li>
          ))}
        </ul>
      )}
    </div>
  );
}

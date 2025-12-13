// src/pages/ComandasMesaPage.tsx
import React, { useEffect, useState } from "react";
import { useParams, useNavigate } from "react-router-dom";
import api from "../services/api";
import { Loader2 } from "lucide-react";

interface ComandaResumo {
  id: number;
  status?: string;
  ativa?: boolean;
  dataAbertura?: string;
  dataFechamento?: string | null;
  itensCount?: number;
}

export default function ComandasMesaPage() {
  const { mesaId } = useParams<{ mesaId: string }>();
  const navigate = useNavigate();
  const [comandas, setComandas] = useState<ComandaResumo[]>([]);
  const [loading, setLoading] = useState(false);
  const [criando, setCriando] = useState(false);
  const [erro, setErro] = useState<string | null>(null);

  const loadComandas = async () => {
    if (!mesaId) return;
    setLoading(true);
    setErro(null);
    try {
      // busca todas as comandas da mesa (ajuste no backend: retorna LIST)
      const resp = await api.get(`/comandas/mesa/${mesaId}`);
      // filtra apenas ABERTAS (se quiser exibir apenas abertas)
      const lista: ComandaResumo[] = Array.isArray(resp.data) ? resp.data : [];
      setComandas(lista);
    } catch (err: any) {
      console.error("Erro loadComandas:", err);
      setErro(err?.response?.data?.message || "Falha ao carregar comandas.");
    } finally {
      setLoading(false);
    }
  };

  const criarComanda = async () => {
    if (!mesaId) return;
    setCriando(true);
    try {
      const resp = await api.post(`/comandas/criar/${mesaId}`);
      // abre a página de itens da nova comanda
      const novaId = resp.data?.id;
      if (novaId) {
        navigate(`/comandas/${novaId}/itens?mesaId=${mesaId}`);
      } else {
        // se backend retornar objeto diferente, recarrega lista
        await loadComandas();
      }
    } catch (err: any) {
      console.error("Erro criarComanda:", err);
      alert(err?.response?.data?.message || "Falha ao criar comanda.");
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
      <h2 className="text-2xl font-bold mb-6 text-gray-800">Comandas da Mesa #{mesaId}</h2>
      {erro && <p className="text-red-500 mb-4">{erro}</p>}

      <div className="flex items-center gap-2 mb-6">
        <button
          onClick={criarComanda}
          disabled={criando}
          className="px-4 py-2 bg-green-600 text-white rounded"
        >
          {criando ? <Loader2 className="w-4 h-4 inline-block animate-spin mr-2" /> : null}
          Nova Comanda
        </button>

        <button
          onClick={loadComandas}
          disabled={loading}
          className="px-3 py-2 border rounded"
        >
          {loading ? <Loader2 className="w-4 h-4 inline-block animate-spin mr-2" /> : "Atualizar"}
        </button>
      </div>

      {loading ? (
        <p>Carregando comandas...</p>
      ) : comandas.length === 0 ? (
        <p className="text-gray-500">Nenhuma comanda encontrada para esta mesa.</p>
      ) : (
        <ul className="space-y-3">
          {comandas.map((c) => (
            <li key={c.id} className="p-3 border rounded-lg flex justify-between items-center hover:bg-gray-50">
              <div>
                <p className="font-semibold">Comanda #{c.id}</p>
                <p className="text-sm text-gray-500">
                  Status: {c.status ?? (c.ativa ? "ABERTA" : "FECHADA")}{" "}
                  {c.dataAbertura ? `| ${new Date(c.dataAbertura).toLocaleString()}` : ""}
                </p>
              </div>

              <div className="flex gap-2">
                <button
                  onClick={() => navigate(`/comandas/${c.id}/itens?mesaId=${mesaId}`)}
                  className="px-3 py-1 bg-blue-600 text-white rounded"
                >
                  Ver Itens
                </button>
              </div>
            </li>
          ))}
        </ul>
      )}
    </div>
  );
}

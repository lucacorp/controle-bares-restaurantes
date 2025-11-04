import React, { useEffect, useState } from "react";
import FiltroComandas from "./FiltroComandas";
import MesaCard from "./MesaCard";
import api from "../services/api"; // cliente Axios configurado

interface Comanda {
  id: number;
  numeroMesa: number;
  status: string;
  criadoEm: string;
}

const ComandasPorMesa: React.FC = () => {
  const [comandas, setComandas] = useState<Comanda[]>([]);
  const [loading, setLoading] = useState(false);
  const [filtro, setFiltro] = useState({ mesa: "", status: "" });

  const carregarComandas = async () => {
    setLoading(true);
    try {
      if (!filtro.mesa) {
        setComandas([]);
        return;
      }

      // Chamada ajustada para usar a rota correta do backend
      const resp = await api.get(`/comandas/mesa/${filtro.mesa}`);
      let data: Comanda[] = resp.data;

      // Se houver filtro de status, aplicamos no frontend
      if (filtro.status) {
        data = data.filter(c => c.status === filtro.status);
      }

      setComandas(data);
    } catch (e) {
      console.error("Erro ao carregar comandas", e);
      setComandas([]);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    carregarComandas();
  }, [filtro]);

  const handleAbrirComanda = async (mesa: number) => {
    try {
      await api.post(`/comandas/criar/${mesa}`);
      carregarComandas();
    } catch (e) {
      console.error("Erro ao abrir comanda", e);
    }
  };

  const handleExcluir = async (id: number) => {
    try {
      await api.delete(`/comandas/${id}`);
      carregarComandas();
    } catch (e) {
      console.error("Erro ao excluir comanda", e);
    }
  };

  const handleFechar = async (id: number) => {
    try {
      await api.post(`/comandas/${id}/fechar`);
      carregarComandas();
    } catch (e) {
      console.error("Erro ao fechar comanda", e);
    }
  };

  return (
    <div className="p-4 space-y-4">
      <h1 className="text-xl font-bold">Comandas por Mesa</h1>

      {/* Filtros */}
      <FiltroComandas filtro={filtro} setFiltro={setFiltro} />

      {/* Lista de comandas */}
      {loading ? (
        <p>Carregando...</p>
      ) : (
        <div className="grid md:grid-cols-2 lg:grid-cols-3 gap-4">
          {comandas.length === 0 ? (
            <p>Nenhuma comanda encontrada.</p>
          ) : (
            comandas.map(c => (
              <MesaCard
                key={c.id}
                comanda={c}
                onAbrir={handleAbrirComanda}
                onExcluir={handleExcluir}
                onFechar={handleFechar}
              />
            ))
          )}
        </div>
      )}
    </div>
  );
};

export default ComandasPorMesa;

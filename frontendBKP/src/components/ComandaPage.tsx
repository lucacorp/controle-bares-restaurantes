import React, { useEffect, useState } from "react";
import { useParams } from "react-router-dom";
import api from "../services/api";

interface Produto {
  id: number;
  nome: string;
  precoVenda: number;
}

interface ItemComanda {
  id: number;
  quantidade: number;
  produto: Produto;
}

interface Comanda {
  id: number;
  mesaId: number;
  ativa: boolean;
  itens: ItemComanda[];
}

const ComandaPage: React.FC = () => {
  const { id } = useParams<{ id: string }>();
  const [comandas, setComandas] = useState<Comanda[]>([]);
  const [mensagem, setMensagem] = useState<string>("");

  useEffect(() => {
    carregarComandas();
  }, [id]);

  const carregarComandas = async () => {
    try {
      const { data } = await api.get(`/comandas/mesa/${id}`);
      setComandas(Array.isArray(data) ? data : []);
      setMensagem("");
    } catch (e: any) {
      setMensagem(e.response?.data?.message || "Erro ao carregar comandas.");
    }
  };

  const handleFecharComanda = async (comandaId: number) => {
    try {
      await api.put(`/comandas/${comandaId}/fechar`);
      setMensagem("Comanda fechada com sucesso!");
      carregarComandas();
    } catch (e: any) {
      setMensagem(e.response?.data?.message || "Erro ao fechar comanda.");
    }
  };

  const handleExcluirComanda = async (comandaId: number) => {
    try {
      await api.delete(`/comandas/${comandaId}`);
      setMensagem("Comanda excluída com sucesso!");
      carregarComandas();
    } catch (e: any) {
      setMensagem(e.response?.data?.message || "Erro ao excluir comanda.");
    }
  };

  const calcularTotal = (itens: ItemComanda[]) =>
    itens.reduce((sum, item) => sum + (item.produto?.precoVenda || 0) * (item.quantidade || 0), 0);

  return (
    <div className="container mt-4">
      <h2>Comandas da Mesa {id}</h2>

      {mensagem && <div className="alert alert-info mt-3">{mensagem}</div>}

      {comandas.length === 0 ? (
        <p>Nenhuma comanda encontrada para esta mesa.</p>
      ) : (
        comandas.map((c) => (
          <div key={c.id} className="card mt-4">
            <div className="card-header d-flex justify-content-between align-items-center">
              <h5>Comanda {c.id}</h5>
              {c.ativa && (
                <div>
                  <button
                    className="btn btn-sm btn-success me-2"
                    onClick={() => handleFecharComanda(c.id)}
                  >
                    Fechar Comanda
                  </button>
                  <button
                    className="btn btn-sm btn-danger"
                    onClick={() => handleExcluirComanda(c.id)}
                  >
                    Excluir Comanda
                  </button>
                </div>
              )}
            </div>

            <div className="card-body">
              {c.itens.length === 0 ? (
                <p>Nenhum item nesta comanda.</p>
              ) : (
                <table className="table">
                  <thead>
                    <tr>
                      <th>Descrição</th>
                      <th>Qtd</th>
                      <th>Preço</th>
                      <th>Subtotal</th>
                    </tr>
                  </thead>
                  <tbody>
                    {c.itens.map((item) => (
                      <tr key={item.id}>
                        <td>{item.produto?.nome || "Produto"}</td>
                        <td>{item.quantidade}</td>
                        <td>R$ {(item.produto?.precoVenda || 0).toFixed(2)}</td>
                        <td>
                          R${" "}
                          {((item.produto?.precoVenda || 0) * (item.quantidade || 0)).toFixed(2)}
                        </td>
                      </tr>
                    ))}
                  </tbody>
                  <tfoot>
                    <tr>
                      <td colSpan={3} className="text-end fw-bold">
                        Total:
                      </td>
                      <td className="fw-bold">R$ {calcularTotal(c.itens).toFixed(2)}</td>
                    </tr>
                  </tfoot>
                </table>
              )}
            </div>
          </div>
        ))
      )}
    </div>
  );
};

export default ComandaPage;

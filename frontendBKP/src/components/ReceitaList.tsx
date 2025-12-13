// src/components/ReceitaList.tsx
import { useEffect, useState } from "react";
import api from "./axiosConfig";
import ReceitaForm from "./ReceitaForm";

interface ReceitaItemDTO {
  produtoId: number;
  quantidade: number;
}

interface ReceitaDTO {
  id: number;
  nome: string;
  adicional: number;
  produtoFinalId: number;
  itens: ReceitaItemDTO[];
}

interface Produto {
  id: number;
  nome: string;
}

export default function ReceitaList() {
  const [receitas, setReceitas] = useState<ReceitaDTO[]>([]);
  const [produtos, setProdutos] = useState<Produto[]>([]);
  const [receitaEditando, setReceitaEditando] = useState<number | undefined>(undefined);
  const [showForm, setShowForm] = useState(false);

  useEffect(() => {
    carregarDados();
  }, []);

  const carregarDados = async () => {
    try {
      const [receitasRes, produtosRes] = await Promise.all([
        api.get("/receitas"),
        api.get("/produtos"),
      ]);

      /** ───── DEBUG ───── **/
      console.log("Receitas da API:", receitasRes.data);
      console.log("Produtos da API:", produtosRes.data);
      /** ────────────────── **/

      // Garantir que ambos são arrays
      if (Array.isArray(receitasRes.data)) {
        setReceitas(receitasRes.data);
      } else {
        console.error("Formato inválido: /receitas não retornou array.");
        setReceitas([]);
      }

      if (Array.isArray(produtosRes.data)) {
        setProdutos(produtosRes.data);
      } else {
        console.error("Formato inválido: /produtos não retornou array.");
        setProdutos([]);
      }
    } catch (error) {
      console.error("Erro ao carregar dados:", error);
      alert("Erro ao carregar dados. Faça login novamente.");
    }
  };

  const excluirReceita = async (id: number) => {
    if (window.confirm("Deseja excluir esta receita?")) {
      await api.delete(`/receitas/${id}`);
      carregarDados();
    }
  };

  const abrirEdicao = (id: number) => {
    setReceitaEditando(id);
    setShowForm(true);
  };

  const abrirNovo = () => {
    setReceitaEditando(undefined);
    setShowForm(true);
  };

  const getNomeProduto = (id: number) =>
    produtos.find((p) => p.id === id)?.nome || `ID ${id}`;

  const logout = () => {
    localStorage.removeItem("token");
    window.location.href = "/login";
  };

  return (
    <div className="p-5">
      <div className="flex justify-between items-center mb-4">
        <h2 className="text-2xl font-bold">Receitas</h2>
        <button
          onClick={logout}
          className="bg-red-600 text-white px-4 py-2 rounded"
        >
          Sair
        </button>
      </div>

      <button
        onClick={abrirNovo}
        className="mb-4 bg-blue-600 text-white px-4 py-2 rounded"
      >
        + Nova Receita
      </button>

      {showForm && (
        <div className="border p-4 mb-4 rounded">
          <ReceitaForm
            receitaId={receitaEditando}
            onSuccess={() => {
              setShowForm(false);
              carregarDados();
            }}
          />
        </div>
      )}

      <table className="w-full border-collapse border rounded text-sm">
        <thead className="bg-gray-100">
          <tr>
            <th className="p-2 border">Nome</th>
            <th className="p-2 border">Produto Final</th>
            <th className="p-2 border">Adicional (%)</th>
            <th className="p-2 border">Itens</th>
            <th className="p-2 border">Ações</th>
          </tr>
        </thead>
        <tbody>
          {receitas.map((r) => (
            <tr key={r.id} className="border-t">
              <td className="p-2 border">{r.nome}</td>
              <td className="p-2 border">
                {getNomeProduto(r.produtoFinalId)} (ID {r.produtoFinalId})
              </td>
              <td className="p-2 border">{r.adicional.toFixed(2)}%</td>
              <td className="p-2 border">
                <ul className="list-disc ml-4">
                  {r.itens.map((item, idx) => (
                    <li key={idx}>
                      {getNomeProduto(item.produtoId)}: {item.quantidade}
                    </li>
                  ))}
                </ul>
              </td>
              <td className="p-2 border">
                <button
                  onClick={() => abrirEdicao(r.id)}
                  className="mr-2 bg-yellow-500 text-white px-2 py-1 rounded"
                >
                  Editar
                </button>
                <button
                  onClick={() => excluirReceita(r.id)}
                  className="bg-red-500 text-white px-2 py-1 rounded"
                >
                  Excluir
                </button>
              </td>
            </tr>
          ))}
          {receitas.length === 0 && (
            <tr>
              <td colSpan={5} className="text-center p-4 text-gray-500">
                Nenhuma receita encontrada.
              </td>
            </tr>
          )}
        </tbody>
      </table>
    </div>
  );
}

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
      setReceitas(receitasRes.data);
      setProdutos(produtosRes.data);
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

  const getNomeProduto = (id: number) => {
    return produtos.find((p) => p.id === id)?.nome || `ID ${id}`;
  };

  const logout = () => {
    localStorage.removeItem("token");
    window.location.href = "/login";
  };

  return (
    <div style={{ padding: 20 }}>
      <div style={{ display: "flex", justifyContent: "space-between", alignItems: "center" }}>
        <h2>Receitas</h2>
        <button onClick={logout} style={{ background: "#c00", color: "white", border: "none", padding: "8px 12px", borderRadius: 4 }}>
          Sair
        </button>
      </div>

      <button onClick={abrirNovo} style={{ margin: "1rem 0" }}>
        + Nova Receita
      </button>

      {showForm && (
        <div style={{ border: "1px solid #ccc", padding: 16, marginBottom: "1rem" }}>
          <ReceitaForm
            receitaId={receitaEditando}
            onSuccess={() => {
              setShowForm(false);
              carregarDados();
            }}
          />
        </div>
      )}

      <table border={1} cellPadding={8} style={{ borderCollapse: "collapse", width: "100%" }}>
        <thead>
          <tr>
            <th>Nome</th>
            <th>Produto Final</th>
            <th>Adicional (%)</th>
            <th>Itens</th>
            <th>Ações</th>
          </tr>
        </thead>
        <tbody>
          {receitas.map((r) => (
            <tr key={r.id}>
              <td>{r.nome}</td>
              <td>{getNomeProduto(r.produtoFinalId)} (ID {r.produtoFinalId})</td>
              <td>{r.adicional.toFixed(2)}%</td>
              <td>
                <ul>
                  {r.itens.map((item, idx) => (
                    <li key={idx}>
                      {getNomeProduto(item.produtoId)}: {item.quantidade}
                    </li>
                  ))}
                </ul>
              </td>
              <td>
                <button onClick={() => abrirEdicao(r.id)} style={{ marginRight: 8 }}>
                  Editar
                </button>
                <button onClick={() => excluirReceita(r.id)}>Excluir</button>
              </td>
            </tr>
          ))}
        </tbody>
      </table>
    </div>
  );
}

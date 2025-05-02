import { useEffect, useState } from "react";
import axios from "axios";

interface ReceitaItemDTO {
  produtoId: number;
  quantidade: number;
}

interface ReceitaDTO {
  id?: number;
  nome: string;
  adicional: number;
  produtoFinalId: number;
  itens: ReceitaItemDTO[];
}

interface Produto {
  id: number;
  nome: string;
}

interface Props {
  receitaId?: number;
  onSuccess?: () => void;
}

export default function ReceitaForm({ receitaId, onSuccess }: Props) {
  const [form, setForm] = useState<ReceitaDTO>({
    nome: "",
    adicional: 0,
    produtoFinalId: 0,
    itens: [{ produtoId: 0, quantidade: 1 }],
  });
  const [produtos, setProdutos] = useState<Produto[]>([]);
  const [erro, setErro] = useState<string | null>(null);

  useEffect(() => {
    axios.get("/api/produtos").then(res => setProdutos(res.data));
    if (receitaId) {
      axios.get(`/api/receitas/${receitaId}`)
        .then(res => setForm(res.data))
        .catch(() => setErro("Erro ao carregar receita"));
    }
  }, [receitaId]);

  const handleItemChange = (index: number, field: keyof ReceitaItemDTO, value: any) => {
    const novosItens = [...form.itens];
    novosItens[index] = { ...novosItens[index], [field]: value };
    setForm({ ...form, itens: novosItens });
  };

  const addItem = () => {
    setForm({ ...form, itens: [...form.itens, { produtoId: 0, quantidade: 1 }] });
  };

  const removeItem = (index: number) => {
    const novosItens = [...form.itens];
    novosItens.splice(index, 1);
    setForm({ ...form, itens: novosItens });
  };

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setErro(null);

    if (!form.nome || form.produtoFinalId === 0 || form.itens.some(i => i.produtoId === 0)) {
      setErro("Preencha todos os campos obrigatórios");
      return;
    }

    try {
      if (form.id) {
        await axios.put(`/api/receitas/${form.id}`, form);
      } else {
        await axios.post("/api/receitas", form);
      }
      if (onSuccess) onSuccess();
    } catch (error: any) {
      setErro(error.response?.data?.message || "Erro ao salvar receita");
    }
  };

  return (
    <form onSubmit={handleSubmit}>
      <h2>{form.id ? "Editar Receita" : "Nova Receita"}</h2>

      {erro && <p style={{ color: "red" }}>{erro}</p>}

      <div>
        <label>Nome:</label>
        <input
          type="text"
          value={form.nome}
          onChange={(e) => setForm({ ...form, nome: e.target.value })}
          required
        />
      </div>

      <div>
        <label>Adicional (%):</label>
        <input
          type="number"
          step="0.01"
          value={form.adicional}
          onChange={(e) => setForm({ ...form, adicional: parseFloat(e.target.value) || 0 })}
        />
      </div>

      <div>
        <label>Produto Final:</label>
        <select
          value={form.produtoFinalId}
          onChange={(e) => setForm({ ...form, produtoFinalId: parseInt(e.target.value) })}
          required
        >
          <option value={0}>Selecione</option>
          {produtos.map(p => (
            <option key={p.id} value={p.id}>{p.nome}</option>
          ))}
        </select>
      </div>

      <h3>Itens da Receita</h3>
      {form.itens.map((item, index) => (
        <div key={index} style={{ marginBottom: "1rem" }}>
          <select
            value={item.produtoId}
            onChange={(e) => handleItemChange(index, "produtoId", parseInt(e.target.value))}
            required
          >
            <option value={0}>Produto</option>
            {produtos.map(p => (
              <option key={p.id} value={p.id}>{p.nome}</option>
            ))}
          </select>

          <input
            type="number"
            step="0.01"
            value={item.quantidade}
            onChange={(e) => handleItemChange(index, "quantidade", parseFloat(e.target.value))}
            required
          />

          <button type="button" onClick={() => removeItem(index)}>Remover</button>
        </div>
      ))}

      <button type="button" onClick={addItem}>+ Adicionar Item</button>
      <br /><br />
      <button type="submit">Salvar Receita</button>
    </form>
  );
}

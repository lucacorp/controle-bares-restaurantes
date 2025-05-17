// src/components/ReceitaForm.tsx
import { useEffect, useState } from "react";
import api from "../services/api";

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
    api.get("/produtos")
      .then(res => setProdutos(res.data))
      .catch(() => setErro("Erro ao carregar produtos"));

    if (receitaId) {
      api.get(`/receitas/${receitaId}`)
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
        await api.put(`/receitas/${form.id}`, form);
      } else {
        await api.post("/receitas", form);
      }
      onSuccess?.();
    } catch (error: any) {
      setErro(error.response?.data?.message || "Erro ao salvar receita");
    }
  };

  return (
    <div className="min-h-screen flex justify-center items-start bg-gray-100 py-10 px-4">
      <div className="bg-white rounded-2xl shadow-md p-6 w-full max-w-3xl">
        <h2 className="text-2xl font-bold mb-4">{form.id ? "Editar Receita" : "Nova Receita"}</h2>

        {erro && <p className="text-red-500 mb-4">{erro}</p>}

        <form onSubmit={handleSubmit} className="space-y-4">
          <div>
            <label className="block font-medium">Nome:</label>
            <input
              type="text"
              value={form.nome}
              onChange={(e) => setForm({ ...form, nome: e.target.value })}
              required
              className="w-full border border-gray-300 rounded p-2"
            />
          </div>

          <div>
            <label className="block font-medium">Adicional (%):</label>
            <input
              type="number"
              step="0.01"
              value={form.adicional}
              onChange={(e) => setForm({ ...form, adicional: parseFloat(e.target.value) || 0 })}
              className="w-full border border-gray-300 rounded p-2"
            />
          </div>

          <div>
            <label className="block font-medium">Produto Final:</label>
            <select
              value={form.produtoFinalId}
              onChange={(e) => setForm({ ...form, produtoFinalId: parseInt(e.target.value) })}
              required
              className="w-full border border-gray-300 rounded p-2"
            >
              <option value={0}>Selecione</option>
              {produtos.map(p => (
                <option key={p.id} value={p.id}>{p.nome}</option>
              ))}
            </select>
          </div>

          <h3 className="text-lg font-semibold mt-6">Itens da Receita</h3>
          {form.itens.map((item, index) => (
            <div key={index} className="flex items-center gap-2 mb-2">
              <select
                value={item.produtoId}
                onChange={(e) => handleItemChange(index, "produtoId", parseInt(e.target.value))}
                required
                className="border border-gray-300 rounded p-2"
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
                className="border border-gray-300 rounded p-2 w-24"
              />

              <button
                type="button"
                onClick={() => removeItem(index)}
                className="text-red-600 hover:underline"
              >
                Remover
              </button>
            </div>
          ))}

          <button type="button" onClick={addItem} className="text-blue-600 hover:underline">
            + Adicionar Item
          </button>

          <div className="pt-4">
            <button
              type="submit"
              className="bg-blue-600 text-white px-4 py-2 rounded hover:bg-blue-700"
            >
              Salvar Receita
            </button>
          </div>
        </form>
      </div>
    </div>
  );
}

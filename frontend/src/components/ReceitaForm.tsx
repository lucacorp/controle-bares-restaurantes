// src/components/ReceitaForm.tsx
import { useEffect, useState } from "react";
import api from "../services/api";

interface ReceitaItemDTO {
  produtoId: number;
  quantidade: number;
}

export interface ReceitaDTO {
  id?: number;
  nome: string;
  adicional: number;
  produtoFinalId: number;
  itens: ReceitaItemDTO[];
}

interface ProdutoDTO {
  id: number;
  nome: string;
}

interface Props {
  receitaId?: number;
  onSuccess?: () => void;
}

/**
 * 🍳 Formulário de criação/edição de receita
 * --------------------------------------------------
 * • Autopreenche quando recebe um receitaId
 * • Validação básica de campos obrigatórios
 * • Produtos carregados da API (tratando retorno string ou array)
 */
export default function ReceitaForm({ receitaId, onSuccess }: Props) {
  /* ----------------------- Estados ----------------------- */
  const [form, setForm] = useState<ReceitaDTO>({
    nome: "",
    adicional: 0,
    produtoFinalId: 0,
    itens: [{ produtoId: 0, quantidade: 1 }],
  });

  const [produtos, setProdutos] = useState<ProdutoDTO[]>([]);
  const [erro, setErro] = useState<string | null>(null);
  const [loading, setLoading] = useState<boolean>(false);

  /* ---------------------- Helpers ------------------------ */
  const fetchProdutos = async () => {
    try {
      const res = await api.get("/produtos");
      let data: unknown = res.data;
      if (typeof data === "string") {
        try {
          data = JSON.parse(data);
        } catch (e) {
          console.error("Falha JSON.parse produtos", e);
        }
      }
      setProdutos(Array.isArray(data) ? (data as ProdutoDTO[]) : []);
    } catch (e) {
      setErro("Erro ao carregar produtos");
    }
  };

  const fetchReceita = async (id: number) => {
    try {
      const res = await api.get(`/receitas/${id}`);
      setForm(res.data);
    } catch (e) {
      setErro("Erro ao carregar receita");
    }
  };

  /* ---------------------- Effects ------------------------ */
  useEffect(() => {
    fetchProdutos();
    if (receitaId) fetchReceita(receitaId);
  }, [receitaId]);

  /* --------------------- Handlers ------------------------ */
  const handleItemChange = (idx: number, field: keyof ReceitaItemDTO, value: number) => {
    setForm((prev) => {
      const itens = [...prev.itens];
      itens[idx] = { ...itens[idx], [field]: value } as ReceitaItemDTO;
      return { ...prev, itens };
    });
  };

  const addItem = () =>
    setForm((prev) => ({ ...prev, itens: [...prev.itens, { produtoId: 0, quantidade: 1 }] }));

  const removeItem = (idx: number) =>
    setForm((prev) => ({ ...prev, itens: prev.itens.filter((_, i) => i !== idx) }));

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setErro(null);

    // Validação simples
    if (!form.nome.trim() || form.produtoFinalId === 0 || form.itens.some((i) => i.produtoId === 0)) {
      setErro("Preencha todos os campos obrigatórios");
      return;
    }

    try {
      setLoading(true);
      if (form.id) {
        await api.put(`/receitas/${form.id}`, form);
      } else {
        await api.post("/receitas", form);
      }
      onSuccess?.();
    } catch (error: any) {
      setErro(error.response?.data?.message || "Erro ao salvar receita");
    } finally {
      setLoading(false);
    }
  };

  /* ---------------------- Render ------------------------- */
  return (
    <div className="bg-white rounded-2xl shadow-md p-6 w-full max-w-3xl mx-auto">
      <h2 className="text-2xl font-bold mb-4">{form.id ? "Editar Receita" : "Nova Receita"}</h2>

      {erro && <p className="text-red-500 mb-4">{erro}</p>}

      <form onSubmit={handleSubmit} className="space-y-4">
        {/* Nome */}
        <div>
          <label className="block font-medium">Nome<span className="text-red-600">*</span></label>
          <input
            className="w-full border rounded p-2"
            value={form.nome}
            onChange={(e) => setForm({ ...form, nome: e.target.value })}
            required
          />
        </div>

        {/* Adicional */}
        <div>
          <label className="block font-medium">Adicional (%)</label>
          <input
            type="number"
            step="0.01"
            className="w-full border rounded p-2"
            value={form.adicional}
            onChange={(e) => setForm({ ...form, adicional: parseFloat(e.target.value) || 0 })}
          />
        </div>

        {/* Produto Final */}
        <div>
          <label className="block font-medium">Produto Final<span className="text-red-600">*</span></label>
          <select
            className="w-full border rounded p-2"
            value={form.produtoFinalId}
            onChange={(e) => setForm({ ...form, produtoFinalId: parseInt(e.target.value) })}
            required
          >
            <option value={0}>Selecione</option>
            {produtos.map((p) => (
              <option key={p.id} value={p.id}>{p.nome}</option>
            ))}
          </select>
        </div>

        {/* Itens da Receita */}
        <h3 className="text-lg font-semibold mt-6">Itens da Receita</h3>
        <div className="space-y-2">
          {form.itens.map((item, idx) => (
            <div key={idx} className="flex items-center gap-2">
              <select
                className="border rounded p-2 flex-1"
                value={item.produtoId}
                onChange={(e) => handleItemChange(idx, "produtoId", parseInt(e.target.value))}
                required
              >
                <option value={0}>Produto</option>
                {produtos.map((p) => (
                  <option key={p.id} value={p.id}>{p.nome}</option>
                ))}
              </select>

              <input
                type="number"
                step="0.01"
                className="border rounded p-2 w-[90px]"
                value={item.quantidade}
                onChange={(e) => handleItemChange(idx, "quantidade", parseFloat(e.target.value) || 0)}
                required
              />

              <button type="button" className="text-red-600" onClick={() => removeItem(idx)}>
                Remover
              </button>
            </div>
          ))}

          <button type="button" className="text-blue-600" onClick={addItem}>
            + Adicionar Item
          </button>
        </div>

        {/* Botão Salvar */}
        <div className="pt-4">
          <button
            type="submit"
            className="bg-blue-600 hover:bg-blue-700 text-white px-4 py-2 rounded"
            disabled={loading}
          >
            {loading ? "Salvando..." : "Salvar Receita"}
          </button>
        </div>
      </form>
    </div>
  );
}

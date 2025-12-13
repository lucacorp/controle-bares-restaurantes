// src/components/AjusteEstoqueForm.tsx
import api from "../services/api";
import { useState } from "react";
import { MovimentacaoEstoqueDTO } from "../types/MovimentacaoEstoqueDTO";
import axios from "axios";
// REMOVA ESTA LINHA: import { useOutletContext } from 'react-router-dom';

// REMOVA ESTA INTERFACE:
// interface OutletContext {
//   productId: number;
// }

// ADICIONE productId como prop novamente, e REMOVA o uso de useOutletContext
export default function AjusteEstoqueForm({ productId }: { productId: number }) { // <-- AQUI A MUDANÇA
  // REMOVA ESTA LINHA: const { productId } = useOutletContext<OutletContext>();

  console.log("AjusteEstoqueForm recebeu productId VIA PROP:", productId); // Verifique este log

  const [quantidade, setQuantidade] = useState<number>(1);
  const [tipo, setTipo] = useState<"ENTRADA" | "SAIDA">("ENTRADA");
  const [descricao, setDescricao] = useState<string>("");

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();

    if (quantidade <= 0) {
      alert("Quantidade deve ser maior que zero");
      return;
    }

    const payload: MovimentacaoEstoqueDTO = {
      produtoId: productId, // Continua usando productId, que agora vem da prop
      quantidade,
      tipo: tipo.trim().toUpperCase() as "ENTRADA" | "SAIDA",
      observacao: descricao.trim(),
    };

    console.log("Payload enviado via Axios:", payload);

    try {
      await api.post("/api/movimentacoes-estoque", payload);
      alert("Movimentação realizada com sucesso!");

      setQuantidade(1);
      setDescricao("");
      setTipo("ENTRADA");
    } catch (error) {
      console.error("Erro ao movimentar estoque:", error);

      if (axios.isAxiosError(error) && error.response?.data?.message) {
        alert(error.response.data.message);
      } else {
        alert("Erro ao movimentar estoque.");
      }
    }
  };

  return (
    <form onSubmit={handleSubmit} className="space-y-4 max-w-md">
      <select
        value={tipo}
        onChange={(e) => setTipo(e.target.value as "ENTRADA" | "SAIDA")}
        className="w-full border rounded p-2"
      >
        <option value="ENTRADA">Entrada</option>
        <option value="SAIDA">Saída</option>
      </select>

      <input
        type="number"
        value={quantidade}
        onChange={(e) => setQuantidade(Number(e.target.value))}
        placeholder="Quantidade"
        className="w-full border rounded p-2"
        min={0.01}
        step={0.01}
      />

      <textarea
        value={descricao}
        onChange={(e) => setDescricao(e.target.value)}
        placeholder="Observação"
        className="w-full border rounded p-2"
      />

      <button
        type="submit"
        className="w-full bg-blue-600 text-white py-2 rounded"
      >
        Confirmar Movimentação
      </button>
    </form>
  );
}
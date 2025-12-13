import { useEffect, useState } from "react";
import api from "../services/api";
import ReciboComanda from "./ReciboComanda";

interface ComandaResumo {
  id: number;
  comandaId: number;
  total: number;
  dataFechamento: string;
  nomeCliente: string;
  observacoes: string;
}

export default function ComandaResumoPage() {
  const [resumos, setResumos] = useState<ComandaResumo[]>([]);
  const [comandaParaImprimir, setComandaParaImprimir] = useState<number | null>(null);

  const loadResumos = async () => {
    try {
      const resp = await api.get("/comandas/resumos");
      setResumos(Array.isArray(resp.data) ? resp.data : []);
    } catch (err) {
      console.error("Erro ao carregar resumos:", err);
    }
  };

  useEffect(() => {
    loadResumos();
  }, []);

  return (
    <div className="p-6 bg-white rounded-lg shadow-lg max-w-5xl mx-auto">
      <h2 className="text-2xl font-bold text-gray-700 mb-4">Resumos de Comandas Finalizadas</h2>

      {resumos.length === 0 ? (
        <p className="text-gray-500">Nenhum resumo encontrado.</p>
      ) : (
        <table className="w-full border-collapse text-sm">
          <thead>
            <tr className="bg-gray-100 text-left">
              <th className="p-2">Comanda</th>
              <th className="p-2">Total</th>
              <th className="p-2">Data</th>
              <th className="p-2">Cliente</th>
              <th className="p-2">Obs</th>
              <th className="p-2">Ações</th>
            </tr>
          </thead>
          <tbody>
            {resumos.map(resumo => (
              <tr key={resumo.id} className="border-b hover:bg-gray-50">
                <td className="p-2 font-medium">#{resumo.comandaId}</td>
                <td className="p-2">R$ {resumo.total.toFixed(2)}</td>
                <td className="p-2">{new Date(resumo.dataFechamento).toLocaleString()}</td>
                <td className="p-2">{resumo.nomeCliente || "-"}</td>
                <td className="p-2">{resumo.observacoes || "-"}</td>
                <td className="p-2">
                  <button
                    onClick={() => setComandaParaImprimir(resumo.comandaId)}
                    className="bg-black text-white text-xs px-3 py-1 rounded hover:bg-gray-800"
                  >
                    Imprimir Recibo
                  </button>
                </td>
              </tr>
            ))}
          </tbody>
        </table>
      )}

      {comandaParaImprimir && (
        <div className="mt-6">
          <ReciboComanda comandaId={comandaParaImprimir} autoPrint />
        </div>
      )}
    </div>
  );
}

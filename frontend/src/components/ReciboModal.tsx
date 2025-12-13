import { ComandaResumoDTO } from "@/types/ComandaResumoDTO";
import api from "@/services/api";

interface Props {
  resumo: ComandaResumoDTO;
  onClose: () => void;
}

export default function ReciboModal({ resumo, onClose }: Props) {
  const reemitir = async () => {
    try {
      await api.post(`/comanda-resumo/${resumo.id}/emitir-nfe`);
      alert("Reemissão solicitada com sucesso.");
    } catch (err: any) {
      alert("Erro ao reemitir NFC-e: " + (err?.response?.data || err?.message));
    }
  };

  return (
    <div className="fixed inset-0 z-50 bg-black bg-opacity-50 flex items-center justify-center">
      <div className="bg-white rounded-2xl shadow-xl p-6 w-full max-w-md relative">
        <button
          onClick={onClose}
          className="absolute top-3 right-3 text-gray-500 hover:text-red-600 text-xl"
        >
          ×
        </button>

        <h2 className="text-2xl font-bold mb-4 text-center text-gray-800">Recibo da Comanda</h2>

        <div className="space-y-2 text-gray-700">
          <p>
            <strong>Cliente:</strong> {resumo.nomeCliente || "Não informado"}
          </p>
          <p>
            <strong>Data de Fechamento:</strong> {resumo.dataFechamento}
          </p>
          <p>
            <strong>Total:</strong> R$ {resumo.valorTotal?.toFixed?.(2) ?? "0.00"}
          </p>
          <p>
            <strong>Status Fiscal:</strong> {resumo.statusFiscal || resumo.statusSat || "—"}
          </p>
          <p>
            <strong>Observações:</strong>
          </p>
          <p className="bg-gray-100 rounded p-2 min-h-[3rem]">{resumo.observacoes || "—"}</p>
        </div>

        <div className="mt-6 text-center">
          <div className="flex items-center justify-center gap-2">
            <button
              onClick={reemitir}
              className="px-4 py-2 bg-green-600 text-white rounded hover:bg-green-700 transition"
            >
              Reemitir NFC-e
            </button>
            <button
              onClick={onClose}
              className="px-4 py-2 bg-blue-600 text-white rounded hover:bg-blue-700 transition"
            >
              Fechar
            </button>
          </div>
        </div>
      </div>
    </div>
  );
}

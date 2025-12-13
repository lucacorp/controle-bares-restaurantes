import { ComandaResumoDTO } from "@/types/ComandaResumoDTO";

interface Props {
  resumo: ComandaResumoDTO;
  onClose: () => void;
}

export default function ReciboModal({ resumo, onClose }: Props) {
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
          <p><strong>Cliente:</strong> {resumo.nomeCliente || "Não informado"}</p>
          <p><strong>Data de Fechamento:</strong> {resumo.dataFechamento}</p>
          <p><strong>Total:</strong> R$ {resumo.valorTotal.toFixed(2)}</p>
          <p><strong>Observações:</strong></p>
          <p className="bg-gray-100 rounded p-2 min-h-[3rem]">{resumo.observacoes || "—"}</p>
        </div>

        <div className="mt-6 text-center">
          <button
            onClick={onClose}
            className="px-4 py-2 bg-blue-600 text-white rounded hover:bg-blue-700 transition"
          >
            Fechar
          </button>
        </div>
      </div>
    </div>
  );
}

import { useEffect, useState, useRef } from "react";
import api from "../services/api";
import { useReactToPrint } from 'react-to-print';

interface ComandaResumo {
  id: number;
  comandaId: number;
  total: number;
  dataFechamento: string;
  nomeCliente: string;
  observacoes: string;
}

interface Props {
  comandaId: number;
  autoPrint?: boolean;
}

export default function ReciboComanda({ comandaId, autoPrint = false }: Props) {
  const [resumos, setResumos] = useState<ComandaResumo[]>([]);
  const [erro, setErro] = useState<string>("");
  const printRef = useRef<HTMLDivElement>(null);

  const loadResumo = async () => {
    try {
      const resp = await api.get(`/comandas/${comandaId}/resumo`);
      setResumos(Array.isArray(resp.data) ? resp.data : []);
    } catch (err) {
      console.error("Erro ao carregar resumo:", err);
      setErro("Erro ao carregar resumo.");
    }
  };

  const handlePrint = useReactToPrint({
    content: () => printRef.current,
    documentTitle: `Recibo-Comanda-${comandaId}`,
    onAfterPrint: () => console.log('✅ Impressão concluída!')
  });

  useEffect(() => {
    loadResumo();
  }, [comandaId]);

  useEffect(() => {
    if (autoPrint && resumos.length > 0) {
      setTimeout(() => {
        handlePrint?.();
      }, 500);
    }
  }, [autoPrint, resumos]);

  if (erro) {
    return <div className="text-red-500">{erro}</div>;
  }

  if (resumos.length === 0) {
    return <p className="text-gray-500">Nenhum resumo disponível para impressão.</p>;
  }

  return (
    <div className="mt-6">
      <div ref={printRef} className="font-mono text-xs bg-white p-4 border w-64 mx-auto">
        <h2 className="text-center font-bold mb-2">RECIBO DE COMANDA</h2>
        <p>Comanda: #{comandaId}</p>
        <p>Data: {new Date(resumos[0].dataFechamento).toLocaleString()}</p>
        <p>Cliente: {resumos[0].nomeCliente || "-"}</p>
        <p>Observações: {resumos[0].observacoes || "-"}</p>
        <hr className="my-2" />
        <p className="font-bold">TOTAL: R$ {resumos[0].total.toFixed(2)}</p>
        <p className="text-center mt-4">*** Obrigado! ***</p>
      </div>

      {!autoPrint && (
        <div className="text-center mt-4">
          <button
            onClick={handlePrint}
            className="bg-black text-white px-4 py-2 rounded hover:bg-gray-800"
          >
            Imprimir Recibo
          </button>
        </div>
      )}
    </div>
  );
}

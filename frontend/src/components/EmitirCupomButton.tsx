// src/components/EmitirCupomButton.tsx
import axios from "axios";
import { Button } from "@/components/ui/button";
import { useModoFiscal } from "@/hooks/useModoFiscal";

interface Props {
  resumoId: number;
}

export function EmitirCupomButton({ resumoId }: Props) {
  const modoFiscal = useModoFiscal();

  const emitirSat = async () => {
    try {
      await axios.post(`/api/comanda-resumo/${resumoId}/emitir-sat`);
      alert("Cupom SAT emitido!");
    } catch (err: any) {
      alert("Erro ao emitir SAT: " + err?.response?.data?.message || "Erro desconhecido");
    }
  };

  const emitirNfce = async () => {
    try {
      await axios.post(`/api/comanda-resumo/${resumoId}/emitir-nfe`);
      alert("NFC-e emitida!");
    } catch (err: any) {
      alert("Erro ao emitir NFC-e: " + err?.response?.data?.message || "Erro desconhecido");
    }
  };

  if (!modoFiscal) return null;

  return (
    <div className="mt-4">
      {modoFiscal === "SAT" && (
        <Button onClick={emitirSat}>Emitir Cupom SAT</Button>
      )}
      {modoFiscal === "NFCE" && (
        <Button onClick={emitirNfce}>Emitir NFC-e</Button>
      )}
    </div>
  );
}

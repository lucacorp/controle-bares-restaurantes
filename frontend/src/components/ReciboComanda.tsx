import {
  useEffect,
  useState,
  useRef,
  forwardRef,
  useImperativeHandle,
} from "react";
import api from "../services/api";
import { useReactToPrint } from "react-to-print";
import html2canvas from "html2canvas";
import jsPDF from "jspdf";

/* ---------- tipos ---------- */
export interface ComandaResumoDTO {
  id: number;
  total: number;
  dataFechamento: string;
  nomeCliente: string;
  observacoes: string;
  numeroCupom?: string;
  chaveSat?: string;
  valorIcms?: number;
  valorPis?: number;
  valorCofins?: number;
  itens: {
    itemNo: number;
    descricao: string;
    quantidade: number;
    precoUnitario: number;
    subtotal: number;
  }[];
}

export interface ReciboComandaHandle {
  refresh: (dto: ComandaResumoDTO) => void;
  savePDF: () => Promise<void>;
}

interface Props {
  comandaId: number;
  autoPrint?: boolean;
}

/* ---------- componente ---------- */
const ReciboComanda = forwardRef<ReciboComandaHandle, Props>(
  ({ comandaId, autoPrint = false }, ref) => {
    const [resumo, setResumo] = useState<ComandaResumoDTO | null>(null);
    const [erro, setErro] = useState("");
    const printRef = useRef<HTMLDivElement>(null);

    /* --------‑‑ expose imperative API -------- */
    useImperativeHandle(ref, () => ({
      refresh: (dto) => setResumo(dto),
      savePDF: async () => {
        if (!printRef.current) return;
        /* captura em alta resolução */
        const canvas = await html2canvas(printRef.current, { scale: 2 });
        const img = canvas.toDataURL("image/png");
        const pdf = new jsPDF({
          orientation: "p",
          unit: "pt",
          format: [canvas.width, canvas.height],
        });
        pdf.addImage(img, "PNG", 0, 0, canvas.width, canvas.height);
        pdf.save(`Cupom-${comandaId}.pdf`);
      },
    }));

    /* -------- carregar resumo -------- */
    useEffect(() => {
      (async () => {
        try {
          const { data } = await api.get<ComandaResumoDTO[]>(
            `/comanda-resumo/comanda/${comandaId}`
          );
          setResumo(data[0] ?? null);
        } catch {
          setErro("Erro ao carregar resumo.");
        }
      })();
    }, [comandaId]);

    /* -------- impressão direta -------- */
    const handlePrint = useReactToPrint({
      content: () => printRef.current,
    });

    useEffect(() => {
      if (autoPrint && resumo) handlePrint();
    }, [autoPrint, resumo]);

    if (erro) return <p className="text-red-500">{erro}</p>;
    if (!resumo) return <p className="text-gray-500 text-sm">Carregando…</p>;

    /* --------‑‑‑ layout -------- */
    return (
      <div className="mt-6">
        <div
          ref={printRef}
          className="font-mono text-xs w-64 mx-auto bg-white p-4 border"
        >
          <h3 className="text-center font-bold">RECIBO DE COMANDA</h3>
          <p>Comanda #{comandaId}</p>
          <p>Data: {new Date(resumo.dataFechamento).toLocaleString()}</p>
          <p>Cliente: {resumo.nomeCliente || "-"}</p>
          <hr className="my-1" />
          {resumo.itens.map((it) => (
            <div key={it.itemNo} className="flex justify-between">
              <span>
                {it.quantidade}× {it.descricao}
              </span>
              <span>R$ {it.subtotal.toFixed(2)}</span>
            </div>
          ))}
          <hr className="my-1" />

          {/* impostos + dados SAT (só se já emitido) */}
          {resumo.valorIcms !== undefined && (
            <>
              <p>ICMS: R$ {resumo.valorIcms.toFixed(2)}</p>
              <p>PIS:  R$ {resumo.valorPis?.toFixed(2)}</p>
              <p>COFINS: R$ {resumo.valorCofins?.toFixed(2)}</p>
              <p>Cupom # {resumo.numeroCupom}</p>
              <p className="break-all">Chave: {resumo.chaveSat}</p>
              <hr className="my-1" />
            </>
          )}

          <p className="font-bold text-right">
            TOTAL R$ {resumo.total.toFixed(2)}
          </p>
          <p className="text-center mt-1">*** Obrigado! ***</p>
        </div>

        {/* botões utilitários */}
        <div className="flex justify-center gap-2 mt-3">
          <button
            onClick={handlePrint}
            className="bg-black text-white px-3 py-1 rounded"
          >
            Imprimir
          </button>
          <button
            onClick={() => ref && (ref as any).current?.savePDF()}
            className="bg-gray-700 text-white px-3 py-1 rounded"
          >
            Salvar PDF
          </button>
        </div>
      </div>
    );
  }
);

export default ReciboComanda;

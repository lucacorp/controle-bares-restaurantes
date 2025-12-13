import { useEffect, useRef, useState } from "react";
import api from "../services/api";
import { useReactToPrint } from "react-to-print";

interface SatItemDTO {
  itemNo: number;
  descricao: string;
  quantidade: number;
  precoUnitario: number;
  subtotal: number;
  valorIcms: number;
  valorPis: number;
  valorCofins: number;
}

interface SatResumoDTO {
  id: number;
  comandaId: number;
  total: number;
  totalBruto: number;
  desconto: number;
  acrescimo: number;
  valorIcms: number;
  valorPis: number;
  valorCofins: number;
  dataFechamento: string;
  nomeCliente: string;
  chaveSat: string;
  numeroCupom: string;
  assinaturaQrcode?: string;   // opcional
  itens: SatItemDTO[];
}

interface Props {
  resumoId: number;
  autoPrint?: boolean;
}

export default function ReciboSAT({ resumoId, autoPrint = false }: Props) {
  const [resumo, setResumo] = useState<SatResumoDTO | null>(null);
  const [erro, setErro] = useState("");
  const ref = useRef<HTMLDivElement>(null);

  const imprimir = useReactToPrint({
    content: () => ref.current,
    documentTitle: `CupomFiscal-${resumoId}`,
  });

  useEffect(() => {
    (async () => {
      try {
        const { data } = await api.get(`/comanda-resumo/${resumoId}`);
        setResumo(data);
      } catch {
        setErro("Erro ao carregar cupom.");
      }
    })();
  }, [resumoId]);

  useEffect(() => {
    if (autoPrint && resumo) imprimir();
  }, [autoPrint, resumo]);

  if (erro) return <p className="text-red-500">{erro}</p>;
  if (!resumo) return <p>Carregando cupom…</p>;

  return (
    <>
      <div ref={ref} className="font-mono text-[10px] w-72 mx-auto p-2 border bg-white">
        <h3 className="text-center font-bold">CFe‑SAT</h3>
        <p className="text-center">Cupom #{resumo.numeroCupom}</p>

        <p>{new Date(resumo.dataFechamento).toLocaleString()}</p>
        {resumo.nomeCliente && <p>Cliente: {resumo.nomeCliente}</p>}
        <hr className="my-1" />

        {resumo.itens.map((it) => (
          <div key={it.itemNo}>
            <div className="flex justify-between">
              <span>{it.itemNo}. {it.descricao}</span>
              <span>R$ {it.subtotal.toFixed(2)}</span>
            </div>
            <div className="flex justify-between pl-2 text-gray-600">
              <span>{it.quantidade}x R$ {it.precoUnitario.toFixed(2)}</span>
              <span>ICMS {it.valorIcms.toFixed(2)}</span>
            </div>
          </div>
        ))}

        <hr className="my-1" />
        <p>Total bruto: R$ {resumo.totalBruto.toFixed(2)}</p>
        {resumo.desconto > 0 && <p>Desconto: -R$ {resumo.desconto.toFixed(2)}</p>}
        {resumo.acrescimo > 0 && <p>Acréscimo: +R$ {resumo.acrescimo.toFixed(2)}</p>}
        <p className="font-bold">TOTAL: R$ {resumo.total.toFixed(2)}</p>

        <hr className="my-1" />
        <p>ICMS R$ {resumo.valorIcms.toFixed(2)}</p>
        <p>PIS  R$ {resumo.valorPis.toFixed(2)}</p>
        <p>COFINS R$ {resumo.valorCofins.toFixed(2)}</p>

        <hr className="my-1" />
        <p className="break-all">{resumo.chaveSat}</p>

        {resumo.assinaturaQrcode && (
          <div className="flex justify-center mt-1">
            <img
              src={`data:image/png;base64,${resumo.assinaturaQrcode}`}
              alt="QR‑Code"
              className="w-28 h-28"
            />
          </div>
        )}
        <p className="text-center mt-1">Consulta: www.fazenda.sp.gov.br</p>
        <p className="text-center mt-1">*** Obrigado! ***</p>
      </div>

      {!autoPrint && (
        <div className="text-center mt-3">
          <button
            onClick={imprimir}
            className="bg-black text-white px-4 py-1 rounded"
          >
            Imprimir
          </button>
        </div>
      )}
    </>
  );
}

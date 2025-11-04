import { Download, FileText } from "lucide-react";

interface Props {
  resumoId: number;
}

export default function CupomFiscalDownloadButtons({ resumoId }: Props) {
  const base = "http://localhost:8080/api/arquivos";

  const abrir = (tipo: "xml" | "pdf") => {
    window.open(`${base}/${tipo}/${resumoId}`, "_blank");
  };

  const baixar = async (tipo: "xml" | "pdf") => {
    const url = `${base}/${tipo}/${resumoId}`;
    const res = await fetch(url);
    const blob = await res.blob();
    const a = document.createElement("a");
    a.href = URL.createObjectURL(blob);
    a.download = tipo === "pdf" ? `DANFE_${resumoId}.pdf` : `NFe_${resumoId}.xml`;
    a.click();
  };

  return (
    <div className="mt-4 flex gap-3">
      <button
        onClick={() => abrir("xml")}
        className="flex items-center gap-2 px-3 py-2 bg-gray-200 rounded hover:bg-gray-300"
      >
        <FileText size={16} />
        Visualizar XML
      </button>

      <button
        onClick={() => baixar("xml")}
        className="flex items-center gap-2 px-3 py-2 bg-gray-200 rounded hover:bg-gray-300"
      >
        <Download size={16} />
        Baixar XML
      </button>

      <button
        onClick={() => abrir("pdf")}
        className="flex items-center gap-2 px-3 py-2 bg-blue-600 text-white rounded hover:bg-blue-700"
      >
        <FileText size={16} />
        Visualizar DANFE
      </button>

      <button
        onClick={() => baixar("pdf")}
        className="flex items-center gap-2 px-3 py-2 bg-blue-600 text-white rounded hover:bg-blue-700"
      >
        <Download size={16} />
        Baixar DANFE
      </button>
    </div>
  );
}

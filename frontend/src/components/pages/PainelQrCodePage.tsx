import { useEffect, useState } from "react";
import axios from "axios";
import QRCode from "react-qr-code";

interface Mesa {
  id: number;
  descricao: string;
}

export default function PainelQrCodePage() {
  const [mesas, setMesas] = useState<Mesa[]>([]);

  // Base fixa para garantir funcionamento do QR Code no celular
  const getBaseUrl = () => {
    return "http://192.168.200.107:5173"; // ⬅️ Coloque aqui o IP do seu PC e a porta do Vite em execução
  };

  useEffect(() => {
    const token = localStorage.getItem("token");

    axios
      .get("/api/mesas", {
        headers: {
          Authorization: `Bearer ${token}`,
        },
      })
      .then((res) => setMesas(res.data))
      .catch((err) => {
        console.error("Erro ao buscar mesas", err);
        setMesas([]);
      });
  }, []);

  const handleImprimir = () => {
    window.print();
  };

  return (
    <div className="p-4">
      <div className="flex justify-between items-center mb-6 print:hidden">
        <h1 className="text-2xl font-semibold">Painel de Mesas com QR Code</h1>
        <button
          onClick={handleImprimir}
          className="border px-4 py-2 rounded hover:bg-gray-100"
        >
          Imprimir ou Salvar como PDF
        </button>
      </div>

      <div className="grid grid-cols-2 md:grid-cols-3 lg:grid-cols-4 gap-6">
        {mesas.map((mesa) => (
          <div
            key={mesa.id}
            className="border rounded p-4 text-center bg-white shadow"
          >
            <p className="font-bold text-lg mb-2">Mesa {mesa.descricao}</p>
            <QRCode
              value={`${getBaseUrl()}/comanda/publica/mesa/${mesa.id}`}
              size={128}
            />
          </div>
        ))}
      </div>
    </div>
  );
}

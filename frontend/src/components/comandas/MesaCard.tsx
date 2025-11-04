import React from "react";

interface Comanda {
  id: number;
  numeroMesa: number;
  status: string;
  criadoEm: string;
}

interface Props {
  comanda: Comanda;
  onAbrir: (mesa: number) => void;
  onExcluir: (id: number) => void;
  onFechar: (id: number) => void;
}

const MesaCard: React.FC<Props> = ({ comanda, onAbrir, onExcluir, onFechar }) => {
  return (
    <div className="border rounded p-4 shadow space-y-2">
      <h2 className="font-semibold">Mesa {comanda.numeroMesa}</h2>
      <p>Status: {comanda.status}</p>
      <p className="text-sm text-gray-500">
        Criado em: {new Date(comanda.criadoEm).toLocaleString()}
      </p>

      <div className="flex gap-2">
        {comanda.status === "ABERTA" ? (
          <button
            onClick={() => onFechar(comanda.id)}
            className="bg-green-500 text-white px-2 py-1 rounded"
          >
            Fechar
          </button>
        ) : (
          <button
            onClick={() => onAbrir(comanda.numeroMesa)}
            className="bg-blue-500 text-white px-2 py-1 rounded"
          >
            Abrir
          </button>
        )}

        <button
          onClick={() => onExcluir(comanda.id)}
          className="bg-red-500 text-white px-2 py-1 rounded"
        >
          Excluir
        </button>

        <a
          href={`/comandas/${comanda.id}`}
          className="bg-gray-500 text-white px-2 py-1 rounded"
        >
          Entrar
        </a>
      </div>
    </div>
  );
};

export default MesaCard;

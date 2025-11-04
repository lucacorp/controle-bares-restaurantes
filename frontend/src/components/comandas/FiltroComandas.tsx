import React from "react";

interface Props {
  filtro: { mesa: string; status: string };
  setFiltro: React.Dispatch<
    React.SetStateAction<{ mesa: string; status: string }>
  >;
}

const FiltroComandas: React.FC<Props> = ({ filtro, setFiltro }) => {
  return (
    <div className="flex gap-2">
      <input
        type="text"
        placeholder="Mesa"
        value={filtro.mesa}
        onChange={(e) => setFiltro((f) => ({ ...f, mesa: e.target.value }))}
        className="border p-2 rounded"
      />

      <select
        value={filtro.status}
        onChange={(e) => setFiltro((f) => ({ ...f, status: e.target.value }))}
        className="border p-2 rounded"
      >
        <option value="">Todos</option>
        <option value="ABERTA">Aberta</option>
        <option value="FECHADA">Fechada</option>
      </select>
    </div>
  );
};

export default FiltroComandas;

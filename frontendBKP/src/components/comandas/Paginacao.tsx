import React from "react";

interface Props {
  pagina: number;
  totalPaginas: number;
  setPagina: (p: number) => void;
}

const Paginacao: React.FC<Props> = ({ pagina, totalPaginas, setPagina }) => {
  return (
    <div className="flex gap-2 items-center">
      <button
        disabled={pagina === 1}
        onClick={() => setPagina(pagina - 1)}
        className="px-3 py-1 border rounded disabled:opacity-50"
      >
        Anterior
      </button>

      <span>
        Página {pagina} de {totalPaginas}
      </span>

      <button
        disabled={pagina === totalPaginas}
        onClick={() => setPagina(pagina + 1)}
        className="px-3 py-1 border rounded disabled:opacity-50"
      >
        Próxima
      </button>
    </div>
  );
};

export default Paginacao;

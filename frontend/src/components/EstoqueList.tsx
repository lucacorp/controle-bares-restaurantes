import { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
import api from "../services/api";

interface EstoqueDTO {
  id: number;
  produtoId: number;
  produtoNome: string;
  saldo: number;
}

export default function EstoqueList() {
  const [estoques, setEstoques] = useState<EstoqueDTO[]>([]);
  const navigate = useNavigate();

  useEffect(() => {
    api
      .get("/estoques")
      .then((response) => setEstoques(response.data))
      .catch((error) =>
        console.error("Erro ao carregar estoques:", error)
      );
  }, []);

  return (
    <div className="p-4">
      <h2 className="text-2xl font-bold mb-4">Estoque</h2>
      <table className="w-full border border-gray-300 rounded-md overflow-hidden">
        <thead className="bg-gray-100">
          <tr>
            <th className="px-4 py-2 text-left">Produto</th>
            <th className="px-4 py-2 text-left">Quantidade</th>
            <th className="px-4 py-2 text-center">Ações</th>
          </tr>
        </thead>
        <tbody>
          {estoques.map((item) => (
            <tr key={item.id} className="border-t">
              <td className="px-4 py-2">
                {item.produtoNome ? (
                  item.produtoNome
                ) : (
                  <span className="text-red-600 italic">
                    Produto não encontrado ou removido.
                  </span>
                )}
              </td>
              <td className="px-4 py-2">{item.saldo}</td>
              <td className="px-4 py-2 space-x-2 text-center">
                <button
					onClick={() => navigate(`/estoque/${item.produtoId}/ajuste`)}
					className="bg-yellow-500 hover:bg-yellow-600 text-white px-3 py-1 rounded-md"
					>
					Ajustar
				</button>

				<button
					onClick={() => navigate(`/estoque/${item.produtoId}/movimentacoes`)}
					className="bg-blue-500 hover:bg-blue-600 text-white px-3 py-1 rounded-md"
					>
				Movimentações
				</button>

              </td>
            </tr>
          ))}
        </tbody>
      </table>
    </div>
  );
}

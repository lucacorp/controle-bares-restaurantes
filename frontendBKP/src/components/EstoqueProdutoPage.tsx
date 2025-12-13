import { NavLink, useLocation, useParams } from "react-router-dom";
import AjusteEstoquePage from "./EstoqueAjustePage";
import MovimentacoesPage from "./MovimentacoesPage";

export default function EstoqueProdutoPage() {
  const { id } = useParams();               // produtoId vindo da rota
  const location = useLocation();           // path atual

  if (!id) return <div>Produto inválido</div>;
  const productId = Number(id);

  // Detecta qual aba está ativa olhando o pathname
  const isAjuste = location.pathname.endsWith("/ajuste");
  const isMov    = location.pathname.endsWith("/movimentacoes");

  return (
    <div className="p-4 space-y-6 max-w-5xl mx-auto">
      <h1 className="text-2xl font-bold">Produto # {productId}</h1>

      {/* Tabs */}
      <div className="space-x-2">
        <NavLink
          to={`/estoque/${productId}/movimentacoes`}
          className={({ isActive }) =>
            `${isActive ? "bg-black text-white" : "border"} px-4 py-2 rounded`
          }
        >
          Movimentações
        </NavLink>
        <NavLink
          to={`/estoque/${productId}/ajuste`}
          className={({ isActive }) =>
            `${isActive ? "bg-black text-white" : "border"} px-4 py-2 rounded`
          }
        >
          Ajustar Estoque
        </NavLink>
      </div>

      {/* Conteúdo da aba */}
      {isMov && <MovimentacoesPage productId={productId} />}
      {isAjuste && <AjusteEstoquePage productId={productId} />}

      {/* fallback caso alguém digite apenas /estoque/:id */}
      {!isMov && !isAjuste && (
        <div className="text-gray-500 mt-6">
          Selecione uma aba para visualizar.
        </div>
      )}
    </div>
  );
}

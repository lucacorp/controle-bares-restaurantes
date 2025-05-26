import { Routes, Route, Navigate, useParams } from "react-router-dom";

import Login from "./components/Login";
import Dashboard from "./components/Dashboard";
import ProductList from "./components/ProductList";
import ProductForm from "./components/ProductForm";
import TableList from "./components/TableList";
import FormMesa from "./components/FormMesa";
import ReceitaList from "./components/ReceitaList";
import ReceitaForm from "./components/ReceitaForm";
import RotaProtegida from "./components/RotaProtegida";
import ItensComandaPage from "./components/ItensComandaPage";
import ComandaResumoPage from "./components/ComandaResumoPage";
import SidebarLayout from "./components/SidebarLayout";
import MesaComandasPage from "./components/MesaComandasPage";

// Wrapper para extrair o comandaId da URL
function ItensComandaWrapper() {
  const { id } = useParams();
  if (!id) return <div>Comanda inválida</div>;
  return <ItensComandaPage comandaId={Number(id)} />;
}

function App() {
  return (
    <Routes>
      {/* Redirecionamento inicial */}
      <Route path="/" element={<Navigate to="/dashboard" />} />

      {/* Login */}
      <Route path="/login" element={<Login />} />

      {/* Layout com sidebar para rotas protegidas */}
      <Route
        element={
          <RotaProtegida>
            <SidebarLayout />
          </RotaProtegida>
        }
      >
        <Route path="/dashboard" element={<Dashboard />} />
        <Route path="/produtos" element={<ProductList />} />
        <Route path="/produtos/novo" element={<ProductForm />} />
        <Route path="/mesas" element={<TableList />} />
        <Route path="/mesas/nova" element={<FormMesa />} />
        <Route path="/mesas/:id" element={<FormMesa />} />

        {/*  Nova rota para múltiplas comandas por mesa */}
        <Route path="/mesas/:mesaId/comandas" element={<MesaComandasPage />} />

        <Route path="/receitas" element={<ReceitaList />} />
        <Route path="/receitas/nova" element={<ReceitaForm />} />
		<Route path="/comandas/resumos" element={<ComandaResumoPage />} />
		<Route path="/comandas/resumos" element={<ComandaResumoPage />} /> 

        {/* Itens da comanda */}
        <Route path="/comandas/:id/itens" element={<ItensComandaWrapper />} />
      </Route>

      {/* Fallback */}
      <Route path="*" element={<Navigate to="/dashboard" />} />
    </Routes>
  );
}

export default App;

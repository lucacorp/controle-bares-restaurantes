import { Routes, Route, Navigate, useParams } from "react-router-dom";

import { useAuth } from "./AuthContext";

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
import SidebarLayout from "./components/SidebarLayout";

function ItensComandaWrapper() {
  const { id } = useParams();
  if (!id) return <div>Comanda inválida</div>;
  return <ItensComandaPage comandaId={Number(id)} />;
}

function App() {
  const { isAuthenticated } = useAuth();

  return (
    <Routes>
      <Route
        path="/"
        element={<Navigate to={isAuthenticated ? "/dashboard" : "/login"} />}
      />

      <Route
        path="/login"
        element={isAuthenticated ? <Navigate to="/dashboard" /> : <Login />}
      />

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
        <Route path="/receitas" element={<ReceitaList />} />
        <Route path="/receitas/nova" element={<ReceitaForm />} />
        <Route path="/comandas/:id/itens" element={<ItensComandaWrapper />} />
      </Route>

      <Route
        path="*"
        element={<Navigate to={isAuthenticated ? "/dashboard" : "/login"} />}
      />
    </Routes>
  );
}

export default App;

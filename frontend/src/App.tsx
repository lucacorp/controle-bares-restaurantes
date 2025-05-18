// src/App.tsx
import {
  Routes,
  Route,
  Navigate,
  useParams,
} from "react-router-dom";
// index.tsx ou main.tsx
import './index.css'; // que contém @tailwind base, components, utilities

import SidebarLayout from "./components/SidebarLayout";
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
import { AuthProvider, useAuth } from "./AuthContext";
import ComandaPage from './components/ComandaPage';

function ItensComandaWrapper() {
  const { id } = useParams();
  if (!id) return <div>Comanda inválida</div>;
  return <ItensComandaPage comandaId={Number(id)} />;
}

function Rotas() {
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

      <Route element={<RotaProtegida><SidebarLayout /></RotaProtegida>}>
        <Route path="/dashboard" element={<Dashboard />} />
        <Route path="/produtos" element={<ProductList />} />
        <Route path="/produtos/novo" element={<ProductForm />} />
        <Route path="/mesas" element={<TableList />} />
        <Route path="/mesas/:id" element={<FormMesa />} />
        <Route path="/receitas" element={<ReceitaList />} />
        <Route path="/receitas/nova" element={<ReceitaForm />} />
        <Route path="/comandas/:id/itens" element={<ItensComandaWrapper />} />
        <Route path="/comanda/:mesaId" element={<ComandaPage />} />
      </Route>

      <Route
        path="*"
        element={<Navigate to={isAuthenticated ? "/dashboard" : "/login"} />}
      />
    </Routes>
  );
}

export default function App() {
  return (
    <AuthProvider>
      <Rotas />
    </AuthProvider>
  );
}

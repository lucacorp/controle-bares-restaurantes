// src/App.tsx
import { BrowserRouter as Router, Routes, Route, Navigate } from "react-router-dom";
import { useEffect, useState } from "react";
import Login from "./components/Login";
import Dashboard from "./components/Dashboard";
import ProductList from "./components/ProductList";
import ProductForm from "./components/ProductForm";
import TableList from "./components/TableList";
import FormMesa from "./components/FormMesa";
import ReceitaList from "./components/ReceitaList";
import ReceitaForm from "./components/ReceitaForm";
import RotaProtegida from "./components/RotaProtegida";

function App() {
  const [isAuthenticated, setIsAuthenticated] = useState(false);

  useEffect(() => {
    const token = localStorage.getItem("token");
    setIsAuthenticated(!!token);
  }, []);

  return (
    <Router>
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
          path="/dashboard"
          element={
            <RotaProtegida>
              <Dashboard />
            </RotaProtegida>
          }
        />
        <Route
          path="/produtos/novo"
          element={
            <RotaProtegida>
              <ProductForm />
            </RotaProtegida>
          }
        />
        <Route
          path="/mesas"
          element={
            <RotaProtegida>
              <TableList />
            </RotaProtegida>
          }
        />
        <Route
          path="/mesas/nova"
          element={
            <RotaProtegida>
              <FormMesa />
            </RotaProtegida>
          }
        />
        <Route
          path="/receitas"
          element={
            <RotaProtegida>
              <ReceitaList />
            </RotaProtegida>
          }
        />
        <Route
          path="/receitas/nova"
          element={
            <RotaProtegida>
              <ReceitaForm />
            </RotaProtegida>
          }
        />
        <Route
          path="*"
          element={<Navigate to={isAuthenticated ? "/dashboard" : "/login"} />}
        />
      </Routes>
    </Router>
  );
}

export default App;

import { BrowserRouter, Routes, Route, Navigate } from "react-router-dom";
import RotaProtegida from "./components/RotaProtegida";

import ReceitaList from "./ReceitaList";
import Login from "./Login";

function App() {
  const isAutenticado = !!localStorage.getItem("token");

  return (
    <BrowserRouter>
      <Routes>
        <Route
          path="/login"
          element={isAutenticado ? <Navigate to="/" replace /> : <Login />}
        />
        <Route
          path="/"
          element={
            <RotaProtegida isAuthenticated={isAutenticado}>
              <ReceitaList />
            </RotaProtegida>
          }
        />
        {/* outras rotas protegidas podem ser adicionadas aqui */}
      </Routes>
    </BrowserRouter>
  );
}

export default App;

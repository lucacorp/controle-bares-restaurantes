// App.tsx (atualizado)
import { Routes, Route, Navigate, useParams } from "react-router-dom";
import { ToastContainer } from 'react-toastify';
import 'react-toastify/dist/ReactToastify.css';

import Login from "./components/Login";
import Dashboard from "./components/Dashboard";
import TableList from "./components/TableList";
import FormMesa from "./components/FormMesa";
import ReceitaList from "./components/ReceitaList";
import ReceitaForm from "./components/ReceitaForm";
import RotaProtegida from "./components/RotaProtegida";
import ItensComandaPage from "./components/ItensComandaPage";
import ComandaResumoPage from "./components/ComandaResumoPage";
import SidebarLayout from "./components/SidebarLayout";
import ProductManager from "./components/ProductManager";
import ConfigPage from "./components/ConfigPage";
import PainelQrCodePage from "./components/pages/PainelQrCodePage";
import ComandaPublicPage from "./components/ComandaPublicPage";
import GarcomPainelPage from "./components/pages/garcom/GarcomPainelPage";
import GarcomComandaPage from "./components/pages/garcom/GarcomComandaPage";
import CozinhaPainelPage from "./components/pages/cozinha/CozinhaPainelPage";
import MesaComandasPage from './components/MesaComandasPage';

import EstoquePage from "./components/EstoquePage";
import EstoqueProdutoPage from "./components/EstoqueProdutoPage";

function ItensComandaWrapper() {
  const { id } = useParams();
  if (!id) return <div>Comanda inválida</div>;
  return <ItensComandaPage comandaId={Number(id)} />;
}

function App() {
  return (
    <>
      <Routes>
        {/* Público */}
        <Route path="/login" element={<Login />} />
        <Route path="/comanda/publica/mesa/:mesaId" element={<ComandaPublicPage />} />
        <Route path="/garcom" element={<GarcomPainelPage />} />
        <Route path="/garcom/comanda/:id" element={<GarcomComandaPage />} />
        <Route path="/cozinha" element={<CozinhaPainelPage />} />

        {/* Protegido */}
        <Route
          element={
            <RotaProtegida>
              <SidebarLayout />
            </RotaProtegida>
          }
        >
          {/* Dashboard e Config */}
          <Route path="/" element={<Navigate to="/dashboard" />} />
          <Route path="/dashboard" element={<Dashboard />} />
          <Route path="/config" element={<ConfigPage />} />

          {/* Produtos */}
          <Route path="/produtos" element={<ProductManager />} />
          <Route path="/produtos/novo" element={<ProductManager />} />

          {/* Estoque */}
          <Route path="/estoque" element={<EstoquePage />} />
          <Route path="/estoque/:id/ajuste" element={<EstoqueProdutoPage />} />
          <Route path="/estoque/:id/movimentacoes" element={<EstoqueProdutoPage />} />

          {/* Mesas */}
          <Route path="/mesas" element={<TableList />} />
          <Route path="/mesas/nova" element={<FormMesa />} />
          <Route path="/mesas/:id" element={<FormMesa />} />
          {/* <Route path="/mesas/:id/comandas" element={<ComandasPorMesa />} /> */}
          <Route path="/comandas/mesa/:mesaId" element={<MesaComandasPage />} />
          <Route path="/painel/qrcodes" element={<PainelQrCodePage />} />

          {/* Receitas */}
          <Route path="/receitas" element={<ReceitaList />} />
          <Route path="/receitas/nova" element={<ReceitaForm />} />

          {/* Comandas */}
          <Route path="/comandas/resumos" element={<ComandaResumoPage />} />
          <Route path="/comandas/:id/itens" element={<ItensComandaWrapper />} />
        </Route>

        {/* Fallback */}
        <Route path="*" element={<Navigate to="/dashboard" />} />
      </Routes>

      <ToastContainer position="top-center" />
    </>
  );
}

export default App;

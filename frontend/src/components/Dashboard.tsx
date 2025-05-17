// src/components/Dashboard.tsx
import { useNavigate } from "react-router-dom";
import { useAuth } from "../AuthContext";

export default function Dashboard() {
  const navigate = useNavigate();
  const { logout } = useAuth();

  const handleLogout = () => {
    logout(); // Remove token do localStorage e atualiza o estado
    navigate("/login"); // Redireciona para login
  };

  return (
    <div className="min-h-screen w-full flex flex-col items-center justify-start bg-gray-100 px-6 py-10">
      <div className="w-full max-w-7xl flex justify-between items-center mb-10">
        <h1 className="text-3xl font-bold">Bem-vindo ao Painel</h1>
        <button
          onClick={handleLogout}
          className="bg-red-500 hover:bg-red-600 text-white px-4 py-2 rounded"
        >
          Sair
        </button>
      </div>

      <div className="grid gap-6 grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 w-full max-w-7xl">
        <button
          onClick={() => navigate('/mesas')}
          className="bg-white p-6 rounded-2xl shadow hover:shadow-lg transition text-left w-full"
        >
          <h3 className="text-xl font-semibold mb-2">🍽️ Mapa de Mesas</h3>
          <p className="text-gray-600">Gerencie as mesas do salão</p>
        </button>

        <button
          onClick={() => navigate('/produtos/novo')}
          className="bg-white p-6 rounded-2xl shadow hover:shadow-lg transition text-left w-full"
        >
          <h3 className="text-xl font-semibold mb-2">📦 Cadastro de Produtos</h3>
          <p className="text-gray-600">Adicione e edite produtos</p>
        </button>

        <button
          onClick={() => navigate('/receitas')}
          className="bg-white p-6 rounded-2xl shadow hover:shadow-lg transition text-left w-full"
        >
          <h3 className="text-xl font-semibold mb-2">🍳 Cadastro de Receitas</h3>
          <p className="text-gray-600">Monte receitas com insumos e adição de custos</p>
        </button>

        <button
          onClick={() => navigate('/fornecedores')}
          className="bg-white p-6 rounded-2xl shadow hover:shadow-lg transition text-left w-full"
        >
          <h3 className="text-xl font-semibold mb-2">🤝 Cadastro de Fornecedores</h3>
          <p className="text-gray-600">Gerencie seus fornecedores</p>
        </button>

        <button
          onClick={() => navigate('/relatorios')}
          className="bg-white p-6 rounded-2xl shadow hover:shadow-lg transition text-left w-full"
        >
          <h3 className="text-xl font-semibold mb-2">📊 Relatórios</h3>
          <p className="text-gray-600">Visualize relatórios e métricas</p>
        </button>
      </div>
    </div>
  );
}

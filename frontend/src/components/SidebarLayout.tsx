import { Outlet, useNavigate } from "react-router-dom";
// index.tsx ou main.tsx


export default function SidebarLayout() {
  const navigate = useNavigate();

  return (
    <div className="flex min-h-screen bg-gray-50">
      {/* Sidebar */}
      <aside className="w-64 bg-white shadow-lg flex flex-col p-6 border-r border-gray-200">
        <h2 className="text-3xl font-bold mb-8 text-blue-700">Painel</h2>

        <nav className="flex flex-col gap-4">
          <SidebarButton onClick={() => navigate('/dashboard')} label="Dashboard" icon="🏠" />
          <SidebarButton onClick={() => navigate('/mesas')} label="Mapa de Mesas" icon="🍽️" />
          <SidebarButton onClick={() => navigate('/produtos/novo')} label="Produtos" icon="📦" />
          <SidebarButton onClick={() => navigate('/receitas')} label="Receitas" icon="🍳" />
          <SidebarButton onClick={() => navigate('/fornecedores')} label="Fornecedores" icon="🤝" />
          <SidebarButton onClick={() => navigate('/relatorios')} label="Relatórios" icon="📊" />
        </nav>

        <button
          onClick={() => {
            localStorage.removeItem("token");
            navigate("/login");
          }}
          className="mt-auto bg-red-500 text-white rounded-lg px-4 py-2 hover:bg-red-600 transition"
        >
          🚪 Sair
        </button>
      </aside>

      {/* Conteúdo principal */}
      <main className="flex-1 p-8 bg-gray-100 overflow-y-auto">
        <Outlet />
      </main>
    </div>
  );
}

function SidebarButton({
  onClick,
  label,
  icon,
}: {
  onClick: () => void;
  label: string;
  icon: string;
}) {
  return (
    <button
      onClick={onClick}
      className="flex items-center gap-3 text-gray-700 text-left px-3 py-2 rounded-md hover:bg-blue-100 hover:text-blue-600 transition"
    >
      <span className="text-xl">{icon}</span>
      <span className="text-base font-medium">{label}</span>
    </button>
  );
}

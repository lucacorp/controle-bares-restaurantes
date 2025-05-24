import { useState, useEffect } from "react";
import { Outlet, useNavigate } from "react-router-dom";
import { ChevronLeft, ChevronRight } from "lucide-react";

export default function SidebarLayout() {
  const navigate = useNavigate();
  const [collapsed, setCollapsed] = useState(false);

  const menuItems = [
    { icon: "🏠", label: "Dashboard", path: "/dashboard" },
    { icon: "🍽️", label: "Mesas", path: "/mesas" },
    { icon: "📦", label: "Produtos", path: "/produtos" },
    { icon: "🍳", label: "Receitas", path: "/receitas" },
    { icon: "📊", label: "Relatórios", path: "/relatorios" },
  ];

  const toggleSidebar = () => setCollapsed(!collapsed);

  // Responsividade: colapsar automaticamente em telas menores que 768px
  useEffect(() => {
    const handleResize = () => {
      if (window.innerWidth < 768) {
        setCollapsed(true);
      } else {
        setCollapsed(false);
      }
    };

    // Chama ao montar
    handleResize();

    // Ouvinte de resize
    window.addEventListener("resize", handleResize);

    // Cleanup
    return () => window.removeEventListener("resize", handleResize);
  }, []);

  return (
    <div className="flex min-h-screen bg-gray-100">
      {/* Sidebar */}
      <aside
        className={`bg-white shadow-md flex flex-col p-4 transition-all duration-300 ${
          collapsed ? "w-20" : "w-64"
        }`}
      >
        <button
          onClick={toggleSidebar}
          className="mb-6 focus:outline-none self-end"
        >
          {collapsed ? <ChevronRight size={24} /> : <ChevronLeft size={24} />}
        </button>

        <nav className="flex flex-col gap-4">
          {menuItems.map((item) => (
            <button
              key={item.path}
              onClick={() => navigate(item.path)}
              className="flex items-center gap-2 hover:text-blue-600 text-left"
            >
              <span>{item.icon}</span>
              {!collapsed && <span>{item.label}</span>}
            </button>
          ))}
        </nav>

        <button
          onClick={() => {
            localStorage.removeItem("token");
            navigate("/login");
          }}
          className={`mt-auto bg-red-500 text-white rounded px-4 py-2 hover:bg-red-600 ${
            collapsed ? "text-xs p-1" : ""
          }`}
        >
          {collapsed ? "⏻" : "Sair"}
        </button>
      </aside>

      {/* Conteúdo principal */}
      <main className="flex-1 p-6">
        <Outlet />
      </main>
    </div>
  );
}

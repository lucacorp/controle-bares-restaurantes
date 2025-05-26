import { useState, useEffect } from "react";
import { Outlet, useNavigate, useLocation } from "react-router-dom";
import { ChevronLeft, ChevronRight } from "lucide-react";

export default function SidebarLayout() {
  const navigate = useNavigate();
  const location = useLocation();
  const [collapsed, setCollapsed] = useState(false);

  const menuItems = [
    { icon: "🏠", label: "Dashboard", path: "/dashboard" },
    { icon: "🍽️", label: "Mesas", path: "/mesas" },
    { icon: "📦", label: "Produtos", path: "/produtos" },
    { icon: "🍳", label: "Receitas", path: "/receitas" },
    { icon: "📊", label: "Relatórios", path: "/relatorios" },
    { icon: "🧾", label: "Resumos", path: "/comandas/resumos" },  // ✅ Novo item
  ];

  const toggleSidebar = () => setCollapsed(!collapsed);

  useEffect(() => {
    const handleResize = () => {
      setCollapsed(window.innerWidth < 768);
    };
    handleResize();
    window.addEventListener("resize", handleResize);
    return () => window.removeEventListener("resize", handleResize);
  }, []);

  return (
    <div className="flex min-h-screen bg-gray-100">
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
              className={`flex items-center gap-2 text-left p-2 rounded hover:bg-gray-200 ${
                location.pathname.startsWith(item.path)
                  ? "bg-gray-200 font-bold"
                  : ""
              }`}
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

      <main className="flex-1 p-6">
        <Outlet />
      </main>
    </div>
  );
}

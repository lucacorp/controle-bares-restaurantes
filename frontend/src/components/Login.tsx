import { useState } from "react";
import { useNavigate } from "react-router-dom";

export default function Login() {
  const [login, setLogin] = useState("");
  const [senha, setSenha] = useState("");
  const navigate = useNavigate();

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();

    try {
      const response = await fetch("http://localhost:8080/api/auth/login", {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({ login, senha }),
      });

      if (response.ok) {
        const data = await response.json();
        if (data?.token) {
          localStorage.setItem("token", data.token);
          navigate("/"); // redireciona para a rota protegida
        } else {
          alert("Token inválido ou ausente.");
        }
      } else {
        alert("Login ou senha inválidos.");
      }
    } catch (error) {
      alert("Erro ao conectar com o servidor.");
      console.error(error);
    }
  };

  return (
    <form
      onSubmit={handleSubmit}
      className="bg-white p-8 rounded-2xl shadow-md w-full max-w-sm"
    >
      <h2 className="text-2xl font-bold mb-6 text-center text-gray-700">Login</h2>
      <input
        type="text"
        placeholder="Usuário"
        value={login}
        onChange={(e) => setLogin(e.target.value)}
        className="w-full mb-4 p-2 border border-gray-300 rounded"
      />
      <input
        type="password"
        placeholder="Senha"
        value={senha}
        onChange={(e) => setSenha(e.target.value)}
        className="w-full mb-6 p-2 border border-gray-300 rounded"
      />
      <button
        type="submit"
        className="w-full bg-blue-600 text-white py-2 rounded hover:bg-blue-700 transition"
      >
        Entrar
      </button>
    </form>
  );
}

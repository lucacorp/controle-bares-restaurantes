// src/pages/garcom/LoginPage.tsx
import { useState } from "react";
import axios from "axios";
import { useNavigate } from "react-router-dom";

export default function GarcomLoginPage() {
  const [email, setEmail] = useState("");
  const [senha, setSenha] = useState("");
  const [erro, setErro] = useState("");
  const navigate = useNavigate();

  const logar = () => {
    axios.post("http://192.168.200.107:8080/api/auth/login", { email, senha })
      .then(res => {
        localStorage.setItem("token", res.data.token);
        localStorage.setItem("usuario", JSON.stringify(res.data.usuario));
        navigate("/garcom/painel");
      })
      .catch(() => setErro("Credenciais inválidas"));
  };

  return (
    <div className="flex flex-col items-center justify-center h-screen p-4">
      <div className="bg-white shadow rounded p-6 w-full max-w-sm">
        <h1 className="text-xl font-bold mb-4 text-center">Login do Garçom</h1>

        <input
          className="border p-2 w-full mb-3 rounded"
          type="email"
          placeholder="E-mail"
          value={email}
          onChange={e => setEmail(e.target.value)}
        />

        <input
          className="border p-2 w-full mb-4 rounded"
          type="password"
          placeholder="Senha"
          value={senha}
          onChange={e => setSenha(e.target.value)}
        />

        {erro && <p className="text-red-600 text-sm mb-2 text-center">{erro}</p>}

        <button
          className="bg-blue-600 text-white py-2 px-4 w-full rounded hover:bg-blue-700"
          onClick={logar}
        >
          Entrar
        </button>
      </div>
    </div>
  );
}

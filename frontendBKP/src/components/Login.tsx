import { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { useAuth } from '../AuthContext';

export default function Login() {
  const [email, setEmail] = useState('');
  const [senha, setSenha] = useState('');
  const [nome, setNome] = useState('');
  const [mostrarRegistro, setMostrarRegistro] = useState(false);
  const navigate = useNavigate();
  const { login } = useAuth();

  const handleLogin = async (e: React.FormEvent) => {
    e.preventDefault();

    if (!email.trim() || !senha.trim()) {
      alert('Preencha todos os campos.');
      return;
    }

    try {
      const response = await fetch('http://localhost:8080/api/auth/login', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ email: email.trim(), senha }),
      });

      if (!response.ok) {
        alert('Login ou senha inválidos.');
        return;
      }

      const data = await response.json();
      if (data?.token) {
        login(data.token);
        navigate('/dashboard');
      } else {
        alert('Token inválido ou ausente.');
      }
    } catch (error) {
      console.error('Erro ao conectar com o servidor:', error);
      alert('Erro ao conectar com o servidor.');
    }
  };

  const handleRegistro = async (e: React.FormEvent) => {
    e.preventDefault();

    try {
      const response = await fetch('http://localhost:8080/api/auth/register', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ nome, email, senha }),
      });

      if (response.ok) {
        alert('Usuário registrado com sucesso. Faça login.');
        setMostrarRegistro(false);
      } else {
        alert('Erro ao registrar usuário.');
      }
    } catch (error) {
      console.error('Erro de conexão:', error);
      alert('Erro de conexão.');
    }
  };

  return (
    <div className="flex justify-center items-center min-h-screen bg-gray-100">
      <form
        onSubmit={mostrarRegistro ? handleRegistro : handleLogin}
        className="bg-white p-8 rounded-2xl shadow-md w-full max-w-sm"
      >
        <h2 className="text-2xl font-bold mb-6 text-center text-gray-700">
          {mostrarRegistro ? 'Registrar' : 'Login'}
        </h2>

        {mostrarRegistro && (
          <input
            type="text"
            placeholder="Nome"
            value={nome}
            onChange={(e) => setNome(e.target.value)}
            className="w-full mb-4 p-2 border border-gray-300 rounded"
          />
        )}

        <input
          type="text"
          placeholder="E-mail"
          value={email}
          onChange={(e) => setEmail(e.target.value)}
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
          {mostrarRegistro ? 'Registrar' : 'Entrar'}
        </button>

        <p
          onClick={() => setMostrarRegistro(!mostrarRegistro)}
          className="mt-4 text-sm text-center text-blue-600 cursor-pointer hover:underline"
        >
          {mostrarRegistro
            ? 'Já tem uma conta? Fazer login'
            : 'Não tem conta? Registrar'}
        </p>
      </form>
    </div>
  );
}

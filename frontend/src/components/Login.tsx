import { useState } from 'react'

interface LoginProps {
  onLogin: () => void
}

export default function Login({ onLogin }: LoginProps) {
  const [nome, setNome] = useState('')
  const [senha, setSenha] = useState('')

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault()

    const response = await fetch('http://localhost:8080/api/usuarios/login', {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ nome, senha })
    })

    if (response.ok) {
      onLogin()
    } else {
      alert('Usuário ou senha inválidos')
    }
  }

  return (
    <form
      onSubmit={handleSubmit}
      className="bg-white p-8 rounded-2xl shadow-md w-full max-w-sm"
    >
      <h2 className="text-2xl font-bold mb-6 text-center text-gray-700">Login</h2>
      <input
        type="text"
        placeholder="Nome de usuário"
        value={nome}
        onChange={(e) => setNome(e.target.value)}
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
  )
}

import { useState } from 'react'
import './App.css'
import TableList from './components/TableList'
import Login from './components/Login'

function App() {
  const [logado, setLogado] = useState(false)

  const handleLogin = () => {
    setLogado(true)
  }

  const handleLogout = () => {
    setLogado(false)
  }

  return (
    <div className="min-h-screen bg-gray-100 py-10 px-4">
      {logado ? (
        <div>
          <div className="flex justify-end mb-4">
            <button
              onClick={handleLogout}
              className="px-4 py-2 bg-red-600 text-white rounded hover:bg-red-700 transition"
            >
              Sair
            </button>
          </div>
          <TableList />
        </div>
      ) : (
        <Login onLogin={handleLogin} />
      )}
    </div>
  )
}

export default App

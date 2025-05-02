// src/components/TableList.tsx
import { useEffect, useState } from 'react'
import { Pencil, Trash2, Plus } from 'lucide-react'
import { useNavigate } from 'react-router-dom'

type Mesa = {
  id: number
  nome: string
  status: string
}

const statusColors: Record<string, string> = {
  'Disponível': 'text-green-600',
  'Ocupada': 'text-red-600',
  'Reservada': 'text-yellow-600'
}

export default function TableList() {
  const [mesas, setMesas] = useState<Mesa[]>([])
  const navigate = useNavigate()

  useEffect(() => {
    fetch('http://localhost:8080/api/mesas')
      .then(res => res.json())
      .then(data => setMesas(data))
      .catch(err => {
        console.error('Erro ao carregar mesas:', err)
        alert('Erro ao carregar mesas.')
      })
  }, [])

  const handleDelete = async (id: number) => {
    if (confirm('Deseja remover esta mesa?')) {
      try {
        await fetch(`http://localhost:8080/api/mesas/${id}`, { method: 'DELETE' })
        setMesas(prev => prev.filter(m => m.id !== id))
      } catch (err) {
        console.error(err)
        alert('Erro ao remover mesa.')
      }
    }
  }

  return (
    <div className="mt-10 px-4 max-w-5xl mx-auto">
      <div className="flex items-center justify-between mb-6">
        <h2 className="text-2xl font-bold text-gray-800">Lista de Mesas</h2>
        <button
          onClick={() => navigate('/mesas/novo')}
          className="flex items-center gap-2 px-4 py-2 bg-green-600 text-white rounded hover:bg-green-700"
        >
          <Plus size={18} /> Nova Mesa
        </button>
      </div>

      <div className="grid grid-cols-1 sm:grid-cols-2 md:grid-cols-3 gap-6">
        {mesas.map(table => (
          <div key={table.id} className="bg-white shadow-lg rounded-2xl p-6 flex flex-col items-start gap-3">
            <h3 className="text-xl font-semibold text-gray-700">{table.nome}</h3>
            <p className={`text-sm font-medium ${statusColors[table.status]}`}>
              Status: {table.status}
            </p>
            <div className="mt-4 flex gap-2">
              <button
                onClick={() => navigate(`/mesas/editar/${table.id}`)}
                className="flex items-center gap-1 px-3 py-1.5 bg-blue-600 text-white rounded hover:bg-blue-700 transition"
              >
                <Pencil size={16} /> Editar
              </button>
              <button
                onClick={() => handleDelete(table.id)}
                className="flex items-center gap-1 px-3 py-1.5 border border-red-500 text-red-500 rounded hover:bg-red-100 transition"
              >
                <Trash2 size={16} /> Remover
              </button>
            </div>
          </div>
        ))}
      </div>
    </div>
  )
}

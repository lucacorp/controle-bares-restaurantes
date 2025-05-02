// src/components/FormMesa.tsx
import { useEffect, useState } from 'react'
import { useNavigate, useParams } from 'react-router-dom'

const statusOptions = ['Disponível', 'Ocupada', 'Reservada']

type Mesa = {
  id?: number
  nome: string
  status: string
}

export default function FormMesa() {
  const [mesa, setMesa] = useState<Mesa>({ nome: '', status: 'Disponível' })
  const { id } = useParams()
  const navigate = useNavigate()

  useEffect(() => {
    if (id) {
      fetch(`http://localhost:8080/api/mesas/${id}`)
        .then(res => res.json())
        .then(data => setMesa(data))
        .catch(err => {
          console.error('Erro ao carregar mesa:', err)
          alert('Erro ao carregar dados da mesa.')
        })
    }
  }, [id])

  const handleChange = (e: React.ChangeEvent<HTMLInputElement | HTMLSelectElement>) => {
    const { name, value } = e.target
    setMesa(prev => ({ ...prev, [name]: value }))
  }

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault()

    try {
      const method = id ? 'PUT' : 'POST'
      const url = id
        ? `http://localhost:8080/api/mesas/${id}`
        : `http://localhost:8080/api/mesas`

      const response = await fetch(url, {
        method,
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(mesa)
      })

      if (!response.ok) throw new Error('Erro ao salvar')

      alert('Mesa salva com sucesso!')
      navigate('/mesas')
    } catch (error) {
      console.error(error)
      alert('Erro ao salvar a mesa.')
    }
  }

  return (
    <div className="max-w-xl mx-auto mt-10 bg-white shadow-md rounded-xl p-6">
      <h2 className="text-2xl font-bold mb-4 text-center text-gray-800">
        {id ? 'Editar Mesa' : 'Nova Mesa'}
      </h2>

      <form onSubmit={handleSubmit} className="space-y-4">
        <div>
          <label className="block font-medium mb-1">Nome da Mesa</label>
          <input
            type="text"
            name="nome"
            value={mesa.nome}
            onChange={handleChange}
            required
            className="w-full border border-gray-300 rounded px-3 py-2"
          />
        </div>

        <div>
          <label className="block font-medium mb-1">Status</label>
          <select
            name="status"
            value={mesa.status}
            onChange={handleChange}
            className="w-full border border-gray-300 rounded px-3 py-2"
          >
            {statusOptions.map(status => (
              <option key={status} value={status}>
                {status}
              </option>
            ))}
          </select>
        </div>

        <div className="flex justify-end gap-3 mt-4">
          <button
            type="button"
            onClick={() => navigate('/mesas')}
            className="px-4 py-2 border border-gray-400 rounded hover:bg-gray-100"
          >
            Cancelar
          </button>
          <button
            type="submit"
            className="px-4 py-2 bg-blue-600 text-white rounded hover:bg-blue-700"
          >
            Salvar
          </button>
        </div>
      </form>
    </div>
  )
}

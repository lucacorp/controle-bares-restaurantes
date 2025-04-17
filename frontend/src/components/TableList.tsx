import { Pencil, Trash2 } from 'lucide-react'

const tables = [
  { id: 1, name: 'Mesa 1', status: 'Disponível' },
  { id: 2, name: 'Mesa 2', status: 'Ocupada' }, 
  { id: 3, name: 'Mesa 3', status: 'Reservada' }
]


const statusColors: Record<string, string> = {
  'Disponível': 'text-green-600',
  'Ocupada': 'text-red-600',
  'Reservada': 'text-yellow-600'
}

export default function TableList() {
  return (
    <div className="mt-10 px-4 max-w-5xl mx-auto">
      <h2 className="text-2xl font-bold mb-6 text-center text-gray-800">Lista de Mesas</h2>
      
      <div className="grid grid-cols-1 sm:grid-cols-2 md:grid-cols-3 gap-6">
        {tables.map(table => (
          <div key={table.id} className="bg-white shadow-lg rounded-2xl p-6 flex flex-col items-start gap-3">
            <h3 className="text-xl font-semibold text-gray-700">{table.name}</h3>
            <p className={`text-sm font-medium ${statusColors[table.status]}`}>
              Status: {table.status}
            </p>
            <div className="mt-4 flex gap-2">
              <button className="flex items-center gap-1 px-3 py-1.5 bg-blue-600 text-white rounded hover:bg-blue-700 transition">
                <Pencil size={16} /> Editar
              </button>
              <button className="flex items-center gap-1 px-3 py-1.5 border border-red-500 text-red-500 rounded hover:bg-red-100 transition">
                <Trash2 size={16} /> Remover
              </button>
            </div>
          </div>
        ))}
      </div>
    </div>
  )
}

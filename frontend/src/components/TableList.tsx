import { useEffect, useState } from 'react';
import { Pencil, Trash2, Plus, UtensilsCrossed } from 'lucide-react';
import { useNavigate } from 'react-router-dom';
import api from '../services/api';

type Mesa = {
  id: number;
  descricao: string;
  ocupada: boolean;
};

export default function TableList() {
  const [mesas, setMesas] = useState<Mesa[]>([]);
  const navigate = useNavigate();

  useEffect(() => {
    api.get('/mesas')
      .then(res => setMesas(res.data))
      .catch(err => {
        console.error('Erro ao carregar mesas:', err);
        alert('Erro ao carregar mesas. Faça login novamente.');
      });
  }, []);

  const handleDelete = async (id: number) => {
    if (confirm('Deseja remover esta mesa?')) {
      try {
        await api.delete(`/mesas/${id}`);
        setMesas(prev => prev.filter(m => m.id !== id));
      } catch (err) {
        console.error(err);
        alert('Erro ao remover mesa.');
      }
    }
  };

  const handleAbrirComanda = (mesaId: number) => {
    navigate(`/comanda/${mesaId}`);
  };

  return (
    <div className="mt-10 px-4 max-w-5xl mx-auto">
      <div className="flex items-center justify-between mb-6">
        <h2 className="text-2xl font-bold text-gray-800">Mapa de Mesas</h2>
        <button
          onClick={() => navigate('/mesas/nova')}
          className="flex items-center gap-2 px-4 py-2 bg-green-600 text-white rounded hover:bg-green-700"
        >
          <Plus size={18} /> Nova Mesa
        </button>
      </div>

      <div className="grid grid-cols-1 sm:grid-cols-2 md:grid-cols-3 gap-6">
        {mesas.map(table => (
          <div key={table.id} className="bg-white shadow-lg rounded-2xl p-6 flex flex-col items-start gap-3">
            <h3 className="text-xl font-semibold text-gray-700">{table.descricao}</h3>
            <p className={`text-sm font-medium ${table.ocupada ? 'text-red-600' : 'text-green-600'}`}>
              Status: {table.ocupada ? 'Ocupada' : 'Disponível'}
            </p>

            <div className="mt-4 grid grid-cols-3 gap-2 w-full">
              <button
                onClick={() => navigate(`/mesas/${table.id}`)}
                className="flex items-center justify-center gap-1 px-3 py-1.5 bg-blue-600 text-white rounded hover:bg-blue-700 transition"
              >
                <Pencil size={16} /> Editar
              </button>

              <button
                onClick={() => handleDelete(table.id)}
                className="flex items-center justify-center gap-1 px-3 py-1.5 border border-red-500 text-red-500 rounded hover:bg-red-100 transition"
              >
                <Trash2 size={16} /> Remover
              </button>

              <button
                onClick={() => handleAbrirComanda(table.id)}
                className="flex items-center justify-center gap-1 px-3 py-1.5 bg-purple-600 text-white rounded hover:bg-purple-700 transition"
              >
                <UtensilsCrossed size={16} />
                {table.ocupada ? 'Ver Comanda' : 'Abrir Comanda'}
              </button>
            </div>
          </div>
        ))}
      </div>
    </div>
  );
}

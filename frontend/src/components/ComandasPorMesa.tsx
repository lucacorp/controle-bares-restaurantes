import { useEffect, useState } from 'react';
import { Trash2, NotebookPen } from 'lucide-react';
import { useParams, useNavigate } from 'react-router-dom';
import api from '../services/api';

type Comanda = {
  id: number;
  status: 'ABERTA' | 'FECHADA' | string;
  dataAbertura?: string;
  dataFechamento?: string;
};

export default function ComandasPorMesa() {
  const { id: mesaId } = useParams<{ id: string }>();
  const navigate = useNavigate();
  const [comandas, setComandas] = useState<Comanda[]>([]);

  useEffect(() => {
    if (mesaId) carregarComandas();
  }, [mesaId]);

  const carregarComandas = async () => {
    try {
      const res = await api.get(`/comandas/mesa/${mesaId}`);
      setComandas(res.data);
    } catch (err) {
      console.error('Erro ao carregar comandas:', err);
      alert('Erro ao carregar comandas.');
    }
  };

  const criarNovaComanda = async () => {
    try {
      await api.post('/comandas', { mesaId });
      alert('Nova comanda criada com sucesso!');
      carregarComandas();
    } catch (err) {
      console.error('Erro ao criar comanda:', err);
      alert('Erro ao criar comanda.');
    }
  };

  const excluirComanda = async (comandaId: number) => {
    if (confirm('Deseja excluir esta comanda?')) {
      try {
        await api.delete(`/comandas/${comandaId}`);
        setComandas(prev => prev.filter(c => c.id !== comandaId));
        alert('Comanda excluída com sucesso!');
      } catch (err) {
        console.error('Erro ao excluir comanda:', err);
        alert('Erro ao excluir comanda.');
      }
    }
  };

  return (
    <div className="mt-10 px-4 max-w-4xl mx-auto">
      <h2 className="text-2xl font-bold mb-6">Comandas da Mesa #{mesaId}</h2>

      <button
        onClick={() => navigate('/mesas')}
        className="mb-4 inline-block px-4 py-2 bg-gray-300 rounded hover:bg-gray-400"
      >
        Voltar para Mesas
      </button>

      <button
        onClick={criarNovaComanda}
        className="mb-4 ml-4 inline-block px-4 py-2 bg-green-600 text-white rounded hover:bg-green-700"
      >
        Nova Comanda
      </button>

      {comandas.length === 0 ? (
        <p className="text-center text-gray-500">Nenhuma comanda encontrada.</p>
      ) : (
        <table className="w-full border-collapse">
          <thead>
            <tr className="bg-gray-100">
              <th className="p-2 border">ID</th>
              <th className="p-2 border">Status</th>
              <th className="p-2 border">Abertura</th>
              <th className="p-2 border">Fechamento</th>
              <th className="p-2 border">Ação</th>
            </tr>
          </thead>
          <tbody>
            {comandas.map(comanda => (
              <tr key={comanda.id} className="hover:bg-gray-50">
                <td className="p-2 border">#{comanda.id}</td>
                <td className="p-2 border">{comanda.status}</td>
                <td className="p-2 border">{comanda.dataAbertura || '-'}</td>
                <td className="p-2 border">{comanda.dataFechamento || '-'}</td>
                <td className="p-2 border flex gap-2">
                  <button
                    onClick={() => navigate(`/comandas/${comanda.id}/itens`)}
                    className="text-blue-500 hover:text-blue-700"
                    title="Lançar Itens"
                  >
                    <NotebookPen size={16} />
                  </button>
                  <button
                    onClick={() => excluirComanda(comanda.id)}
                    className="text-red-500 hover:text-red-700"
                    title="Excluir comanda"
                  >
                    <Trash2 size={16} />
                  </button>
                </td>
              </tr>
            ))}
          </tbody>
        </table>
      )}
    </div>
  );
}

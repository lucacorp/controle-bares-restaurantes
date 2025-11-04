import { useEffect, useState } from 'react';
import { Trash2, Plus, List, NotebookPen } from 'lucide-react';
import { useNavigate } from 'react-router-dom';
import api from '../services/api';
import ModalFinalizarMesa from './ModalFinalizarMesa';
import ModalLancarItem from './ModalLancarItem';  // ✅ IMPORTA O MODAL

type Mesa = {
  id: number;
  descricao: string;
  ocupada: boolean;
  status?: 'LIVRE' | 'OCUPADA' | 'FECHADA' | string;
};

export default function TableList() {
  const [mesas, setMesas] = useState<Mesa[]>([]);
  const [mesaParaFinalizar, setMesaParaFinalizar] = useState<number | null>(null);
  const [modalLancarOpen, setModalLancarOpen] = useState(false);
  const [comandaSelecionada, setComandaSelecionada] = useState<number | null>(null);
  const navigate = useNavigate();

  useEffect(() => {
    carregarMesas();
  }, []);

  const carregarMesas = async () => {
    try {
      const res = await api.get('/mesas');
      setMesas(res.data);
    } catch (err) {
      console.error('Erro ao carregar mesas:', err);
      alert('Erro ao carregar mesas. Faça login novamente ou verifique a conexão.');
    }
  };

  const handleDelete = async (id: number) => {
    if (confirm('Deseja remover esta mesa?')) {
      try {
        await api.delete(`/mesas/${id}`);
        setMesas(prev => prev.filter(m => m.id !== id));
        alert('Mesa removida com sucesso!');
      } catch (err: any) {
        console.error('Erro ao remover mesa:', err);
        if (err.response?.status === 409) {
          alert('Não é possível remover a mesa, pois há comandas associadas.');
        } else {
          alert('Erro ao remover mesa. Tente novamente mais tarde.');
        }
      }
    }
  };

  const finalizarMesa = async (mesaId: number, dividirPor: number | null) => {
    try {
      const resp = await api.post(`/mesas/${mesaId}/finalizar`, null, {
        params: { dividirPor }
      });

      const { total, valorPorPessoa, message } = resp.data;

      alert(`✅ ${message}\nTotal: R$ ${Number(total).toFixed(2)}\nValor por pessoa: R$ ${Number(valorPorPessoa).toFixed(2)}`);

      setMesaParaFinalizar(null);
      await carregarMesas();
    } catch (err: any) {
      console.error('Erro ao finalizar mesa:', err);
      alert('Erro ao finalizar mesa. Tente novamente.');
    }
  };

  const handleLancarItens = () => {
    setModalLancarOpen(true);
  };

  const confirmarLancarItens = (comandaId: number) => {
    setModalLancarOpen(false);
    setComandaSelecionada(null);
    navigate(`/comandas/${comandaId}/itens`);
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

      {mesas.length === 0 ? (
        <p className="text-center text-gray-500">Nenhuma mesa cadastrada.</p>
      ) : (
        <div className="grid grid-cols-1 sm:grid-cols-2 md:grid-cols-3 gap-6">
          {mesas.map(table => (
            <div key={table.id} className="bg-white shadow-lg rounded-2xl p-6 flex flex-col items-start gap-3">
              <h3 className="text-xl font-semibold text-gray-700">{table.descricao}</h3>
              <p className={`text-sm font-medium ${table.status === 'OCUPADA' ? 'text-red-600' : 'text-green-600'}`}>
                Status: {table.status ?? 'Indefinido'}
              </p>

              <div className="mt-4 grid grid-cols-2 gap-2 w-full">
                <button
                  onClick={() => setMesaParaFinalizar(table.id)}
                  className="bg-green-600 text-white px-4 py-2 rounded hover:bg-green-700"
                >
                  Finalizar Mesa
                </button>

                <button
                  onClick={() => handleDelete(table.id)}
                  className="flex items-center justify-center gap-1 px-3 py-1.5 border border-red-500 text-red-500 rounded hover:bg-red-100 transition"
                >
                  <Trash2 size={16} /> Remover
                </button>

                <button
                 // onClick={() => navigate(`/mesas/${table.id}/comandas`)}
                  onClick={() => navigate(`/comandas/mesa/${table.id}`)}

				  className="flex items-center justify-center gap-1 px-3 py-1.5 bg-purple-600 text-white rounded hover:bg-purple-700 transition"
                >
                  <List size={16} /> Ver Comandas
                </button>

                <button
                  onClick={handleLancarItens}
                  className="flex items-center justify-center gap-1 px-3 py-1.5 bg-blue-600 text-white rounded hover:bg-blue-700 transition"
                >
                  <NotebookPen size={16} /> Lançar Itens
                </button>
              </div>
            </div>
          ))}
        </div>
      )}

      <ModalFinalizarMesa
        isOpen={mesaParaFinalizar !== null}
        onClose={() => setMesaParaFinalizar(null)}
        onConfirm={(dividirPor) => {
          if (mesaParaFinalizar !== null) {
            finalizarMesa(mesaParaFinalizar, dividirPor);
          }
        }}
      />

      <ModalLancarItem
        isOpen={modalLancarOpen}
        onClose={() => setModalLancarOpen(false)}
        onConfirm={confirmarLancarItens}
      />
    </div>
  );
}

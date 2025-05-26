import { useEffect, useState } from 'react';
import api from '../services/api';

interface ComandaResumo {
  id: number;
  comandaId: number;
  total: number;
  dataFechamento: string;
  nomeCliente: string;
  observacoes: string;
}

interface Props {
  comandaId: number;
}

export default function ComandaResumoPage({ comandaId }: Props) {
  const [resumos, setResumos] = useState<ComandaResumo[]>([]);

  const loadResumos = async () => {
    try {
      const resp = await api.get(`/comandas/${comandaId}/resumo`);
      setResumos(Array.isArray(resp.data) ? resp.data : []);
    } catch (err) {
      console.error('Erro ao carregar resumos:', err);
    }
  };

  useEffect(() => {
    loadResumos();
  }, [comandaId]);

  return (
    <div className="p-6 bg-white rounded-lg shadow-lg max-w-4xl mx-auto">
      <h2 className="text-2xl font-bold text-gray-700 mb-4">
        Resumo da Comanda #{comandaId}
      </h2>

      {resumos.length === 0 ? (
        <p className="text-gray-500">Nenhum resumo encontrado.</p>
      ) : (
        <table className="w-full border-collapse">
          <thead>
            <tr className="bg-gray-100 text-left">
              <th className="p-2">Total</th>
              <th className="p-2">Data Fechamento</th>
              <th className="p-2">Cliente</th>
              <th className="p-2">Observações</th>
            </tr>
          </thead>
          <tbody>
            {resumos.map(resumo => (
              <tr key={resumo.id} className="border-b hover:bg-gray-50">
                <td className="p-2">R$ {resumo.total.toFixed(2)}</td>
                <td className="p-2">{new Date(resumo.dataFechamento).toLocaleString()}</td>
                <td className="p-2">{resumo.nomeCliente || '-'}</td>
                <td className="p-2">{resumo.observacoes || '-'}</td>
              </tr>
            ))}
          </tbody>
        </table>
      )}
    </div>
  );
}

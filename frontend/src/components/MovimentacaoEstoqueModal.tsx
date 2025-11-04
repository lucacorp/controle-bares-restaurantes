import { useState } from 'react';
import api from '../services/api';

export default function MovimentacaoEstoqueModal({ produtoId, onClose }: { produtoId: number; onClose: () => void }) {
  const [quantidade, setQuantidade] = useState<number>(0);
  const [tipo, setTipo] = useState<string>('ENTRADA');

  const handleSubmit = async (e: any) => {
    e.preventDefault();
    await api.post('/movimentacoes', { produtoId, tipo, quantidade });
    onClose();
  };

  return (
    <div className="fixed inset-0 bg-black bg-opacity-30 flex items-center justify-center">
      <div className="bg-white p-6 rounded shadow">
        <h2 className="text-xl font-bold mb-4">Movimentar Estoque</h2>
        <form onSubmit={handleSubmit}>
          <label>Tipo:</label>
          <select value={tipo} onChange={(e) => setTipo(e.target.value)} className="border p-2 mb-2 w-full">
            <option value="ENTRADA">Entrada</option>
            <option value="SAIDA">Saída</option>
          </select>

          <label>Quantidade:</label>
          <input
            type="number"
            value={quantidade}
            onChange={(e) => setQuantidade(Number(e.target.value))}
            className="border p-2 mb-4 w-full"
          />

          <div className="flex justify-end gap-2">
            <button onClick={onClose} type="button" className="px-4 py-2 border rounded">
              Cancelar
            </button>
            <button type="submit" className="px-4 py-2 bg-green-600 text-white rounded">
              Confirmar
            </button>
          </div>
        </form>
      </div>
    </div>
  );
}

// components/ComandasList.tsx
import React, { useEffect, useState } from 'react';
import { listarComandas, fecharComanda } from '../services/comandaService';
import { ComandaDTO } from '../types/ComandaDTO';

export default function ComandasList() {
  const [comandas, setComandas] = useState<ComandaDTO[]>([]);

  useEffect(() => {
    listarComandas().then(setComandas);
  }, []);

  const handleFechar = async (id: number) => {
    await fecharComanda(id);
    setComandas(await listarComandas());
  };

  return (
    <div>
      <h2 className="text-xl mb-4">Comandas</h2>
      <ul className="space-y-2">
        {comandas.map((c) => (
          <li key={c.id} className="border p-2 rounded shadow">
            <p>Mesa: {c.mesaId}</p>
            <p>Status: {c.status}</p>
            <p>Abertura: {c.dataAbertura}</p>
            <p>Fechamento: {c.dataFechamento || '—'}</p>
            {c.status === 'ABERTA' && (
              <button onClick={() => handleFechar(c.id!)} className="mt-2 px-4 py-1 bg-red-500 text-white rounded">
                Fechar
              </button>
            )}
          </li>
        ))}
      </ul>
    </div>
  );
}

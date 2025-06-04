import { useState } from 'react';

interface ModalLancarItemProps {
  isOpen: boolean;
  onClose: () => void;
  onConfirm: (comandaId: number) => void;
}

export default function ModalLancarItem({ isOpen, onClose, onConfirm }: ModalLancarItemProps) {
  const [comandaId, setComandaId] = useState<number | null>(null);

  if (!isOpen) return null;

  return (
    <div className="fixed inset-0 bg-black bg-opacity-30 flex justify-center items-center">
      <div className="bg-white p-6 rounded shadow-md">
        <h2 className="text-xl font-bold mb-4">Lançar Itens</h2>

        <label className="block mb-2">Número da Comanda</label>
        <input
          type="number"
          value={comandaId ?? ''}
          onChange={(e) => setComandaId(Number(e.target.value))}
          className="w-full border border-gray-300 rounded px-3 py-2 mb-4"
        />

        <div className="flex justify-end gap-3">
          <button onClick={onClose} className="px-4 py-2 border rounded hover:bg-gray-100">
            Cancelar
          </button>
          <button
            onClick={() => {
              if (comandaId) onConfirm(comandaId);
            }}
            className="px-4 py-2 bg-blue-600 text-white rounded hover:bg-blue-700"
          >
            Confirmar
          </button>
        </div>
      </div>
    </div>
  );
}

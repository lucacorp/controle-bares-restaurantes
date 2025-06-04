import { useState } from 'react';

interface ModalFinalizarMesaProps {
  isOpen: boolean;
  onClose: () => void;
  onConfirm: (dividirPor: number | null) => void;
}

export default function ModalFinalizarMesa({ isOpen, onClose, onConfirm }: ModalFinalizarMesaProps) {
  const [dividirPor, setDividirPor] = useState<number | null>(null);

  const handleSubmit = (e: React.FormEvent) => {
    e.preventDefault();
    onConfirm(dividirPor);
    setDividirPor(null);
  };

  if (!isOpen) return null;

  return (
    <div className="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center z-50">
      <div className="bg-white rounded-lg shadow-lg p-6 w-full max-w-sm">
        <h3 className="text-lg font-semibold mb-4">Finalizar Mesa</h3>

        <form onSubmit={handleSubmit} className="space-y-4">
          <div>
            <label className="block mb-1 font-medium">Dividir entre quantas pessoas?</label>
            <input
              type="number"
              min={1}
              value={dividirPor ?? ''}
              onChange={(e) => setDividirPor(e.target.value ? Number(e.target.value) : null)}
              placeholder="Opcional"
              className="w-full border border-gray-300 rounded px-3 py-2"
            />
          </div>

          <div className="flex justify-end gap-2">
            <button
              type="button"
              onClick={() => {
                setDividirPor(null);
                onClose();
              }}
              className="px-4 py-2 border border-gray-400 rounded hover:bg-gray-100"
            >
              Cancelar
            </button>
            <button
              type="submit"
              className="px-4 py-2 bg-green-600 text-white rounded hover:bg-green-700"
            >
              Finalizar
            </button>
          </div>
        </form>
      </div>
    </div>
  );
}

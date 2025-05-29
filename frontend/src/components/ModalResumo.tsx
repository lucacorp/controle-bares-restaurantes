interface Props {
  isOpen: boolean;
  onClose: () => void;
  onConfirm: (nomeCliente: string, observacoes: string) => void;
  nomeCliente: string;
  setNomeCliente: (v: string) => void;
  observacoes: string;
  setObservacoes: (v: string) => void;
}

export default function ModalResumo({
  isOpen,
  onClose,
  onConfirm,
  nomeCliente,
  setNomeCliente,
  observacoes,
  setObservacoes
}: Props) {
  if (!isOpen) return null; // ✅ ESSENCIAL

  return (
    <div className="fixed inset-0 bg-black bg-opacity-50 flex justify-center items-center">
      <div className="bg-white p-6 rounded shadow-md">
        <h2 className="text-xl font-bold mb-4">Finalizar Comanda</h2>

        <input
          type="text"
          placeholder="Nome do Cliente"
          value={nomeCliente}
          onChange={e => setNomeCliente(e.target.value)}
          className="border p-2 rounded w-full mb-2"
        />

        <textarea
          placeholder="Observações"
          value={observacoes}
          onChange={e => setObservacoes(e.target.value)}
          className="border p-2 rounded w-full mb-4"
        />

        <div className="flex justify-end space-x-2">
          <button
            onClick={onClose}
            className="bg-gray-300 px-4 py-2 rounded hover:bg-gray-400"
          >
            Cancelar
          </button>
          <button
            onClick={() => onConfirm(nomeCliente, observacoes)}
            className="bg-purple-600 text-white px-4 py-2 rounded hover:bg-purple-700"
          >
            Confirmar
          </button>
        </div>
      </div>
    </div>
  );
}

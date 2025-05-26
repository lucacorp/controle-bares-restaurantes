import { Dialog, DialogContent, DialogHeader, DialogTitle, DialogTrigger } from "@/components/ui/dialog";
import { Button } from "@/components/ui/button";
import { Produto } from "@/types/Produto";
import { useState } from "react";

interface Props {
  produtos: Produto[];
  onAdicionar: (produtoId: number, quantidade: number) => void;
}

export default function AddItemModal({ produtos, onAdicionar }: Props) {
  const [open, setOpen] = useState(false);
  const [produtoId, setProdutoId] = useState<number>(0);
  const [quantidade, setQuantidade] = useState<number>(1);

  const handleAdd = () => {
    if (produtoId && quantidade > 0) {
      onAdicionar(produtoId, quantidade);
      resetarFormulario();
      setOpen(false);
    }
  };

  const resetarFormulario = () => {
    setProdutoId(0);
    setQuantidade(1);
  };

  return (
    <Dialog open={open} onOpenChange={(value) => { setOpen(value); if (!value) resetarFormulario(); }}>
      <DialogTrigger asChild>
        <Button className="bg-blue-600 text-white hover:bg-blue-700">Adicionar Item</Button>
      </DialogTrigger>
      <DialogContent className="bg-white rounded-xl shadow-lg p-6 space-y-4">
        <DialogHeader>
          <DialogTitle className="text-lg font-semibold">Adicionar Item à Comanda</DialogTitle>
        </DialogHeader>

        <select
          value={produtoId}
          onChange={e => setProdutoId(Number(e.target.value))}
          className="w-full border border-gray-300 rounded px-3 py-2"
        >
          <option disabled value={0}>Selecione um produto</option>
          {produtos.map(p => (
            <option key={p.id} value={p.id}>{p.nome}</option>
          ))}
        </select>

        <input
          type="number"
          value={quantidade}
          min={1}
          onChange={e => setQuantidade(Number(e.target.value))}
          className="w-full border border-gray-300 rounded px-3 py-2"
        />

        <Button
          onClick={handleAdd}
          disabled={produtoId === 0}
          className="w-full bg-green-600 text-white hover:bg-green-700"
        >
          Adicionar
        </Button>
      </DialogContent>
    </Dialog>
  );
}

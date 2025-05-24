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
        <Button className="bg-blue-600 text-white">Adicionar Item</Button>
      </DialogTrigger>
      <DialogContent>
        <DialogHeader>
          <DialogTitle>Adicionar Item à Comanda</DialogTitle>
        </DialogHeader>

        <select
          value={produtoId}
          onChange={e => setProdutoId(Number(e.target.value))}
          className="border p-2 rounded w-full mb-2"
        >
          <option disabled value={0}>Selecione um produto</option>
          {produtos.map(p => (
            <option key={p.id} value={p.id}>{p.nome}</option>
          ))}
        </select>

        <input
          type="number"
          min={1}
          value={quantidade}
          onChange={e => setQuantidade(Number(e.target.value))}
          className="border p-2 rounded w-full mb-4"
        />

        <Button onClick={handleAdd} disabled={produtoId === 0}>
          Adicionar
        </Button>
      </DialogContent>
    </Dialog>
  );
}

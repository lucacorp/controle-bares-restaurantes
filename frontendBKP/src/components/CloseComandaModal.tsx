import {
  Dialog,
  DialogTrigger,
  DialogContent,
  DialogHeader,
  DialogTitle,
  DialogFooter,
} from "@/components/ui/dialog";
import { Button } from "@/components/ui/button";
import { useState } from "react";

interface Props {
  total: number;
  onConfirmar: () => void;
}

export default function CloseComandaModal({ total, onConfirmar }: Props) {
  const [open, setOpen] = useState(false);

  const handleConfirmar = () => {
    onConfirmar();
    setOpen(false);
  };

  return (
    <Dialog open={open} onOpenChange={setOpen}>
      <DialogTrigger asChild>
        <Button variant="destructive">Fechar Comanda</Button>
      </DialogTrigger>

      <DialogContent>
        <DialogHeader>
          <DialogTitle>Confirmar Fechamento</DialogTitle>
        </DialogHeader>

        <div className="text-base">
          Tem certeza que deseja fechar esta comanda?
        </div>
        <div className="font-bold text-lg text-right mt-2">
          Total: R$ {total.toFixed(2)}
        </div>

        <DialogFooter className="flex justify-end gap-2">
          <Button variant="ghost" onClick={() => setOpen(false)}>
            Cancelar
          </Button>
          <Button variant="destructive" onClick={handleConfirmar}>
            Confirmar
          </Button>
        </DialogFooter>
      </DialogContent>
    </Dialog>
  );
}

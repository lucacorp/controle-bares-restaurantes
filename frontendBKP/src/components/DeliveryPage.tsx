import { useEffect, useState } from "react";
import { getAllProducts } from "@/services/productService";
import { Product } from "@/types/Product";
import { Input } from "@/components/ui/input";
import { Button } from "@/components/ui/button";
import { Textarea } from "@/components/ui/textarea";

type PedidoItem = {
  product: Product;
  quantity: number;
};

export default function DeliveryPage() {
  const [products, setProducts] = useState<Product[]>([]);
  const [pedido, setPedido] = useState<PedidoItem[]>([]);
  const [nome, setNome] = useState("");
  const [telefone, setTelefone] = useState("");
  const [endereco, setEndereco] = useState("");
  const [pagamento, setPagamento] = useState("Dinheiro");
  const [observacoes, setObservacoes] = useState("");

  useEffect(() => {
    getAllProducts().then(setProducts);
  }, []);

  const handleAdd = (product: Product) => {
    setPedido(prev => {
      const found = prev.find(p => p.product.id === product.id);
      if (found) {
        return prev.map(p =>
          p.product.id === product.id ? { ...p, quantity: p.quantity + 1 } : p
        );
      }
      return [...prev, { product, quantity: 1 }];
    });
  };

  const handleRemove = (product: Product) => {
    setPedido(prev =>
      prev
        .map(p =>
          p.product.id === product.id ? { ...p, quantity: p.quantity - 1 } : p
        )
        .filter(p => p.quantity > 0)
    );
  };

  const getTotal = () =>
    pedido.reduce((sum, item) => sum + item.product.preco * item.quantity, 0);

  const gerarMensagem = () => {
    const itens = pedido
      .map(p => `- ${p.quantity}x ${p.product.nome}`)
      .join("\n");

    return encodeURIComponent(
      `Olá! Novo pedido:\n\nNome: ${nome}\nEndereço: ${endereco}\nItens:\n${itens}\n\nTotal: R$ ${getTotal().toFixed(
        2
      )}\nPagamento: ${pagamento}\nObservações: ${observacoes}`
    );
  };

  const abrirWhatsapp = () => {
    const numero = telefone.replace(/\D/g, ""); // limpa máscara
    const msg = gerarMensagem();
    window.open(`https://wa.me/55${numero}?text=${msg}`, "_blank");
  };

  return (
    <div className="max-w-3xl mx-auto p-4">
      <h2 className="text-2xl font-bold mb-4">Pedido de Delivery</h2>

      <div className="grid grid-cols-1 md:grid-cols-2 gap-4 mb-6">
        <Input
          placeholder="Nome do Cliente"
          value={nome}
          onChange={e => setNome(e.target.value)}
        />
        <Input
          placeholder="Telefone (WhatsApp)"
          value={telefone}
          onChange={e => setTelefone(e.target.value)}
        />
        <Input
          placeholder="Endereço para entrega"
          value={endereco}
          onChange={e => setEndereco(e.target.value)}
          className="md:col-span-2"
        />
        <select
          value={pagamento}
          onChange={e => setPagamento(e.target.value)}
          className="border rounded p-2"
        >
          <option>Dinheiro</option>
          <option>Cartão</option>
          <option>Pix</option>
        </select>
      </div>

      <div className="mb-4">
        <h3 className="text-lg font-semibold mb-2">Itens do Pedido</h3>
        <div className="grid grid-cols-1 sm:grid-cols-2 gap-2">
          {products.map(prod => (
            <div
              key={prod.id}
              className="border rounded p-2 flex justify-between items-center"
            >
              <div>
                <strong>{prod.nome}</strong>
                <div className="text-sm text-gray-500">
                  R$ {prod.preco.toFixed(2)}
                </div>
              </div>
              <div className="flex items-center gap-1">
                <Button onClick={() => handleRemove(prod)}>-</Button>
                <span>
                  {pedido.find(p => p.product.id === prod.id)?.quantity || 0}
                </span>
                <Button onClick={() => handleAdd(prod)}>+</Button>
              </div>
            </div>
          ))}
        </div>
      </div>

      <Textarea
        placeholder="Observações (ex: sem cebola, entregar na portaria...)"
        value={observacoes}
        onChange={e => setObservacoes(e.target.value)}
        className="mb-4"
      />

      <div className="flex justify-between items-center">
        <div className="text-lg font-bold">
          Total: R$ {getTotal().toFixed(2)}
        </div>
        <Button onClick={abrirWhatsapp} disabled={pedido.length === 0}>
          Gerar Pedido no WhatsApp
        </Button>
      </div>
    </div>
  );
}

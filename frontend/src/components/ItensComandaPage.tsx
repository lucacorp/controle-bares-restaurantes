import { useEffect, useState } from 'react';
import api from '../services/api';
import { Produto } from '@/types/Produto';
import AddItemModal from './AddItemModal';

interface ItemComandaDTO {
  id: number;
  produtoId: number;
  produtoNome: string;
  quantidade: number;
  precoUnitario: number;
}

interface Props {
  comandaId: number;
}

export default function ItensComandaPage({ comandaId }: Props) {
  const [itens, setItens] = useState<ItemComandaDTO[]>([]);
  const [produtos, setProdutos] = useState<Produto[]>([]);

  const loadItens = async () => {
    try {
      const resp = await api.get(`/itens-comanda/${comandaId}`);
      setItens(Array.isArray(resp.data) ? resp.data : []);
    } catch (err) {
      console.error('Erro ao carregar itens:', err);
    }
  };

  const loadProdutos = async () => {
    try {
      const resp = await api.get('/produtos');
      setProdutos(Array.isArray(resp.data) ? resp.data : []);
    } catch (err) {
      console.error('Erro ao carregar produtos:', err);
    }
  };

  const handleAdicionar = async (produtoId: number, quantidade: number) => {
    const produto = produtos.find(p => p.id === produtoId);
    if (!produto) {
      alert('Produto inválido');
      return;
    }

    const precoUnitario = produto.precoVenda ?? produto.preco ?? 0;

    try {
      await api.post('/itens-comanda', {
        comanda: { id: comandaId },
        produto: { id: produtoId },
        quantidade,
        precoUnitario
      });
      loadItens();
    } catch (err) {
      console.error('Erro ao adicionar item:', err);
    }
  };

  const calcularTotal = () => {
    return itens.reduce((acc, item) => acc + item.precoUnitario * item.quantidade, 0).toFixed(2);
  };

  useEffect(() => {
    loadItens();
    loadProdutos();
  }, [comandaId]);

  return (
    <div className="p-4">
      <h2 className="text-xl font-bold mb-4">Itens da Comanda #{comandaId}</h2>

      <AddItemModal produtos={produtos} onAdicionar={handleAdicionar} />

      <table className="w-full border mt-4">
        <thead>
          <tr className="bg-gray-100">
            <th>Produto</th>
            <th>Quantidade</th>
            <th>Preço Unitário</th>
            <th>Subtotal</th>
          </tr>
        </thead>
        <tbody>
          {itens.map(item => (
            <tr key={item.id}>
              <td>{item.produtoNome}</td>
              <td>{item.quantidade}</td>
              <td>R$ {item.precoUnitario.toFixed(2)}</td>
              <td>R$ {(item.precoUnitario * item.quantidade).toFixed(2)}</td>
            </tr>
          ))}
        </tbody>
      </table>

      <div className="text-right mt-4 font-bold">
        Total: R$ {calcularTotal()}
      </div>
    </div>
  );
}

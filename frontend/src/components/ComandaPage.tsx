import { useParams } from 'react-router-dom';
import { useEffect, useState } from 'react';
import api from '../services/api';
import { Produto } from '@/types/Produto';
import AddItemModal from '@/components/AddItemModal';

interface ItemComanda {
  id: number;
  produto: Produto;
  quantidade: number;
  precoUnitario: number;
}

interface Comanda {
  id: number;
  mesaId: number;
  status: string;
  dataAbertura: string;
  dataFechamento: string | null;
  total: number;
  itens: ItemComanda[];
}

export default function ComandaPage() {
  const { mesaId } = useParams<{ mesaId: string }>();
  const [comanda, setComanda] = useState<Comanda | null>(null);
  const [produtos, setProdutos] = useState<Produto[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  const carregarComanda = () => {
    const idNum = Number(mesaId);

    if (isNaN(idNum) || idNum <= 0) {
      console.error("mesaId inválido:", mesaId);
      setError("ID da mesa inválido.");
      setLoading(false);
      return;
    }

    api.get(`/comandas/mesa/${idNum}`)
      .then(res => {
        setComanda(res.data);
        setLoading(false);
      })
      .catch(() => {
        api.post('/comandas', { mesaId: idNum })
          .then(res => {
            setComanda(res.data);
            setLoading(false);
          })
          .catch(err => {
            console.error("Erro ao criar comanda:", err);
            setError("Erro ao carregar ou criar comanda.");
            setLoading(false);
          });
      });
  };

  const loadProdutos = async () => {
    try {
      const resp = await api.get("/produtos");
      setProdutos(resp.data);
    } catch (err) {
      console.error("Erro ao carregar produtos:", err);
    }
  };

  useEffect(() => {
    carregarComanda();
    loadProdutos();
  }, [mesaId]);

  if (loading) return <div>Carregando comanda...</div>;
  if (error) return <div className="text-red-600">{error}</div>;

  return (
    <div>
      <h1>Comanda Mesa #{mesaId}</h1>
      <AddItemModal
        produtos={produtos}
        onAdicionar={async (produtoId, quantidade) => {
          try {
            await api.post("/itens-comanda", {
              comanda: { id: comanda!.id },
              produto: { id: produtoId },
              quantidade
            });
            carregarComanda();
          } catch (err) {
            console.error("Erro ao adicionar item:", err);
          }
        }}
      />
    </div>
  );
}

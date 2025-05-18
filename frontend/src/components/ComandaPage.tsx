import { useParams } from 'react-router-dom';
import { useEffect, useState } from 'react';
import api from '../components/axiosConfig'; // seu axios já configurado com baseURL e token

interface ItemComanda {
  id: number;
  produtoNome: string;
  quantidade: number;
  precoUnitario: number;
}

interface Comanda {
  id: number;
  mesaId: number;
  total: number;
  itens: ItemComanda[];
}

export default function ComandaPage() {
  const { mesaId } = useParams<{ mesaId: string }>();

  const [comanda, setComanda] = useState<Comanda | null>(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    if (!mesaId) {
      setError("Mesa inválida");
      setLoading(false);
      return;
    }

    setLoading(true);
    setError(null);

    api.get(`/comandas/mesa/${mesaId}`)
      .then(res => {
        setComanda(res.data);
        setLoading(false);
      })
      .catch(() => {
        api.post('/comandas', { mesaId: Number(mesaId) })
          .then(res => {
            setComanda(res.data);
            setLoading(false);
          })
          .catch(() => {
            setError("Erro ao carregar comanda");
            setLoading(false);
          });
      });
  }, [mesaId]);

  if (loading) return <div className="p-4 text-center">Carregando comanda...</div>;
  if (error) return <div className="p-4 text-center text-red-600">{error}</div>;
  if (!comanda) return <div className="p-4 text-center">Nenhuma comanda encontrada.</div>;

  return (
    <div className="p-4">
      <h1 className="text-xl font-bold mb-4">Comanda da Mesa #{mesaId}</h1>

      <table className="w-full mb-4 border">
        <thead>
          <tr className="bg-gray-100">
            <th>Produto</th>
            <th>Qtd</th>
            <th>Preço Unit.</th>
            <th>Total</th>
          </tr>
        </thead>
        <tbody>
          {comanda.itens.length === 0 ? (
            <tr>
              <td colSpan={4} className="text-center py-4">Nenhum item na comanda</td>
            </tr>
          ) : (
            comanda.itens.map(item => (
              <tr key={item.id}>
                <td>{item.produtoNome}</td>
                <td>{item.quantidade}</td>
                <td>R$ {item.precoUnitario.toFixed(2)}</td>
                <td>R$ {(item.quantidade * item.precoUnitario).toFixed(2)}</td>
              </tr>
            ))
          )}
        </tbody>
      </table>

      <div className="text-right font-bold text-lg">
        Total: R$ {comanda.total.toFixed(2)}
      </div>
    </div>
  );
}

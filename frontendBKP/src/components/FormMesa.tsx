import { useEffect, useState } from 'react';
import { useNavigate, useParams } from 'react-router-dom';
import api from '../services/api';
import FormContainer from './FormContainer';

type StatusMesa = 'LIVRE' | 'OCUPADA' | 'FECHADA';

interface Mesa {
  id?: number; // 🔹 ADICIONADO: 'id' pode ser opcional para a criação de uma nova mesa
  descricao: string;
  ocupada: boolean;
  status: StatusMesa;
  numero: number;
}

export default function FormMesa() {
  const [mesa, setMesa] = useState<Mesa>({
    descricao: '',
    ocupada: false,
    status: 'LIVRE',
    numero: 0
  });
  const { id } = useParams();
  const navigate = useNavigate();

  useEffect(() => {
    if (id && id !== 'nova') {
      api.get(`/mesas/${id}`)
        .then(res => setMesa(res.data))
        .catch(err => {
          console.error('Erro ao carregar mesa:', err);
          alert('Erro ao carregar dados da mesa.');
        });
    }
  }, [id]);

  const handleChange = (e: React.ChangeEvent<HTMLInputElement | HTMLSelectElement>) => {
    const { name, value } = e.target;
    setMesa(prev => {
      // 🔹 CORREÇÃO: Lógica de conversão mais clara
      if (name === 'ocupada') {
        return { ...prev, [name]: value === 'true' };
      } else if (name === 'numero') {
        return { ...prev, [name]: Number(value) };
      }
      return { ...prev, [name]: value };
    });
  };

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();

    if (!mesa.descricao.trim()) {
      alert("A descrição da mesa é obrigatória.");
      return;
    }

    if (mesa.numero <= 0) {
      alert("O número da mesa deve ser um valor válido.");
      return;
    }

    try {
      const method = id && id !== 'nova' ? 'put' : 'post';
      const url = id && id !== 'nova' ? `/mesas/${id}` : '/mesas';
      const response = await api[method](url, mesa);

      alert('Mesa salva com sucesso!');

      const mesaSalva = response.data;
      if (mesaSalva && mesaSalva.id) {
        navigate(`/mesas`);
      } else {
        navigate('/mesas');
      }
    } catch (error: any) {
      console.error('Erro ao salvar mesa:', error);
      const msg = error?.response?.data?.message || 'Erro ao salvar a mesa.';
      alert(msg);
    }
  };

  return (
    <FormContainer title={id && id !== 'nova' ? 'Editar Mesa' : 'Nova Mesa'}>
      <form onSubmit={handleSubmit} className="space-y-4">
        <div>
          <label className="block font-medium mb-1">Descrição</label>
          <input
            type="text"
            name="descricao"
            value={mesa.descricao}
            onChange={handleChange}
            required
            className="w-full border border-gray-300 rounded px-3 py-2"
          />
        </div>

		<div>
          <label className="block font-medium mb-1">Número</label>
			<input
				name="numero"
            type="number"
            // 🔹 CORREÇÃO: 'value' deve ser uma string. Converta para string.
				value={mesa.numero.toString()}
				onChange={handleChange}
				placeholder="Número da mesa"
				className="p-2 border rounded"
			/>
		</div>


        <div>
          <label className="block font-medium mb-1">Ocupada?</label>
          <select
            name="ocupada"
            value={String(mesa.ocupada)}
            onChange={handleChange}
            className="w-full border border-gray-300 rounded px-3 py-2"
          >
            <option value="false">Não</option>
            <option value="true">Sim</option>
          </select>
        </div>

        <div>
          <label className="block font-medium mb-1">Status</label>
          <select
            name="status"
            value={mesa.status}
            onChange={handleChange}
            className="w-full border border-gray-300 rounded px-3 py-2"
          >
            <option value="LIVRE">LIVRE</option>
            <option value="OCUPADA">OCUPADA</option>
            <option value="FECHADA">FECHADA</option>
          </select>
        </div>

        <div className="flex justify-end gap-3 mt-4">
          <button
            type="button"
            onClick={() => navigate('/mesas')}
            className="px-4 py-2 border border-gray-400 rounded hover:bg-gray-100"
          >
            Cancelar
          </button>
          <button
            type="submit"
            className="px-4 py-2 bg-blue-600 text-white rounded hover:bg-blue-700"
          >
            Salvar
          </button>
        </div>
      </form>
    </FormContainer>
  );
}
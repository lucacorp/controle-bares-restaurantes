import { useEffect, useState } from 'react';
import { useNavigate, useParams } from 'react-router-dom';
import api from '../services/api';
import FormContainer from './FormContainer';

export default function FormMesa() {
  const [mesa, setMesa] = useState({ descricao: '', ocupada: false });
  const { id } = useParams();
  const navigate = useNavigate();

  useEffect(() => {
    if (id) {
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
    setMesa(prev => ({
      ...prev,
      [name]: name === 'ocupada' ? value === 'true' : value
    }));
  };

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    try {
      const method = id ? 'put' : 'post';
      const url = id ? `/mesas/${id}` : '/mesas';
      await api[method](url, mesa);
      alert('Mesa salva com sucesso!');
      navigate('/mesas');
    } catch (error) {
      console.error(error);
      alert('Erro ao salvar a mesa.');
    }
  };

  return (
    <FormContainer title={id ? 'Editar Mesa' : 'Nova Mesa'}>
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
          <label className="block font-medium mb-1">Ocupada?</label>
          <select
            name="ocupada"
            value={mesa.ocupada.toString()}
            onChange={handleChange}
            className="w-full border border-gray-300 rounded px-3 py-2"
          >
            <option value="false">Não</option>
            <option value="true">Sim</option>
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

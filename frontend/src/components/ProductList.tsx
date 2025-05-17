import { useNavigate, useParams } from 'react-router-dom';
import { useState, useEffect } from 'react';
import api from '../services/api';
import FormContainer from './FormContainer';

export default function ProductForm() {
  const [produto, setProduto] = useState({
    codigo: '',
    codigo_barras: '',
    nome: '',
    descricao: '',
    grupo: '',
    tipo: '',
    categoria: '',
    unidade: '',
    preco: '',
    preco_custo: '',
    ncm: '',
    cfop: '',
    cst: '',
    origem: '',
    aliquota_icms: '',
    aliquota_ipi: '',
  });

  const [cfopList, setCfopList] = useState<{ codigo: string; descricao: string }[]>([]);
  const [cstList, setCstList] = useState<{ codigo: string; descricao: string }[]>([]);
  const [origemList, setOrigemList] = useState<{ codigo: string; descricao: string }[]>([]);
  const [icmsList] = useState<string[]>(['0', '7', '12', '17', '18', '25']);

  const navigate = useNavigate();
  const { id } = useParams();

  useEffect(() => {
    const fetchData = async () => {
      try {
        if (id) {
          const response = await api.get(`/produtos/${id}`);
          setProduto(response.data);
        }

        const [cfopData, cstData, origemData] = await Promise.all([
          api.get('/cfop'),
          api.get('/cst'),
          api.get('/origem'),
        ]);

        setCfopList(cfopData.data);
        setCstList(cstData.data);
        setOrigemList(origemData.data);
      } catch (error) {
        console.error('Erro ao buscar dados:', error);
        alert('Erro ao carregar informações.');
      }
    };

    fetchData();
  }, [id]);

  const handleChange = (e: React.ChangeEvent<HTMLInputElement | HTMLSelectElement | HTMLTextAreaElement>) => {
    const { name, value } = e.target;

    if (name === 'codigo_barras') {
      const numeric = value.replace(/\D/g, '');
      if (numeric.length > 13) return;
      setProduto((prev) => ({ ...prev, [name]: numeric }));
      return;
    }

    if (name === 'preco' || name === 'preco_custo') {
      const numeric = value.replace(/[^\d]/g, '');
      const formatted = (Number(numeric) / 100).toLocaleString('pt-BR', {
        minimumFractionDigits: 2,
        maximumFractionDigits: 2,
      });
      setProduto((prev) => ({ ...prev, [name]: formatted }));
      return;
    }

    if (name === 'ncm') {
      const numeric = value.replace(/\D/g, '');
      if (numeric.length > 8) return;
      setProduto((prev) => ({ ...prev, [name]: numeric }));
      return;
    }

    if (name === 'aliquota_icms' || name === 'aliquota_ipi') {
      const sanitized = value.replace(/[^\d.]/g, '');
      const number = parseFloat(sanitized);
      if (!isNaN(number) && number <= 100) {
        setProduto((prev) => ({ ...prev, [name]: sanitized }));
      } else if (value === '') {
        setProduto((prev) => ({ ...prev, [name]: '' }));
      }
      return;
    }

    setProduto((prev) => ({ ...prev, [name]: value }));
  };

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();

    const method = id ? 'put' : 'post';
    const url = id ? `/produtos/${id}` : '/produtos';

    try {
      await api[method](url, produto);
      alert(`Produto ${id ? 'atualizado' : 'cadastrado'} com sucesso!`);
      navigate('/produtos/lista');
    } catch (error) {
      console.error('Erro ao salvar produto:', error);
      alert('Erro ao salvar produto.');
    }
  };

  return (
    <FormContainer title={id ? 'Editar Produto' : 'Cadastrar Produto'}>
      <form onSubmit={handleSubmit} className="space-y-6">
        <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
          <input name="codigo" value={produto.codigo} readOnly placeholder="Código" className="p-2 border rounded bg-gray-100" />
          <input name="codigo_barras" value={produto.codigo_barras} onChange={handleChange} placeholder="Código de Barras" className="p-2 border rounded" />
          <input name="nome" value={produto.nome} onChange={handleChange} placeholder="Nome" className="p-2 border rounded" />
          <input name="grupo" value={produto.grupo} onChange={handleChange} placeholder="Grupo" className="p-2 border rounded" />
          <input name="categoria" value={produto.categoria} onChange={handleChange} placeholder="Categoria" className="p-2 border rounded" />
          <input name="unidade" value={produto.unidade} onChange={handleChange} placeholder="Unidade" className="p-2 border rounded" />
          <input name="preco" value={produto.preco} onChange={handleChange} placeholder="Preço Venda" className="p-2 border rounded" />
          <input name="preco_custo" value={produto.preco_custo} onChange={handleChange} placeholder="Preço Custo" className="p-2 border rounded" />
          <textarea name="descricao" value={produto.descricao} onChange={handleChange} placeholder="Descrição" className="p-2 border rounded col-span-1 md:col-span-2" />
          <input name="ncm" value={produto.ncm} onChange={handleChange} placeholder="NCM" className="p-2 border rounded" />

          <select name="cfop" value={produto.cfop} onChange={handleChange} className="p-2 border rounded">
            <option value="">CFOP</option>
            {cfopList.map((cfop) => (
              <option key={cfop.codigo} value={cfop.codigo}>
                {cfop.codigo} - {cfop.descricao}
              </option>
            ))}
          </select>

          <select name="cst" value={produto.cst} onChange={handleChange} className="p-2 border rounded">
            <option value="">CST</option>
            {cstList.map((cst) => (
              <option key={cst.codigo} value={cst.codigo}>
                {cst.codigo} - {cst.descricao}
              </option>
            ))}
          </select>

          <select name="origem" value={produto.origem} onChange={handleChange} className="p-2 border rounded">
            <option value="">Origem</option>
            {origemList.map((origem) => (
              <option key={origem.codigo} value={origem.codigo}>
                {origem.codigo} - {origem.descricao}
              </option>
            ))}
          </select>

          <select name="aliquota_icms" value={produto.aliquota_icms} onChange={handleChange} className="p-2 border rounded">
            <option value="">ICMS</option>
            {icmsList.map((icms) => (
              <option key={icms} value={icms}>
                {icms}
              </option>
            ))}
          </select>

          <input name="aliquota_ipi" value={produto.aliquota_ipi} onChange={handleChange} placeholder="IPI" className="p-2 border rounded" />
        </div>

        <button type="submit" className="w-full bg-blue-600 text-white font-semibold py-2 rounded hover:bg-blue-700">
          {id ? 'Atualizar' : 'Cadastrar'}
        </button>
      </form>
    </FormContainer>
  );
}

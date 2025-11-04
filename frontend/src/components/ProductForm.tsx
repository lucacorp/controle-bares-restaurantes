// src/components/ProductForm.tsx

import { useState, useEffect } from 'react';
import api from '../services/api';
import { toast } from 'react-toastify';

export default function ProductForm({
  id,
  onSave
}: {
  id?: number | null;
  onSave?: () => void;
}) {
  const [produto, setProduto] = useState({
    codigo: '',
    codigo_barras: '',
    nome: '',
    descricao: '',
    grupo: '',
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
    familia: '',
    materiaPrima: false,
    fabricacaoPropria: false,
    estoqueAtual: 0
  });

  const [cfopList, setCfopList] = useState<{ codigo: string; descricao: string }[]>([]);
  const [cstList, setCstList] = useState<{ codigo: string; descricao: string }[]>([]);
  const [origemList, setOrigemList] = useState<{ codigo: string; descricao: string }[]>([]);
  const [icmsList] = useState<string[]>(['0', '7', '12', '17', '18', '25']);
  const [loading, setLoading] = useState(false);

  useEffect(() => {
    const fetchData = async () => {
      setLoading(true);
      try {
        if (id) {
          const response = await api.get(`/produtos/${id}`);
          setProduto(response.data);
        }

        const [cfopData, cstData, origemData] = await Promise.all([
          api.get('/cfop'),
          api.get('/cst'),
          api.get('/origem')
        ]);

        setCfopList(cfopData.data);
        setCstList(cstData.data);
        setOrigemList(origemData.data);
      } catch (error) {
        console.error('Erro ao buscar dados:', error);
        toast.error('Erro ao carregar informações.');
      } finally {
        setLoading(false);
      }
    };

    fetchData();
  }, [id]);

  const handleChange = (e: React.ChangeEvent<HTMLInputElement | HTMLSelectElement | HTMLTextAreaElement>) => {
    const { name, value, type, checked } = e.target;

    if (type === 'checkbox') {
      setProduto((prev) => ({
        ...prev,
        [name]: checked,
        unidade: name === 'materiaPrima' && checked ? 'FRACIONADA' : prev.unidade
      }));
      return;
    }

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
        maximumFractionDigits: 2
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

    if (!produto.nome.trim() || !produto.preco || !produto.codigo_barras) {
      toast.warning('Preencha nome, preço e código de barras.');
      return;
    }

    const method = id ? 'put' : 'post';
    const url = id ? `/produtos/${id}` : '/produtos';

    try {
      await api[method](url, produto);
      toast.success(`Produto ${id ? 'atualizado' : 'cadastrado'} com sucesso!`);
      if (onSave) onSave();
    } catch (error) {
      console.error('Erro ao salvar produto:', error);
      toast.error('Erro ao salvar produto.');
    }
  };

  const handleReset = () => {
    setProduto({
      codigo: '',
      codigo_barras: '',
      nome: '',
      descricao: '',
      grupo: '',
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
      familia: '',
      materiaPrima: false,
      fabricacaoPropria: false,
      estoqueAtual: 0
    });
  };

  return (
    <form onSubmit={handleSubmit} className={`space-y-6 w-full ${loading ? 'opacity-50 pointer-events-none' : ''}`}>
      <h2 className="text-2xl font-bold text-gray-800">
        {id ? 'Editar Produto' : 'Novo Produto'}
      </h2>

      {/* Seção: Identificação */}
      <section>
        <h3 className="text-lg font-semibold mb-2 border-b pb-1">Identificação</h3>
        <div className="grid grid-cols-1 md:grid-cols-3 gap-4">
          <input name="codigo" value={produto.codigo} readOnly placeholder="Código interno" className="p-2 border rounded bg-gray-100" />
          <input name="codigo_barras" value={produto.codigo_barras} onChange={handleChange} placeholder="Código de Barras (EAN)" className="p-2 border rounded" />
          <input name="nome" value={produto.nome} onChange={handleChange} placeholder="Nome do Produto" className="p-2 border rounded" />
          <input name="grupo" value={produto.grupo} onChange={handleChange} placeholder="Grupo" className="p-2 border rounded" />
          <input name="categoria" value={produto.categoria} onChange={handleChange} placeholder="Categoria" className="p-2 border rounded" />
          <input name="familia" value={produto.familia} onChange={handleChange} placeholder="Família (ex: bar, cozinha...)" className="p-2 border rounded" />
        </div>
      </section>

      {/* Seção: Preço e Estoque */}
      <section>
        <h3 className="text-lg font-semibold mb-2 border-b pb-1">Preço & Estoque</h3>
        <div className="grid grid-cols-1 md:grid-cols-3 gap-4">
          <input name="preco" value={produto.preco} onChange={handleChange} placeholder="Preço de Venda (R$)" className="p-2 border rounded" />
          <input name="preco_custo" value={produto.preco_custo} onChange={handleChange} placeholder="Preço de Custo (R$)" className="p-2 border rounded" />
          <input name="estoqueAtual" value={produto.estoqueAtual} readOnly className="p-2 border rounded bg-gray-100" placeholder="Estoque Atual" />
          <label className="flex items-center gap-2 col-span-1">
            <input type="checkbox" name="materiaPrima" checked={produto.materiaPrima} onChange={handleChange} />
            Matéria-prima?
          </label>
          <label className="flex items-center gap-2 col-span-1">
            <input type="checkbox" name="fabricacaoPropria" checked={produto.fabricacaoPropria} onChange={handleChange} />
            Fabricação própria?
          </label>
          <input name="unidade" value={produto.unidade} onChange={handleChange} placeholder="Unidade (ex: UN, KG...)" className="p-2 border rounded" disabled={produto.materiaPrima} />
        </div>
      </section>

      {/* Seção: Fiscal */}
      <section>
        <h3 className="text-lg font-semibold mb-2 border-b pb-1">Informações Fiscais</h3>
        <div className="grid grid-cols-1 md:grid-cols-3 gap-4">
          <input name="ncm" value={produto.ncm} onChange={handleChange} placeholder="NCM (ex: 22030000)" className="p-2 border rounded" />
          <select name="cfop" value={produto.cfop} onChange={handleChange} className="p-2 border rounded">
            <option value="">CFOP</option>
            {cfopList.map(cfop => (
              <option key={cfop.codigo} value={cfop.codigo}>
                {cfop.codigo} - {cfop.descricao}
              </option>
            ))}
          </select>
          <select name="cst" value={produto.cst} onChange={handleChange} className="p-2 border rounded">
            <option value="">CST</option>
            {cstList.map(cst => (
              <option key={cst.codigo} value={cst.codigo}>
                {cst.codigo} - {cst.descricao}
              </option>
            ))}
          </select>
          <select name="origem" value={produto.origem} onChange={handleChange} className="p-2 border rounded">
            <option value="">Origem</option>
            {origemList.map(origem => (
              <option key={origem.codigo} value={origem.codigo}>
                {origem.codigo} - {origem.descricao}
              </option>
            ))}
          </select>
          <select name="aliquota_icms" value={produto.aliquota_icms} onChange={handleChange} className="p-2 border rounded">
            <option value="">ICMS (%)</option>
            {icmsList.map(icms => (
              <option key={icms} value={icms}>{icms}</option>
            ))}
          </select>
          <input name="aliquota_ipi" value={produto.aliquota_ipi} onChange={handleChange} placeholder="IPI (%)" className="p-2 border rounded" />
        </div>
      </section>

      {/* Descrição */}
      <section>
        <textarea name="descricao" value={produto.descricao} onChange={handleChange} placeholder="Descrição detalhada do produto" className="p-2 border rounded w-full min-h-[80px]" />
      </section>

      {/* Ações */}
      <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
        <button type="submit" className="w-full bg-blue-600 text-white font-semibold py-2 rounded hover:bg-blue-700">
          {id ? 'Atualizar' : 'Cadastrar'}
        </button>
        {!id && (
          <button type="button" onClick={handleReset} className="w-full bg-gray-200 text-gray-700 py-2 rounded hover:bg-gray-300">
            Limpar
          </button>
        )}
      </div>
    </form>
  );
}

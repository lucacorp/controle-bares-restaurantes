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
    id: undefined as number | undefined,
    version: undefined as number | undefined,
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

  const [cfopList, setCfopList] = useState<{ id: number; codigo: string; descricao: string }[]>([]);
  const [cstList, setCstList] = useState<{ id: number; codigo: string; descricao: string }[]>([]);
  const [origemList, setOrigemList] = useState<{ id: number; codigo: string; descricao: string }[]>([]);
  const [icmsList] = useState<string[]>(['0', '7', '12', '17', '18', '25']);
  const [loading, setLoading] = useState(false);

  useEffect(() => {
    const fetchData = async () => {
      setLoading(true);
      try {
        if (id) {
          const response = await api.get(`/produtos/${id}`);
          const data = response.data;
          setProduto({
            id: data.id,
            version: data.version,
            codigo: data.codigo || '',
            codigo_barras: data.codigo_barras || data.codigoBarras || '',
            nome: data.nome || '',
            descricao: data.descricao || '',
            grupo: data.grupo || '',
            categoria: data.categoria || '',
            unidade: data.unidade || '',
            preco: (typeof data.precoVenda === 'number')
              ? data.precoVenda.toString()
              : (data.precoVenda || ''),
            preco_custo: (typeof data.preco === 'number')
              ? data.preco.toString()
              : (data.preco || ''),
            ncm: data.ncm || '',
            cfop: data.cfop?.id ? String(data.cfop.id) : '',
            cst: data.cst?.id ? String(data.cst.id) : '',
            origem: data.origem?.id ? String(data.origem.id) : '',
            aliquota_icms: (typeof data.aliquotaIcms === 'number') ? data.aliquotaIcms.toString() : (data.aliquotaIcms || ''),
            aliquota_ipi: (typeof data.aliquotaIpi === 'number') ? data.aliquotaIpi.toString() : (data.aliquotaIpi || ''),
            familia: data.familia || '',
            materiaPrima: !!data.materiaPrima,
            fabricacaoPropria: !!data.fabricacaoPropria,
            estoqueAtual: typeof data.estoqueAtual === 'number' ? data.estoqueAtual : 0
          });
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

  const handleChange = (
    e: React.ChangeEvent<HTMLInputElement | HTMLSelectElement | HTMLTextAreaElement>
  ) => {
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
      setProduto((prev) => ({ ...prev, codigo_barras: numeric }));
      return;
    }

    if (name === 'preco' || name === 'preco_custo') {
      const numeric = value.replace(/[^\d]/g, '');
      const formatted =
        numeric === ''
          ? ''
          : (Number(numeric) / 100).toLocaleString('en-US', {
              minimumFractionDigits: 2,
              maximumFractionDigits: 2
            });
      setProduto((prev) => ({ ...prev, [name]: formatted }));
      return;
    }

    if (name === 'estoqueAtual') {
      const numeric = value.replace(/\D/g, '');
      setProduto((prev) => ({
        ...prev,
        estoqueAtual: numeric === '' ? 0 : parseInt(numeric)
      }));
      return;
    }

    if (name === 'ncm') {
      const numeric = value.replace(/\D/g, '');
      if (numeric.length > 8) return;
      setProduto((prev) => ({ ...prev, ncm: numeric }));
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

  const isEmptyField = (value: string | undefined) => {
    return value === undefined || value.trim() === '';
  };

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();

    // Validação
    if (
      isEmptyField(produto.nome) ||
      isEmptyField(produto.preco) ||
      isEmptyField(produto.preco_custo) ||
      isEmptyField(produto.codigo_barras) ||
      isEmptyField(produto.unidade) ||
      isEmptyField(produto.cfop) ||
      isEmptyField(produto.cst) ||
      isEmptyField(produto.origem)
    ) {
      toast.warning('Preencha todos os campos obrigatórios.');
      return;
    }

    const cfopObj = cfopList.find(cfop => String(cfop.id) === produto.cfop);
    const cstObj = cstList.find(cst => String(cst.id) === produto.cst);
    const origemObj = origemList.find(origem => String(origem.id) === produto.origem);

    const payload: any = {
      id: produto.id,
      version: produto.version,
      codigo: produto.codigo || undefined,
      codigoBarras: produto.codigo_barras,
      nome: produto.nome,
      descricao: produto.descricao,
      grupo: produto.grupo,
      categoria: produto.categoria,
      unidade: produto.unidade,
      precoVenda: produto.preco ? Number(produto.preco.replace(',', '.')) : undefined,
      preco: produto.preco_custo ? Number(produto.preco_custo.replace(',', '.')) : undefined,
      ncm: produto.ncm || undefined,
      cfop: cfopObj ? { id: cfopObj.id } : undefined,
      cst: cstObj ? { id: cstObj.id } : undefined,
      origem: origemObj ? { id: origemObj.id } : undefined,
      aliquotaIcms: produto.aliquota_icms ? Number(produto.aliquota_icms.replace(',', '.')) : undefined,
      aliquotaIpi: produto.aliquota_ipi ? Number(produto.aliquota_ipi.replace(',', '.')) : undefined,
      familia: produto.familia,
      materiaPrima: produto.materiaPrima,
      fabricacaoPropria: produto.fabricacaoPropria,
      estoqueAtual: produto.estoqueAtual
    };

    Object.keys(payload).forEach(key => {
      if (payload[key] === undefined) delete payload[key];
    });

    // Conferência do payload
    console.log('Payload enviado:', payload);

    const method = id ? 'put' : 'post';
    const url = id ? `/produtos/${id}` : '/produtos';

    try {
      await api[method](url, payload);
      toast.success(`Produto ${id ? 'atualizado' : 'cadastrado'} com sucesso!`);
      if (onSave) onSave();
    } catch (error) {
      console.error('Erro ao salvar produto:', error);
      toast.error('Erro ao salvar produto.');
    }
  };

  const handleReset = () => {
    setProduto({
      id: undefined,
      version: undefined,
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
      {/* Identificação */}
      <section>
        <h3 className="text-lg font-semibold mb-2 border-b pb-1">Identificação</h3>
        <div className="grid grid-cols-1 md:grid-cols-3 gap-4">
          <input name="codigo" value={produto.codigo} onChange={handleChange} placeholder="Código interno" className="p-2 border rounded" />
          <input name="codigo_barras" value={produto.codigo_barras} onChange={handleChange} placeholder="Código de Barras (EAN)" className="p-2 border rounded" />
          <input name="nome" value={produto.nome} onChange={handleChange} placeholder="Nome do Produto" className="p-2 border rounded" />
          <input name="grupo" value={produto.grupo} onChange={handleChange} placeholder="Grupo" className="p-2 border rounded" />
          <input name="categoria" value={produto.categoria} onChange={handleChange} placeholder="Categoria" className="p-2 border rounded" />
          <input name="familia" value={produto.familia} onChange={handleChange} placeholder="Família (ex: bar, cozinha...)" className="p-2 border rounded" />
        </div>
        <div className="grid grid-cols-1 md:grid-cols-3 gap-4 mt-2">
          {/* Campo version somente leitura */}
          <input
            name="version"
            value={produto.version ?? ''}
            readOnly
            className="p-2 border rounded bg-gray-100 text-gray-500"
            placeholder="Version"
            style={{ fontStyle: 'italic' }}
          />
        </div>
      </section>

      {/* Preço e Estoque */}
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

      {/* Informações Fiscais */}
      <section>
        <h3 className="text-lg font-semibold mb-2 border-b pb-1">Informações Fiscais</h3>
        <div className="grid grid-cols-1 md:grid-cols-3 gap-4">
          <input name="ncm" value={produto.ncm} onChange={handleChange} placeholder="NCM (ex: 22030000)" className="p-2 border rounded" />
          <select name="cfop" value={produto.cfop} onChange={handleChange} className="p-2 border rounded">
            <option value="">CFOP</option>
            {cfopList.map(cfop => (
              <option key={cfop.id} value={cfop.id}>{cfop.codigo} - {cfop.descricao}</option>
            ))}
          </select>
          <select name="cst" value={produto.cst} onChange={handleChange} className="p-2 border rounded">
            <option value="">CST</option>
            {cstList.map(cst => (
              <option key={cst.id} value={cst.id}>{cst.codigo} - {cst.descricao}</option>
            ))}
          </select>
          <select name="origem" value={produto.origem} onChange={handleChange} className="p-2 border rounded">
            <option value="">Origem</option>
            {origemList.map(origem => (
              <option key={origem.id} value={origem.id}>{origem.codigo} - {origem.descricao}</option>
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
import { useNavigate, useParams } from 'react-router-dom'
import { useState, useEffect } from 'react'

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
  })

  const [cfopList, setCfopList] = useState<{ codigo: string; descricao: string }[]>([])
  const [cstList, setCstList] = useState<{ codigo: string; descricao: string }[]>([])
  const [origemList, setOrigemList] = useState<{ codigo: string; descricao: string }[]>([])
  const [icmsList] = useState<string[]>(['0', '7', '12', '17', '18', '25'])

  const navigate = useNavigate()
  const { id } = useParams()

  useEffect(() => {
    const fetchData = async () => {
      try {
        if (id) {
          const response = await fetch(`http://localhost:8080/api/produtos/${id}`)
          if (!response.ok) throw new Error('Produto não encontrado')
          const data = await response.json()
          setProduto(data)
        }

        const cfopResponse = await fetch('http://localhost:8080/api/cfop')
        if (!cfopResponse.ok) throw new Error('Erro ao carregar CFOP')
        const cfopData = await cfopResponse.json()
        setCfopList(cfopData)

        const cstResponse = await fetch('http://localhost:8080/api/cst')
        if (!cstResponse.ok) throw new Error('Erro ao carregar CST')
        const cstData = await cstResponse.json()
        setCstList(cstData)

        const origemResponse = await fetch('http://localhost:8080/api/origem')
        if (!origemResponse.ok) throw new Error('Erro ao carregar Origem')
        const origemData = await origemResponse.json()
        setOrigemList(origemData)
      } catch (error) {
        console.error('Erro ao buscar dados:', error)
        alert('Ocorreu um erro ao carregar as informações. Tente novamente mais tarde.')
      }
    }

    fetchData()
  }, [id])

  if (!cfopList.length || !cstList.length || !origemList.length) {
    return (
      <div className="text-center mt-10">
        <p>Carregando dados...</p>
      </div>
    )
  }

  const handleChange = (e: React.ChangeEvent<HTMLInputElement | HTMLSelectElement | HTMLTextAreaElement>) => {
    const { name, value } = e.target

    // Máscara EAN-13 para código_barras
    if (name === 'codigo_barras') {
      const numeric = value.replace(/\D/g, '')
      if (numeric.length > 13) return
      setProduto((prev) => ({ ...prev, [name]: numeric }))
      return
    }

    // Máscara moeda
    if (name === 'preco' || name === 'preco_custo') {
      const numeric = value.replace(/[^\d]/g, '')
      const formatted = (Number(numeric) / 100).toLocaleString('pt-BR', {
        minimumFractionDigits: 2,
        maximumFractionDigits: 2
      })
      setProduto((prev) => ({ ...prev, [name]: formatted }))
      return
    }

    // Validação NCM - 8 dígitos
    if (name === 'ncm') {
      const numeric = value.replace(/\D/g, '')
      if (numeric.length > 8) return
      setProduto((prev) => ({ ...prev, [name]: numeric }))
      return
    }

    // Validação ICMS/IPI - número entre 0 e 100
    if (name === 'aliquota_icms' || name === 'aliquota_ipi') {
      const sanitized = value.replace(/[^\d.]/g, '')
      const number = parseFloat(sanitized)
      if (!isNaN(number) && number <= 100) {
        setProduto((prev) => ({ ...prev, [name]: sanitized }))
      } else if (value === '') {
        setProduto((prev) => ({ ...prev, [name]: '' }))
      }
      return
    }

    setProduto((prev) => ({ ...prev, [name]: value }))
  }

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault()
    const url = id ? `http://localhost:8080/api/produtos/${id}` : 'http://localhost:8080/api/produtos'
    const method = id ? 'PUT' : 'POST'

    try {
      await fetch(url, {
        method,
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(produto),
      })

      alert(`Produto ${id ? 'atualizado' : 'cadastrado'} com sucesso!`)
      navigate('/produtos/lista')
    } catch (error) {
      console.error('Erro ao salvar produto:', error)
    }
  }

  return (
    <form
      onSubmit={handleSubmit}
      className="bg-white p-6 rounded shadow-md max-w-5xl mx-auto mt-10"
    >
      <h2 className="text-2xl font-bold mb-6">{id ? 'Editar' : 'Cadastrar'} Produto</h2>
	<div className="mb-4">
  <label className="block text-gray-700 font-medium mb-2" htmlFor="tipo">
    Tipo de Produto
  </label>
  <select
    id="tipo"
    name="tipo"
    value={produto.tipo}
    onChange={handleChange}
    className="w-full border rounded px-3 py-2"
    required
  >
    <option value="">Selecione</option>
    <option value="VENDA">Venda</option>
    <option value="INSUMO">Insumo</option>
  </select>
</div>


      <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
        <input name="codigo" value={produto.codigo} readOnly placeholder="Código (gerado)" className="p-2 border rounded bg-gray-100" />
        <input name="codigo_barras" value={produto.codigo_barras} onChange={handleChange} placeholder="Código de Barras (EAN-13)" className="p-2 border rounded" />
        <input name="nome" value={produto.nome} onChange={handleChange} placeholder="Nome" className="p-2 border rounded" />
        <input name="grupo" value={produto.grupo} onChange={handleChange} placeholder="Grupo" className="p-2 border rounded" />
        <input name="categoria" value={produto.categoria} onChange={handleChange} placeholder="Categoria" className="p-2 border rounded" />
        <input name="unidade" value={produto.unidade} onChange={handleChange} placeholder="Unidade" className="p-2 border rounded" />
        <input name="preco" value={produto.preco} onChange={handleChange} placeholder="Preço Venda (R$)" className="p-2 border rounded" />
        <input name="preco_custo" value={produto.preco_custo} onChange={handleChange} placeholder="Preço Custo (R$)" className="p-2 border rounded" />
        <textarea name="descricao" value={produto.descricao} onChange={handleChange} placeholder="Descrição" className="p-2 border rounded col-span-1 md:col-span-2" />
        <input name="ncm" value={produto.ncm} onChange={handleChange} placeholder="NCM (8 dígitos)" className="p-2 border rounded" />

        <select name="cfop" value={produto.cfop} onChange={handleChange} className="p-2 border rounded">
          <option value="">Selecione o CFOP</option>
          {cfopList.map((cfop) => (
            <option key={cfop.codigo} value={cfop.codigo}>
              {cfop.codigo} - {cfop.descricao}
            </option>
          ))}
        </select>

        <select name="cst" value={produto.cst} onChange={handleChange} className="p-2 border rounded">
          <option value="">Selecione o CST</option>
          {cstList.map((cst) => (
            <option key={cst.codigo} value={cst.codigo}>
              {cst.codigo} - {cst.descricao}
            </option>
          ))}
        </select>

        <select name="origem" value={produto.origem} onChange={handleChange} className="p-2 border rounded">
          <option value="">Selecione a Origem</option>
          {origemList.map((origem) => (
            <option key={origem.codigo} value={origem.codigo}>
              {origem.codigo} - {origem.descricao}
            </option>
          ))}
        </select>

        <select name="aliquota_icms" value={produto.aliquota_icms} onChange={handleChange} className="p-2 border rounded">
          <option value="">Alíquota ICMS (%)</option>
          {icmsList.map((icms) => (
            <option key={icms} value={icms}>{icms}</option>
          ))}
        </select>

        <input name="aliquota_ipi" value={produto.aliquota_ipi} onChange={handleChange} placeholder="Alíquota IPI (%)" className="p-2 border rounded" />
      </div>

      <button type="submit" className="mt-6 w-full bg-blue-600 text-white font-semibold py-2 rounded hover:bg-blue-700">
        {id ? 'Atualizar' : 'Cadastrar'}
      </button>
    </form>
  )
}

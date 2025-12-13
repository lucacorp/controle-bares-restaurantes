import React, { useEffect, useState } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import api from '../services/api';
import { toast } from 'react-toastify';

interface ItemComanda {
  id: number;
  produtoNome: string;
  quantidade: number;
  precoVenda: number;
  total: number;
}

interface Comanda {
  id: number;
  numeroMesa?: number;
  status: string;
  itens: ItemComanda[];
}

type FormaPagamento = 'DINHEIRO' | 'DEBITO' | 'CREDITO' | 'PIX';

export default function ComandaFechamentoPage() {
  const { id } = useParams<{ id: string }>();
  const navigate = useNavigate();

  const [comanda, setComanda] = useState<Comanda | null>(null);
  const [loading, setLoading] = useState(true);
  const [processando, setProcessando] = useState(false);
  
  const [formaPagamento, setFormaPagamento] = useState<FormaPagamento>('DINHEIRO');
  const [valorRecebido, setValorRecebido] = useState<string>('');
  const [nomeCliente, setNomeCliente] = useState<string>('');
  const [cpfCliente, setCpfCliente] = useState<string>('');
  const [observacoes, setObservacoes] = useState<string>('');
  const [emitirNFCe, setEmitirNFCe] = useState<boolean>(true);

  const comandaId = Number(id);

  useEffect(() => {
    carregarComanda();
  }, [id]);

  const carregarComanda = async () => {
    if (!id || isNaN(comandaId)) {
      toast.error('ID da comanda inválido');
      navigate('/comandas');
      return;
    }

    try {
      setLoading(true);
      const { data } = await api.get(`/comandas/${id}`);
      
      if (data.status === 'FECHADA') {
        toast.error('Esta comanda já foi fechada');
        navigate(`/comandas`);
        return;
      }

      if (!data.itens || data.itens.length === 0) {
        toast.error('Comanda sem itens');
        navigate(`/comandas/${id}/itens`);
        return;
      }

      setComanda(data);
    } catch (error: any) {
      console.error('Erro ao carregar comanda:', error);
      toast.error(error?.response?.data?.message || 'Erro ao carregar comanda');
      navigate('/comandas');
    } finally {
      setLoading(false);
    }
  };

  const calcularTotal = (): number => {
    if (!comanda) return 0;
    return comanda.itens.reduce((sum, item) => sum + item.total, 0);
  };

  const calcularTroco = (): number => {
    if (formaPagamento !== 'DINHEIRO') return 0;
    const recebido = parseFloat(valorRecebido) || 0;
    const total = calcularTotal();
    return Math.max(0, recebido - total);
  };

  const formatarCPF = (value: string): string => {
    const numbers = value.replace(/\D/g, '');
    if (numbers.length <= 11) {
      return numbers
        .replace(/(\d{3})(\d)/, '$1.$2')
        .replace(/(\d{3})(\d)/, '$1.$2')
        .replace(/(\d{3})(\d{1,2})$/, '$1-$2');
    }
    return numbers.substring(0, 11);
  };

  const handleCPFChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    setCpfCliente(formatarCPF(e.target.value));
  };

  const formatarValor = (value: string): string => {
    const numbers = value.replace(/\D/g, '');
    const valorDecimal = parseFloat(numbers) / 100;
    return valorDecimal.toFixed(2);
  };

  const handleValorRecebidoChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    const formatted = formatarValor(e.target.value);
    setValorRecebido(formatted);
  };

  const validarFormulario = (): boolean => {
    const total = calcularTotal();

    if (formaPagamento === 'DINHEIRO') {
      const recebido = parseFloat(valorRecebido) || 0;
      if (recebido < total) {
        toast.error('Valor recebido insuficiente');
        return false;
      }
    }

    if (emitirNFCe && cpfCliente) {
      const cpfNumeros = cpfCliente.replace(/\D/g, '');
      if (cpfNumeros.length !== 11) {
        toast.error('CPF inválido');
        return false;
      }
    }

    return true;
  };

  const finalizarComanda = async () => {
    if (!validarFormulario()) return;

    setProcessando(true);
    try {
      // 1. Fechar comanda
      const resumo = await api.post(`/comandas/${id}/fechar`, {
        nomeCliente,
        cpfCliente: cpfCliente.replace(/\D/g, ''),
        observacoes: `Pagamento: ${formaPagamento}${observacoes ? ` - ${observacoes}` : ''}`,
      });

      toast.success('✅ Comanda fechada com sucesso!');

      // 2. Emitir NFCe se solicitado
      if (emitirNFCe) {
        try {
          const nfceResponse = await api.post(`/nfe/emitir/${id}`, {
            cpfCnpj: cpfCliente.replace(/\D/g, '') || null,
          });

          if (nfceResponse.data.status === 'AUTORIZADA') {
            toast.success('🧾 NFC-e emitida com sucesso!');
            
            // Exibir modal com resumo e opção de imprimir
            setTimeout(() => {
              const confirma = window.confirm(
                `✅ Comanda ${id} finalizada!\n\n` +
                `💰 Total: R$ ${calcularTotal().toFixed(2)}\n` +
                `💳 Pagamento: ${formaPagamento}\n` +
                `${formaPagamento === 'DINHEIRO' ? `💵 Troco: R$ ${calcularTroco().toFixed(2)}\n` : ''}` +
                `🧾 Chave NFC-e: ${nfceResponse.data.chaveAcesso?.substring(0, 20)}...\n\n` +
                `Deseja baixar o XML da NFC-e?`
              );

              if (confirma && nfceResponse.data.chaveAcesso) {
                window.open(`/api/nfe/xml/${nfceResponse.data.chaveAcesso}`, '_blank');
              }
            }, 500);
          }
        } catch (nfceError: any) {
          console.error('Erro ao emitir NFCe:', nfceError);
          toast.error('⚠️ Comanda fechada, mas houve erro na NFCe: ' + 
            (nfceError?.response?.data?.message || 'Erro desconhecido'));
        }
      }

      // Navegar de volta
      setTimeout(() => {
        if (comanda?.numeroMesa) {
          navigate(`/mesa/${comanda.numeroMesa}`);
        } else {
          navigate('/comandas');
        }
      }, emitirNFCe ? 2000 : 500);

    } catch (error: any) {
      console.error('Erro ao finalizar comanda:', error);
      toast.error(error?.response?.data?.message || 'Erro ao finalizar comanda');
    } finally {
      setProcessando(false);
    }
  };

  if (loading) {
    return (
      <div className="flex items-center justify-center min-h-screen bg-gray-100">
        <div className="text-center">
          <div className="animate-spin rounded-full h-12 w-12 border-b-2 border-blue-600 mx-auto"></div>
          <p className="mt-4 text-gray-600">Carregando comanda...</p>
        </div>
      </div>
    );
  }

  if (!comanda) {
    return (
      <div className="flex items-center justify-center min-h-screen bg-gray-100">
        <div className="text-center">
          <p className="text-red-600 text-lg">Comanda não encontrada</p>
          <button 
            onClick={() => navigate('/comandas')}
            className="mt-4 px-4 py-2 bg-blue-600 text-white rounded hover:bg-blue-700"
          >
            Voltar
          </button>
        </div>
      </div>
    );
  }

  const total = calcularTotal();
  const troco = calcularTroco();

  return (
    <div className="min-h-screen bg-gray-100 py-8">
      <div className="max-w-4xl mx-auto px-4">
        {/* Header */}
        <div className="bg-white rounded-lg shadow-md p-6 mb-6">
          <div className="flex justify-between items-center mb-4">
            <h1 className="text-2xl font-bold text-gray-800">
              Fechamento de Comanda #{id}
            </h1>
            <button
              onClick={() => navigate(-1)}
              className="text-gray-600 hover:text-gray-800"
            >
              ← Voltar
            </button>
          </div>
          {comanda.numeroMesa && (
            <p className="text-gray-600">Mesa: {comanda.numeroMesa}</p>
          )}
        </div>

        {/* Itens da Comanda */}
        <div className="bg-white rounded-lg shadow-md p-6 mb-6">
          <h2 className="text-xl font-semibold text-gray-800 mb-4">Itens da Comanda</h2>
          
          <div className="overflow-x-auto">
            <table className="min-w-full divide-y divide-gray-200">
              <thead className="bg-gray-50">
                <tr>
                  <th className="px-4 py-3 text-left text-xs font-medium text-gray-500 uppercase">Produto</th>
                  <th className="px-4 py-3 text-center text-xs font-medium text-gray-500 uppercase">Qtd</th>
                  <th className="px-4 py-3 text-right text-xs font-medium text-gray-500 uppercase">Preço Un.</th>
                  <th className="px-4 py-3 text-right text-xs font-medium text-gray-500 uppercase">Total</th>
                </tr>
              </thead>
              <tbody className="bg-white divide-y divide-gray-200">
                {comanda.itens.map((item) => (
                  <tr key={item.id}>
                    <td className="px-4 py-3 text-sm text-gray-900">{item.produtoNome}</td>
                    <td className="px-4 py-3 text-sm text-center text-gray-900">{item.quantidade}</td>
                    <td className="px-4 py-3 text-sm text-right text-gray-900">
                      R$ {item.precoVenda.toFixed(2)}
                    </td>
                    <td className="px-4 py-3 text-sm text-right font-medium text-gray-900">
                      R$ {item.total.toFixed(2)}
                    </td>
                  </tr>
                ))}
              </tbody>
              <tfoot className="bg-gray-50">
                <tr>
                  <td colSpan={3} className="px-4 py-3 text-right text-lg font-bold text-gray-900">
                    TOTAL:
                  </td>
                  <td className="px-4 py-3 text-right text-lg font-bold text-green-600">
                    R$ {total.toFixed(2)}
                  </td>
                </tr>
              </tfoot>
            </table>
          </div>
        </div>

        {/* Forma de Pagamento */}
        <div className="bg-white rounded-lg shadow-md p-6 mb-6">
          <h2 className="text-xl font-semibold text-gray-800 mb-4">Forma de Pagamento</h2>
          
          <div className="grid grid-cols-2 md:grid-cols-4 gap-4 mb-6">
            {(['DINHEIRO', 'DEBITO', 'CREDITO', 'PIX'] as FormaPagamento[]).map((forma) => (
              <button
                key={forma}
                onClick={() => setFormaPagamento(forma)}
                className={`px-6 py-4 rounded-lg border-2 font-medium transition-all ${
                  formaPagamento === forma
                    ? 'border-blue-600 bg-blue-50 text-blue-700'
                    : 'border-gray-300 hover:border-blue-400 text-gray-700'
                }`}
              >
                {forma === 'DINHEIRO' && '💵'}
                {forma === 'DEBITO' && '💳'}
                {forma === 'CREDITO' && '💳'}
                {forma === 'PIX' && '📱'}
                <br />
                <span className="text-sm">{forma}</span>
              </button>
            ))}
          </div>

          {/* Valor Recebido (só para dinheiro) */}
          {formaPagamento === 'DINHEIRO' && (
            <div className="mb-4">
              <label className="block text-sm font-medium text-gray-700 mb-2">
                Valor Recebido
              </label>
              <input
                type="text"
                value={valorRecebido}
                onChange={handleValorRecebidoChange}
                placeholder="0,00"
                className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-transparent"
              />
              {valorRecebido && (
                <div className="mt-2 p-3 bg-green-50 border border-green-200 rounded">
                  <p className="text-green-700 font-medium">
                    Troco: R$ {troco.toFixed(2)}
                  </p>
                </div>
              )}
            </div>
          )}
        </div>

        {/* Dados do Cliente (opcional) */}
        <div className="bg-white rounded-lg shadow-md p-6 mb-6">
          <h2 className="text-xl font-semibold text-gray-800 mb-4">Dados do Cliente (Opcional)</h2>
          
          <div className="grid grid-cols-1 md:grid-cols-2 gap-4 mb-4">
            <div>
              <label className="block text-sm font-medium text-gray-700 mb-2">
                Nome do Cliente
              </label>
              <input
                type="text"
                value={nomeCliente}
                onChange={(e) => setNomeCliente(e.target.value)}
                placeholder="Nome completo"
                className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-transparent"
              />
            </div>
            <div>
              <label className="block text-sm font-medium text-gray-700 mb-2">
                CPF (para NFCe)
              </label>
              <input
                type="text"
                value={cpfCliente}
                onChange={handleCPFChange}
                placeholder="000.000.000-00"
                maxLength={14}
                className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-transparent"
              />
            </div>
          </div>

          <div>
            <label className="block text-sm font-medium text-gray-700 mb-2">
              Observações
            </label>
            <textarea
              value={observacoes}
              onChange={(e) => setObservacoes(e.target.value)}
              placeholder="Observações adicionais..."
              rows={3}
              className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-transparent"
            />
          </div>
        </div>

        {/* Emissão NFCe */}
        <div className="bg-white rounded-lg shadow-md p-6 mb-6">
          <div className="flex items-center">
            <input
              type="checkbox"
              id="emitirNFCe"
              checked={emitirNFCe}
              onChange={(e) => setEmitirNFCe(e.target.checked)}
              className="h-5 w-5 text-blue-600 focus:ring-blue-500 border-gray-300 rounded"
            />
            <label htmlFor="emitirNFCe" className="ml-3 text-gray-700 font-medium">
              🧾 Emitir NFC-e (Nota Fiscal do Consumidor Eletrônica)
            </label>
          </div>
          {emitirNFCe && (
            <p className="mt-2 text-sm text-gray-500">
              A NFC-e será emitida automaticamente após o fechamento da comanda.
              {cpfCliente && ' CPF será incluído na nota fiscal.'}
            </p>
          )}
        </div>

        {/* Botões de Ação */}
        <div className="flex gap-4">
          <button
            onClick={() => navigate(-1)}
            disabled={processando}
            className="flex-1 px-6 py-3 bg-gray-500 text-white rounded-lg font-medium hover:bg-gray-600 disabled:opacity-50 disabled:cursor-not-allowed transition-colors"
          >
            Cancelar
          </button>
          <button
            onClick={finalizarComanda}
            disabled={processando}
            className="flex-1 px-6 py-3 bg-green-600 text-white rounded-lg font-medium hover:bg-green-700 disabled:opacity-50 disabled:cursor-not-allowed transition-colors flex items-center justify-center"
          >
            {processando ? (
              <>
                <div className="animate-spin rounded-full h-5 w-5 border-b-2 border-white mr-2"></div>
                Processando...
              </>
            ) : (
              <>
                ✅ Finalizar Comanda{emitirNFCe ? ' e Emitir NFC-e' : ''}
              </>
            )}
          </button>
        </div>

        {/* Resumo fixo no rodapé (mobile) */}
        <div className="md:hidden fixed bottom-0 left-0 right-0 bg-white border-t border-gray-200 p-4 shadow-lg">
          <div className="flex justify-between items-center">
            <span className="text-lg font-bold text-gray-900">Total:</span>
            <span className="text-2xl font-bold text-green-600">R$ {total.toFixed(2)}</span>
          </div>
        </div>
      </div>
    </div>
  );
}

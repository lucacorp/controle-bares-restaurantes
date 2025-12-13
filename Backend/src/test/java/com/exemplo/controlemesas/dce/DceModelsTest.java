package com.exemplo.controlemesas.dce;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Testes para os modelos de dados DC-e (DadosDCe e ItemDCe).
 */
class DceModelsTest {

    @Test
    void testDadosDCeSettersGetters() {
        DadosDCe dados = new DadosDCe();
        
        dados.setNumeroLote(123);
        dados.setCodigoUF(33);
        dados.setSerie(1);
        dados.setNumero(100);
        
        assertEquals(123, dados.getNumeroLote());
        assertEquals(33, dados.getCodigoUF());
        assertEquals(1, dados.getSerie());
        assertEquals(100, dados.getNumero());
    }

    @Test
    void testDadosDCeValoresPadrao() {
        DadosDCe dados = new DadosDCe();
        
        assertEquals(59, dados.getModelo(), "Modelo padrão deve ser 59");
        assertEquals(1, dados.getTipoEmissao(), "Tipo emissão padrão deve ser 1");
        assertEquals(1, dados.getFinalidade(), "Finalidade padrão deve ser 1");
        assertEquals(0, dados.getProcessoEmissao(), "Processo emissão padrão deve ser 0");
        assertEquals(9, dados.getModalidadeFrete(), "Modalidade frete padrão deve ser 9");
    }

    @Test
    void testDadosDCeRemetente() {
        DadosDCe dados = new DadosDCe();
        
        dados.setRemetenteCNPJ("34028316000103");
        dados.setRemetenteNome("Correios");
        dados.setRemetenteUF("RJ");
        dados.setRemetenteCEP("20000-000");
        
        assertEquals("34028316000103", dados.getRemetenteCNPJ());
        assertEquals("Correios", dados.getRemetenteNome());
        assertEquals("RJ", dados.getRemetenteUF());
        assertEquals("20000-000", dados.getRemetenteCEP());
    }

    @Test
    void testDadosDCeDestinatario() {
        DadosDCe dados = new DadosDCe();
        
        dados.setDestinatarioCPF("12345678901");
        dados.setDestinatarioNome("João Silva");
        dados.setDestinatarioUF("SP");
        dados.setDestinatarioCEP("01000-000");
        
        assertEquals("12345678901", dados.getDestinatarioCPF());
        assertEquals("João Silva", dados.getDestinatarioNome());
        assertEquals("SP", dados.getDestinatarioUF());
        assertEquals("01000-000", dados.getDestinatarioCEP());
    }

    @Test
    void testDadosDCeDestinatarioComCNPJ() {
        DadosDCe dados = new DadosDCe();
        
        dados.setDestinatarioCNPJ("12345678000195");
        dados.setDestinatarioCPF(null);
        
        assertEquals("12345678000195", dados.getDestinatarioCNPJ());
        assertNull(dados.getDestinatarioCPF());
    }

    @Test
    void testDadosDCeItens() {
        DadosDCe dados = new DadosDCe();
        
        ItemDCe item1 = new ItemDCe();
        item1.setCodigoProduto("PROD001");
        
        ItemDCe item2 = new ItemDCe();
        item2.setCodigoProduto("PROD002");
        
        dados.setItens(Arrays.asList(item1, item2));
        
        assertNotNull(dados.getItens());
        assertEquals(2, dados.getItens().size());
        assertEquals("PROD001", dados.getItens().get(0).getCodigoProduto());
        assertEquals("PROD002", dados.getItens().get(1).getCodigoProduto());
    }

    @Test
    void testDadosDCeValorTotal() {
        DadosDCe dados = new DadosDCe();
        
        BigDecimal valor = new BigDecimal("1500.50");
        dados.setValorTotal(valor);
        
        assertEquals(valor, dados.getValorTotal());
        assertEquals(0, valor.compareTo(dados.getValorTotal()));
    }

    @Test
    void testDadosDCeDataEmissao() {
        DadosDCe dados = new DadosDCe();
        
        LocalDateTime agora = LocalDateTime.now();
        dados.setDataEmissao(agora);
        
        assertEquals(agora, dados.getDataEmissao());
    }

    @Test
    void testDadosDCeInformacoesPostais() {
        DadosDCe dados = new DadosDCe();
        
        dados.setCodigoRastreio("AA123456789BR");
        dados.setModalidadePostagem("SEDEX");
        dados.setPesoTotal(new BigDecimal("2.5"));
        dados.setObjetoPostal("Caixa");
        
        assertEquals("AA123456789BR", dados.getCodigoRastreio());
        assertEquals("SEDEX", dados.getModalidadePostagem());
        assertEquals(new BigDecimal("2.5"), dados.getPesoTotal());
        assertEquals("Caixa", dados.getObjetoPostal());
    }

    @Test
    void testItemDCeSettersGetters() {
        ItemDCe item = new ItemDCe();
        
        item.setCodigoProduto("PROD001");
        item.setDescricao("Livro");
        item.setNcm("49019900");
        item.setQuantidade(new BigDecimal("2"));
        item.setValorUnitario(new BigDecimal("50.00"));
        item.setValorTotal(new BigDecimal("100.00"));
        
        assertEquals("PROD001", item.getCodigoProduto());
        assertEquals("Livro", item.getDescricao());
        assertEquals("49019900", item.getNcm());
        assertEquals(new BigDecimal("2"), item.getQuantidade());
        assertEquals(new BigDecimal("50.00"), item.getValorUnitario());
        assertEquals(new BigDecimal("100.00"), item.getValorTotal());
    }

    @Test
    void testItemDCeUnidadePadrao() {
        ItemDCe item = new ItemDCe();
        
        assertEquals("UN", item.getUnidade(), "Unidade padrão deve ser UN");
    }

    @Test
    void testItemDCeUnidadeCustomizada() {
        ItemDCe item = new ItemDCe();
        
        item.setUnidade("KG");
        
        assertEquals("KG", item.getUnidade());
    }

    @Test
    void testItemDCePeso() {
        ItemDCe item = new ItemDCe();
        
        BigDecimal peso = new BigDecimal("0.5");
        item.setPeso(peso);
        
        assertEquals(peso, item.getPeso());
    }

    @Test
    void testItemDCeValoresNulos() {
        ItemDCe item = new ItemDCe();
        
        assertNull(item.getCodigoProduto());
        assertNull(item.getDescricao());
        assertNull(item.getNcm());
        assertNull(item.getQuantidade());
        assertNull(item.getValorUnitario());
        assertNull(item.getValorTotal());
    }

    @Test
    void testItemDCeCalculoValorTotal() {
        ItemDCe item = new ItemDCe();
        
        BigDecimal quantidade = new BigDecimal("3");
        BigDecimal valorUnitario = new BigDecimal("25.50");
        BigDecimal valorTotal = quantidade.multiply(valorUnitario);
        
        item.setQuantidade(quantidade);
        item.setValorUnitario(valorUnitario);
        item.setValorTotal(valorTotal);
        
        assertEquals(new BigDecimal("76.50"), item.getValorTotal());
    }

    @Test
    void testDadosDCeEnderecoCompleto() {
        DadosDCe dados = new DadosDCe();
        
        // Endereço remetente completo
        dados.setRemetenteLogradouro("Av. Principal");
        dados.setRemetenteNumero("1000");
        dados.setRemetenteComplemento("Bloco A");
        dados.setRemetenteBairro("Centro");
        dados.setRemetenteCodigoMunicipio("3304557");
        dados.setRemetenteMunicipio("Rio de Janeiro");
        dados.setRemetenteUF("RJ");
        dados.setRemetenteCEP("20000-000");
        
        assertEquals("Av. Principal", dados.getRemetenteLogradouro());
        assertEquals("1000", dados.getRemetenteNumero());
        assertEquals("Bloco A", dados.getRemetenteComplemento());
        assertEquals("Centro", dados.getRemetenteBairro());
        assertEquals("3304557", dados.getRemetenteCodigoMunicipio());
        assertEquals("Rio de Janeiro", dados.getRemetenteMunicipio());
        assertEquals("RJ", dados.getRemetenteUF());
        assertEquals("20000-000", dados.getRemetenteCEP());
    }

    @Test
    void testDadosDCeListaItensVazia() {
        DadosDCe dados = new DadosDCe();
        
        dados.setItens(new ArrayList<>());
        
        assertNotNull(dados.getItens());
        assertTrue(dados.getItens().isEmpty());
        assertEquals(0, dados.getItens().size());
    }

    @Test
    void testItemDCeNCMValido() {
        ItemDCe item = new ItemDCe();
        
        item.setNcm("49019900");
        
        assertEquals(8, item.getNcm().length(), "NCM deve ter 8 dígitos");
        assertTrue(item.getNcm().matches("\\d+"), "NCM deve conter apenas números");
    }

    @Test
    void testDadosDCeAmbienteHomologacao() {
        DadosDCe dados = new DadosDCe();
        
        dados.setTipoAmbiente(2);
        
        assertEquals(2, dados.getTipoAmbiente(), "Ambiente 2 é homologação");
    }

    @Test
    void testDadosDCeAmbienteProducao() {
        DadosDCe dados = new DadosDCe();
        
        dados.setTipoAmbiente(1);
        
        assertEquals(1, dados.getTipoAmbiente(), "Ambiente 1 é produção");
    }

    @Test
    void testDadosDCeCodigoNumerico() {
        DadosDCe dados = new DadosDCe();
        
        dados.setCodigoNumerico(12345678);
        
        assertEquals(12345678, dados.getCodigoNumerico());
        assertTrue(dados.getCodigoNumerico() >= 0);
        assertTrue(dados.getCodigoNumerico() <= 99999999);
    }

    @Test
    void testItemDCeComTodosOsCampos() {
        ItemDCe item = new ItemDCe();
        
        item.setCodigoProduto("PROD123");
        item.setDescricao("Produto Completo");
        item.setNcm("12345678");
        item.setQuantidade(new BigDecimal("5.5"));
        item.setValorUnitario(new BigDecimal("100.00"));
        item.setValorTotal(new BigDecimal("550.00"));
        item.setUnidade("KG");
        item.setPeso(new BigDecimal("2.75"));
        
        assertNotNull(item.getCodigoProduto());
        assertNotNull(item.getDescricao());
        assertNotNull(item.getNcm());
        assertNotNull(item.getQuantidade());
        assertNotNull(item.getValorUnitario());
        assertNotNull(item.getValorTotal());
        assertNotNull(item.getUnidade());
        assertNotNull(item.getPeso());
        
        assertEquals("PROD123", item.getCodigoProduto());
        assertEquals("KG", item.getUnidade());
    }
}

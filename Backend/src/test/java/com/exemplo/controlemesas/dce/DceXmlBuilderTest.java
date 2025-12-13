package com.exemplo.controlemesas.dce;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Testes para DceXmlBuilder - construção e validação de XML DC-e.
 */
class DceXmlBuilderTest {

    @Test
    void testConstrucaoXmlBasico() {
        DadosDCe dados = criarDadosDCeCompleto();
        
        String xml = DceXmlBuilder.construirXmlDCe(dados);
        
        assertNotNull(xml, "XML não deve ser nulo");
        assertTrue(xml.contains("<?xml version=\"1.0\" encoding=\"UTF-8\"?>"), "Deve ter declaração XML");
        assertTrue(xml.contains("<enviDCe"), "Deve ter tag enviDCe");
        assertTrue(xml.contains("versao=\"1.00\""), "Deve ter versão 1.00");
    }

    @Test
    void testEstruturaPrincipalXml() {
        DadosDCe dados = criarDadosDCeCompleto();
        String xml = DceXmlBuilder.construirXmlDCe(dados);
        
        // Tags principais
        assertTrue(xml.contains("<enviDCe"), "Deve conter enviDCe");
        assertTrue(xml.contains("<DCe>"), "Deve conter DCe");
        assertTrue(xml.contains("<infDCe"), "Deve conter infDCe");
        assertTrue(xml.contains("</infDCe>"), "Deve fechar infDCe");
        assertTrue(xml.contains("</DCe>"), "Deve fechar DCe");
        assertTrue(xml.contains("</enviDCe>"), "Deve fechar enviDCe");
    }

    @Test
    void testSecaoIdentificacao() {
        DadosDCe dados = criarDadosDCeCompleto();
        String xml = DceXmlBuilder.construirXmlDCe(dados);
        
        assertTrue(xml.contains("<ide>"), "Deve ter seção ide");
        assertTrue(xml.contains("<cUF>33</cUF>"), "Deve ter código UF");
        assertTrue(xml.contains("<mod>59</mod>"), "Deve ter modelo 59");
        assertTrue(xml.contains("<serie>1</serie>"), "Deve ter série");
        assertTrue(xml.contains("<nDC>1</nDC>"), "Deve ter número");
        assertTrue(xml.contains("<tpAmb>2</tpAmb>"), "Deve ter tipo ambiente");
        assertTrue(xml.contains("</ide>"), "Deve fechar ide");
    }

    @Test
    void testSecaoRemetente() {
        DadosDCe dados = criarDadosDCeCompleto();
        String xml = DceXmlBuilder.construirXmlDCe(dados);
        
        assertTrue(xml.contains("<rem>"), "Deve ter seção rem");
        assertTrue(xml.contains("<CNPJ>34028316000103</CNPJ>"), "Deve ter CNPJ remetente");
        assertTrue(xml.contains("<xNome>Empresa Correios LTDA</xNome>"), "Deve ter nome remetente");
        assertTrue(xml.contains("<enderRem>"), "Deve ter endereço remetente");
        assertTrue(xml.contains("<UF>RJ</UF>"), "Deve ter UF remetente");
        assertTrue(xml.contains("</rem>"), "Deve fechar rem");
    }

    @Test
    void testSecaoDestinatario() {
        DadosDCe dados = criarDadosDCeCompleto();
        String xml = DceXmlBuilder.construirXmlDCe(dados);
        
        assertTrue(xml.contains("<dest>"), "Deve ter seção dest");
        assertTrue(xml.contains("<CPF>12345678901</CPF>"), "Deve ter CPF destinatário");
        assertTrue(xml.contains("<xNome>João Silva</xNome>"), "Deve ter nome destinatário");
        assertTrue(xml.contains("<enderDest>"), "Deve ter endereço destinatário");
        assertTrue(xml.contains("</dest>"), "Deve fechar dest");
    }

    @Test
    void testItens() {
        DadosDCe dados = criarDadosDCeCompleto();
        String xml = DceXmlBuilder.construirXmlDCe(dados);
        
        assertTrue(xml.contains("<det nItem=\"1\">"), "Deve ter primeiro item");
        assertTrue(xml.contains("<det nItem=\"2\">"), "Deve ter segundo item");
        assertTrue(xml.contains("<prod>"), "Deve ter tag produto");
        assertTrue(xml.contains("<cProd>"), "Deve ter código produto");
        assertTrue(xml.contains("<xProd>"), "Deve ter descrição produto");
        assertTrue(xml.contains("<NCM>"), "Deve ter NCM");
        assertTrue(xml.contains("<qCom>"), "Deve ter quantidade");
        assertTrue(xml.contains("<vUnCom>"), "Deve ter valor unitário");
        assertTrue(xml.contains("<vProd>"), "Deve ter valor total");
    }

    @Test
    void testTotais() {
        DadosDCe dados = criarDadosDCeCompleto();
        String xml = DceXmlBuilder.construirXmlDCe(dados);
        
        assertTrue(xml.contains("<total>"), "Deve ter seção total");
        assertTrue(xml.contains("<vDC>150.00</vDC>"), "Deve ter valor total da DC-e");
        assertTrue(xml.contains("</total>"), "Deve fechar total");
    }

    @Test
    void testTransporte() {
        DadosDCe dados = criarDadosDCeCompleto();
        String xml = DceXmlBuilder.construirXmlDCe(dados);
        
        assertTrue(xml.contains("<transp>"), "Deve ter seção transp");
        assertTrue(xml.contains("<modFrete>9</modFrete>"), "Deve ter modalidade frete");
        assertTrue(xml.contains("</transp>"), "Deve fechar transp");
    }

    @Test
    void testInformacoesAdicionais() {
        DadosDCe dados = criarDadosDCeCompleto();
        String xml = DceXmlBuilder.construirXmlDCe(dados);
        
        assertTrue(xml.contains("<infAdic>"), "Deve ter informações adicionais");
        assertTrue(xml.contains("AA123456789BR"), "Deve ter código de rastreio");
    }

    @Test
    void testChaveAcessoGerada() {
        DadosDCe dados = criarDadosDCeCompleto();
        String xml = DceXmlBuilder.construirXmlDCe(dados);
        
        assertTrue(xml.contains("Id=\"DCe"), "Deve ter ID com chave de acesso");
        
        // Extrai a chave do XML
        int inicio = xml.indexOf("Id=\"DCe") + 7;
        int fim = xml.indexOf("\"", inicio);
        String chave = xml.substring(inicio, fim);
        
        assertEquals(44, chave.length(), "Chave de acesso deve ter 44 dígitos");
        assertTrue(chave.matches("\\d+"), "Chave deve conter apenas números");
    }

    @Test
    void testEscapeCaracteresEspeciais() {
        DadosDCe dados = criarDadosDCeCompleto();
        dados.setRemetenteNome("Empresa & Cia <LTDA>");
        dados.setDestinatarioNome("João \"Silva\" & 'Santos'");
        
        String xml = DceXmlBuilder.construirXmlDCe(dados);
        
        assertTrue(xml.contains("&amp;"), "Deve escapar &");
        assertTrue(xml.contains("&lt;"), "Deve escapar <");
        assertTrue(xml.contains("&gt;"), "Deve escapar >");
        assertTrue(xml.contains("&quot;"), "Deve escapar \"");
        assertTrue(xml.contains("&apos;"), "Deve escapar '");
    }

    @Test
    void testDestinatarioComCNPJ() {
        DadosDCe dados = criarDadosDCeCompleto();
        dados.setDestinatarioCPF(null);
        dados.setDestinatarioCNPJ("12345678000195");
        
        String xml = DceXmlBuilder.construirXmlDCe(dados);
        
        assertTrue(xml.contains("<CNPJ>12345678000195</CNPJ>"), "Deve usar CNPJ do destinatário");
        assertFalse(xml.contains("<CPF>"), "Não deve ter CPF quando tem CNPJ");
    }

    @Test
    void testEnderecoSemComplemento() {
        DadosDCe dados = criarDadosDCeCompleto();
        dados.setRemetenteComplemento(null);
        dados.setDestinatarioComplemento("");
        
        String xml = DceXmlBuilder.construirXmlDCe(dados);
        
        // Deve construir XML sem tag xCpl
        int countXCpl = xml.split("<xCpl>").length - 1;
        assertEquals(0, countXCpl, "Não deve ter tags xCpl quando complemento for vazio/nulo");
    }

    @Test
    void testFormatacaoValoresDecimais() {
        DadosDCe dados = criarDadosDCeCompleto();
        String xml = DceXmlBuilder.construirXmlDCe(dados);
        
        // Valores devem ter 2 casas decimais
        assertTrue(xml.contains("<vUnCom>50.00</vUnCom>"), "Valor unitário deve ter 2 casas decimais");
        assertTrue(xml.contains("<vProd>100.00</vProd>"), "Valor total produto deve ter 2 casas decimais");
        assertTrue(xml.contains("<vDC>150.00</vDC>"), "Valor total DC-e deve ter 2 casas decimais");
    }

    @Test
    void testFormatacaoQuantidade() {
        DadosDCe dados = criarDadosDCeCompleto();
        String xml = DceXmlBuilder.construirXmlDCe(dados);
        
        // Quantidade deve ter 4 casas decimais
        assertTrue(xml.contains("<qCom>2.0000</qCom>"), "Quantidade deve ter 4 casas decimais");
    }

    @Test
    void testCEPSemFormatacao() {
        DadosDCe dados = criarDadosDCeCompleto();
        dados.setRemetenteCEP("20000-000");
        dados.setDestinatarioCEP("21000-000");
        
        String xml = DceXmlBuilder.construirXmlDCe(dados);
        
        assertTrue(xml.contains("<CEP>20000000</CEP>"), "CEP remetente deve estar sem hífen");
        assertTrue(xml.contains("<CEP>21000000</CEP>"), "CEP destinatário deve estar sem hífen");
        // Verifica se não tem hífen dentro das tags CEP
        assertFalse(xml.contains("<CEP>20000-000</CEP>"), "CEP remetente não deve ter hífen");
        assertFalse(xml.contains("<CEP>21000-000</CEP>"), "CEP destinatário não deve ter hífen");
    }

    @Test
    void testNamespaceCorreto() {
        DadosDCe dados = criarDadosDCeCompleto();
        String xml = DceXmlBuilder.construirXmlDCe(dados);
        
        assertTrue(xml.contains("xmlns=\"http://www.portalfiscal.inf.br/dce\""), 
            "Deve ter namespace correto da DC-e");
    }

    // Método auxiliar para criar dados completos de teste
    private DadosDCe criarDadosDCeCompleto() {
        DadosDCe dados = new DadosDCe();
        
        // Identificação
        dados.setNumeroLote(1);
        dados.setCodigoUF(33); // RJ
        dados.setCodigoNumerico(12345678);
        dados.setModelo(59);
        dados.setSerie(1);
        dados.setNumero(1);
        dados.setDataEmissao(LocalDateTime.of(2025, 12, 13, 10, 0, 0));
        dados.setTipoEmissao(1);
        dados.setTipoAmbiente(2);
        dados.setFinalidade(1);
        dados.setProcessoEmissao(0);
        dados.setVersaoAplicativo("Controle Mesas 1.0");
        
        // Remetente
        dados.setRemetenteCNPJ("34028316000103");
        dados.setRemetenteNome("Empresa Correios LTDA");
        dados.setRemetenteLogradouro("Rua Principal");
        dados.setRemetenteNumero("100");
        dados.setRemetenteComplemento("Sala 1");
        dados.setRemetenteBairro("Centro");
        dados.setRemetenteCodigoMunicipio("3304557");
        dados.setRemetenteMunicipio("Rio de Janeiro");
        dados.setRemetenteUF("RJ");
        dados.setRemetenteCEP("20000-000");
        
        // Destinatário
        dados.setDestinatarioCPF("12345678901");
        dados.setDestinatarioNome("João Silva");
        dados.setDestinatarioLogradouro("Rua Secundária");
        dados.setDestinatarioNumero("200");
        dados.setDestinatarioComplemento("Apto 101");
        dados.setDestinatarioBairro("Jardim");
        dados.setDestinatarioCodigoMunicipio("3304557");
        dados.setDestinatarioMunicipio("Rio de Janeiro");
        dados.setDestinatarioUF("RJ");
        dados.setDestinatarioCEP("21000-000");
        
        // Itens
        ItemDCe item1 = new ItemDCe();
        item1.setCodigoProduto("PROD001");
        item1.setDescricao("Livro Técnico");
        item1.setNcm("49019900");
        item1.setQuantidade(new BigDecimal("2.0000"));
        item1.setValorUnitario(new BigDecimal("50.00"));
        item1.setValorTotal(new BigDecimal("100.00"));
        
        ItemDCe item2 = new ItemDCe();
        item2.setCodigoProduto("PROD002");
        item2.setDescricao("Caderno");
        item2.setNcm("48201000");
        item2.setQuantidade(new BigDecimal("1.0000"));
        item2.setValorUnitario(new BigDecimal("50.00"));
        item2.setValorTotal(new BigDecimal("50.00"));
        
        dados.setItens(Arrays.asList(item1, item2));
        
        // Totais
        dados.setValorTotal(new BigDecimal("150.00"));
        
        // Transporte
        dados.setModalidadeFrete(9);
        
        // Informações adicionais
        dados.setCodigoRastreio("AA123456789BR");
        dados.setModalidadePostagem("SEDEX");
        
        return dados;
    }
}

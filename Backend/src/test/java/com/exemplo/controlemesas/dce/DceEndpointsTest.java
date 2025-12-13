package com.exemplo.controlemesas.dce;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Testes para DceEndpoints - validação de URLs e suporte por UF.
 */
class DceEndpointsTest {

    @Test
    void testUFsSuportadas() {
        // Estados que DEVEM suportar DC-e
        String[] ufsComDCe = {"AC", "AL", "AP", "DF", "ES", "PB", "PI", "RJ", "RN", "RO", "RR", "SC", "SE", "TO"};
        
        for (String uf : ufsComDCe) {
            assertTrue(DceEndpoints.ufSuportaDCe(uf), 
                "UF " + uf + " deveria suportar DC-e");
            assertTrue(DceEndpoints.ufSuportaDCe(uf.toLowerCase()), 
                "UF " + uf + " (lowercase) deveria suportar DC-e");
        }
    }

    @Test
    void testUFsNaoSuportadas() {
        // Estados que NÃO suportam DC-e
        String[] ufsSemDCe = {"SP", "MG", "RS", "PR", "BA", "CE", "PE", "GO", "AM", "PA", "MT", "MS", "MA"};
        
        for (String uf : ufsSemDCe) {
            assertFalse(DceEndpoints.ufSuportaDCe(uf), 
                "UF " + uf + " NÃO deveria suportar DC-e");
        }
    }

    @Test
    void testUrlAutorizacaoHomologacao() {
        String url = DceEndpoints.getUrlAutorizacao("RJ", DceEndpoints.Ambiente.HOMOLOGACAO);
        
        assertNotNull(url, "URL não deve ser nula");
        assertTrue(url.startsWith("https://"), "URL deve usar HTTPS");
        assertTrue(url.contains("hom"), "URL de homologação deve conter 'hom'");
        assertTrue(url.contains("DCeRecepcao"), "URL deve apontar para DCeRecepcao");
    }

    @Test
    void testUrlAutorizacaoProducao() {
        String url = DceEndpoints.getUrlAutorizacao("RJ", DceEndpoints.Ambiente.PRODUCAO);
        
        assertNotNull(url, "URL não deve ser nula");
        assertTrue(url.startsWith("https://"), "URL deve usar HTTPS");
        assertFalse(url.contains("hom"), "URL de produção NÃO deve conter 'hom'");
        assertTrue(url.contains("DCeRecepcao"), "URL deve apontar para DCeRecepcao");
    }

    @Test
    void testUrlConsultaReciboHomologacao() {
        String url = DceEndpoints.getUrlConsultaRecibo("ES", DceEndpoints.Ambiente.HOMOLOGACAO);
        
        assertNotNull(url, "URL não deve ser nula");
        assertTrue(url.startsWith("https://"), "URL deve usar HTTPS");
        assertTrue(url.contains("hom"), "URL de homologação deve conter 'hom'");
        assertTrue(url.contains("DCeRetRecepcao"), "URL deve apontar para DCeRetRecepcao");
    }

    @Test
    void testUrlConsultaReciboProducao() {
        String url = DceEndpoints.getUrlConsultaRecibo("DF", DceEndpoints.Ambiente.PRODUCAO);
        
        assertNotNull(url, "URL não deve ser nula");
        assertTrue(url.startsWith("https://"), "URL deve usar HTTPS");
        assertFalse(url.contains("hom"), "URL de produção NÃO deve conter 'hom'");
        assertTrue(url.contains("DCeRetRecepcao"), "URL deve apontar para DCeRetRecepcao");
    }

    @Test
    void testExcecaoParaUFInvalida() {
        assertThrows(IllegalArgumentException.class, () -> {
            DceEndpoints.getUrlAutorizacao("SP", DceEndpoints.Ambiente.HOMOLOGACAO);
        }, "Deve lançar exceção para UF não suportada");
    }

    @Test
    void testExcecaoParaUFInvalidaConsulta() {
        assertThrows(IllegalArgumentException.class, () -> {
            DceEndpoints.getUrlConsultaRecibo("MG", DceEndpoints.Ambiente.PRODUCAO);
        }, "Deve lançar exceção para UF não suportada na consulta");
    }

    @Test
    void testTodasUFsTemUrlsCompletas() {
        String[] ufsComDCe = {"AC", "AL", "AP", "DF", "ES", "PB", "PI", "RJ", "RN", "RO", "RR", "SC", "SE", "TO"};
        
        for (String uf : ufsComDCe) {
            // Homologação
            assertDoesNotThrow(() -> DceEndpoints.getUrlAutorizacao(uf, DceEndpoints.Ambiente.HOMOLOGACAO),
                "UF " + uf + " deve ter URL de autorização (homologação)");
            assertDoesNotThrow(() -> DceEndpoints.getUrlConsultaRecibo(uf, DceEndpoints.Ambiente.HOMOLOGACAO),
                "UF " + uf + " deve ter URL de consulta (homologação)");
            
            // Produção
            assertDoesNotThrow(() -> DceEndpoints.getUrlAutorizacao(uf, DceEndpoints.Ambiente.PRODUCAO),
                "UF " + uf + " deve ter URL de autorização (produção)");
            assertDoesNotThrow(() -> DceEndpoints.getUrlConsultaRecibo(uf, DceEndpoints.Ambiente.PRODUCAO),
                "UF " + uf + " deve ter URL de consulta (produção)");
        }
    }

    @Test
    void testAmbienteEnum() {
        assertEquals(1, DceEndpoints.Ambiente.PRODUCAO.getCodigo());
        assertEquals(2, DceEndpoints.Ambiente.HOMOLOGACAO.getCodigo());
    }

    @Test
    void testCaseSensitivityUF() {
        // Testa case-insensitive
        assertTrue(DceEndpoints.ufSuportaDCe("rj"));
        assertTrue(DceEndpoints.ufSuportaDCe("RJ"));
        assertTrue(DceEndpoints.ufSuportaDCe("Rj"));
        
        String urlLower = DceEndpoints.getUrlAutorizacao("rj", DceEndpoints.Ambiente.HOMOLOGACAO);
        String urlUpper = DceEndpoints.getUrlAutorizacao("RJ", DceEndpoints.Ambiente.HOMOLOGACAO);
        assertEquals(urlLower, urlUpper, "URLs devem ser iguais independente do case");
    }
}

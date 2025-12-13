package com.exemplo.controlemesas.dce;

import lombok.extern.slf4j.Slf4j;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * Construtor de XML para DC-e (Declaração de Conteúdo Eletrônica) versão 1.00.
 * 
 * A DC-e é utilizada pelos Correios para declaração de conteúdo de encomendas postadas.
 */
@Slf4j
public class DceXmlBuilder {

    private static final DateTimeFormatter DATETIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");

    /**
     * Constrói o XML completo da DC-e (não assinado).
     */
    public static String construirXmlDCe(DadosDCe dados) {
        log.info("Construindo XML DC-e número {} série {}", dados.getNumero(), dados.getSerie());

        String chave = gerarChaveAcesso(dados);
        
        StringBuilder xml = new StringBuilder();
        xml.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
        xml.append("<enviDCe xmlns=\"http://www.portalfiscal.inf.br/dce\" versao=\"1.00\">");
        xml.append("<idLote>").append(dados.getNumeroLote()).append("</idLote>");
        xml.append("<DCe>");
        xml.append("<infDCe versao=\"1.00\" Id=\"DCe").append(chave).append("\">");
        
        // Identificação
        xml.append("<ide>");
        xml.append("<cUF>").append(dados.getCodigoUF()).append("</cUF>");
        xml.append("<cDC>").append(dados.getCodigoNumerico()).append("</cDC>");
        xml.append("<mod>").append(dados.getModelo()).append("</mod>"); // 59 para DC-e
        xml.append("<serie>").append(dados.getSerie()).append("</serie>");
        xml.append("<nDC>").append(dados.getNumero()).append("</nDC>");
        xml.append("<dhEmi>").append(dados.getDataEmissao().format(DATETIME_FORMATTER)).append("</dhEmi>");
        xml.append("<tpEmis>").append(dados.getTipoEmissao()).append("</tpEmis>"); // 1=Normal
        xml.append("<cDV>").append(chave.charAt(43)).append("</cDV>");
        xml.append("<tpAmb>").append(dados.getTipoAmbiente()).append("</tpAmb>"); // 1=Prod, 2=Homolog
        xml.append("<finDCe>").append(dados.getFinalidade()).append("</finDCe>"); // 1=Normal
        xml.append("<procEmi>").append(dados.getProcessoEmissao()).append("</procEmi>"); // 0=Aplicativo próprio
        xml.append("<verProc>").append(dados.getVersaoAplicativo()).append("</verProc>");
        xml.append("</ide>");

        // Remetente (quem está enviando - geralmente os Correios)
        xml.append("<rem>");
        xml.append("<CNPJ>").append(dados.getRemetenteCNPJ()).append("</CNPJ>");
        xml.append("<xNome>").append(escaparXml(dados.getRemetenteNome())).append("</xNome>");
        xml.append("<enderRem>");
        xml.append("<xLgr>").append(escaparXml(dados.getRemetenteLogradouro())).append("</xLgr>");
        xml.append("<nro>").append(dados.getRemetenteNumero()).append("</nro>");
        if (dados.getRemetenteComplemento() != null && !dados.getRemetenteComplemento().isEmpty()) {
            xml.append("<xCpl>").append(escaparXml(dados.getRemetenteComplemento())).append("</xCpl>");
        }
        xml.append("<xBairro>").append(escaparXml(dados.getRemetenteBairro())).append("</xBairro>");
        xml.append("<cMun>").append(dados.getRemetenteCodigoMunicipio()).append("</cMun>");
        xml.append("<xMun>").append(escaparXml(dados.getRemetenteMunicipio())).append("</xMun>");
        xml.append("<UF>").append(dados.getRemetenteUF()).append("</UF>");
        xml.append("<CEP>").append(dados.getRemetenteCEP().replace("-", "")).append("</CEP>");
        xml.append("</enderRem>");
        xml.append("</rem>");

        // Destinatário (cliente que receberá a encomenda)
        xml.append("<dest>");
        if (dados.getDestinatarioCNPJ() != null && !dados.getDestinatarioCNPJ().isEmpty()) {
            xml.append("<CNPJ>").append(dados.getDestinatarioCNPJ()).append("</CNPJ>");
        } else if (dados.getDestinatarioCPF() != null && !dados.getDestinatarioCPF().isEmpty()) {
            xml.append("<CPF>").append(dados.getDestinatarioCPF()).append("</CPF>");
        }
        xml.append("<xNome>").append(escaparXml(dados.getDestinatarioNome())).append("</xNome>");
        xml.append("<enderDest>");
        xml.append("<xLgr>").append(escaparXml(dados.getDestinatarioLogradouro())).append("</xLgr>");
        xml.append("<nro>").append(dados.getDestinatarioNumero()).append("</nro>");
        if (dados.getDestinatarioComplemento() != null && !dados.getDestinatarioComplemento().isEmpty()) {
            xml.append("<xCpl>").append(escaparXml(dados.getDestinatarioComplemento())).append("</xCpl>");
        }
        xml.append("<xBairro>").append(escaparXml(dados.getDestinatarioBairro())).append("</xBairro>");
        xml.append("<cMun>").append(dados.getDestinatarioCodigoMunicipio()).append("</cMun>");
        xml.append("<xMun>").append(escaparXml(dados.getDestinatarioMunicipio())).append("</xMun>");
        xml.append("<UF>").append(dados.getDestinatarioUF()).append("</UF>");
        xml.append("<CEP>").append(dados.getDestinatarioCEP().replace("-", "")).append("</CEP>");
        xml.append("</enderDest>");
        xml.append("</dest>");

        // Itens (produtos/conteúdo da encomenda)
        int itemNum = 1;
        for (ItemDCe item : dados.getItens()) {
            xml.append("<det nItem=\"").append(itemNum++).append("\">");
            xml.append("<prod>");
            xml.append("<cProd>").append(item.getCodigoProduto()).append("</cProd>");
            xml.append("<xProd>").append(escaparXml(item.getDescricao())).append("</xProd>");
            xml.append("<NCM>").append(item.getNcm()).append("</NCM>");
            xml.append("<qCom>").append(formatarDecimal(item.getQuantidade(), 4)).append("</qCom>");
            xml.append("<vUnCom>").append(formatarDecimal(item.getValorUnitario(), 2)).append("</vUnCom>");
            xml.append("<vProd>").append(formatarDecimal(item.getValorTotal(), 2)).append("</vProd>");
            xml.append("</prod>");
            xml.append("</det>");
        }

        // Totais
        xml.append("<total>");
        xml.append("<vDC>").append(formatarDecimal(dados.getValorTotal(), 2)).append("</vDC>");
        xml.append("</total>");

        // Dados do transporte/postagem
        xml.append("<transp>");
        xml.append("<modFrete>").append(dados.getModalidadeFrete()).append("</modFrete>"); // 9=Sem frete
        xml.append("</transp>");

        // Dados específicos dos Correios (informações postais)
        xml.append("<infAdic>");
        if (dados.getCodigoRastreio() != null && !dados.getCodigoRastreio().isEmpty()) {
            xml.append("<infCpl>Código de Rastreio: ").append(dados.getCodigoRastreio()).append("</infCpl>");
        }
        xml.append("</infAdic>");

        xml.append("</infDCe>");
        xml.append("</DCe>");
        xml.append("</enviDCe>");

        log.info("XML DC-e construído com sucesso. Chave: {}", chave);
        
        return xml.toString();
    }

    /**
     * Gera a chave de acesso de 44 dígitos da DC-e.
     * Formato: UF (2) + AAMM (4) + CNPJ (14) + Mod (2) + Série (3) + Número (9) + Tipo Emissão (1) + Código (8) + DV (1)
     */
    private static String gerarChaveAcesso(DadosDCe dados) {
        StringBuilder chave = new StringBuilder();
        
        // UF (2 dígitos)
        chave.append(String.format("%02d", dados.getCodigoUF()));
        
        // Ano e Mês de emissão (4 dígitos - AAMM)
        String anoMes = dados.getDataEmissao().format(DateTimeFormatter.ofPattern("yyMM"));
        chave.append(anoMes);
        
        // CNPJ do emitente (14 dígitos)
        chave.append(dados.getRemetenteCNPJ());
        
        // Modelo (2 dígitos) - 59 para DC-e
        chave.append(String.format("%02d", dados.getModelo()));
        
        // Série (3 dígitos)
        chave.append(String.format("%03d", dados.getSerie()));
        
        // Número da DC-e (9 dígitos)
        chave.append(String.format("%09d", dados.getNumero()));
        
        // Tipo de emissão (1 dígito)
        chave.append(dados.getTipoEmissao());
        
        // Código numérico (8 dígitos)
        chave.append(String.format("%08d", dados.getCodigoNumerico()));
        
        // Calcula o dígito verificador
        int dv = calcularDigitoVerificador(chave.toString());
        chave.append(dv);
        
        return chave.toString();
    }

    /**
     * Calcula o dígito verificador da chave de acesso usando Módulo 11.
     */
    private static int calcularDigitoVerificador(String chave) {
        int soma = 0;
        int peso = 2;
        
        for (int i = chave.length() - 1; i >= 0; i--) {
            int digito = Character.getNumericValue(chave.charAt(i));
            soma += digito * peso;
            peso++;
            if (peso > 9) {
                peso = 2;
            }
        }
        
        int resto = soma % 11;
        int dv = 11 - resto;
        
        if (dv == 0 || dv == 1 || dv >= 10) {
            return 0;
        }
        
        return dv;
    }

    /**
     * Formata um BigDecimal com o número especificado de casas decimais.
     */
    private static String formatarDecimal(BigDecimal valor, int casasDecimais) {
        return String.format("%." + casasDecimais + "f", valor);
    }

    /**
     * Escapa caracteres especiais XML.
     */
    private static String escaparXml(String texto) {
        if (texto == null) {
            return "";
        }
        return texto.replace("&", "&amp;")
                    .replace("<", "&lt;")
                    .replace(">", "&gt;")
                    .replace("\"", "&quot;")
                    .replace("'", "&apos;");
    }
}

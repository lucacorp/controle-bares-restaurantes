package com.exemplo.controlemesas.sat;

import com.exemplo.controlemesas.model.ComandaResumo;
import com.exemplo.controlemesas.model.ItemComandaResumo;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.Random;

/**
 * Gera XML compatível com CFe-SAT 0.08.07 para uso com ACBr/Emulador.
 */
public final class SatXmlBuilder {

    private SatXmlBuilder() {}

    public static String buildCFeVenda(ComandaResumo r) {
        try {
            StringBuilder sb = new StringBuilder();

            sb.append("<?xml version=\"1.0\" encoding=\"ISO-8859-1\"?>");
            sb.append("<CFe><infCFe versaoDadosEnt=\"0.08.07\">");

            sb.append("<ide>")
                .append("<cUF>35</cUF>")
                .append("<cNF>").append(String.format("%08d", new Random().nextInt(100000000))).append("</cNF>")
                .append("<mod>59</mod>")
                .append("<dEmi>").append(LocalDate.now().format(DateTimeFormatter.BASIC_ISO_DATE)).append("</dEmi>")
                .append("<hEmi>").append(LocalTime.now().format(DateTimeFormatter.ofPattern("HHmmss"))).append("</hEmi>")
                .append("<tpAmb>2</tpAmb>")
                .append("</ide>");

            sb.append("<emit>")
                .append("<CNPJ>12345678000186</CNPJ>")
                .append("<IE>123456789</IE>")
                .append("<xNome>Empresa Exemplo Ltda</xNome>")
                .append("<cRegTrib>3</cRegTrib>")
                .append("<IM>12345</IM>")
                .append("</emit>");

            sb.append("<dest>")
                .append("<xNome>").append(r.getNomeCliente() == null || r.getNomeCliente().isBlank() ? "CONSUMIDOR" : r.getNomeCliente()).append("</xNome>")
                .append("</dest>");

            int seq = 1;
            for (ItemComandaResumo it : r.getItens()) {
                sb.append("<det nItem=\"").append(seq++).append("\">")
                    .append("<prod>")
                    .append("<cProd>").append(it.getItemNo()).append("</cProd>")
                    .append("<xProd>").append(it.getDescricao()).append("</xProd>")
                    .append("<CFOP>").append(it.getCfop()).append("</CFOP>")
                    .append("<uCom>").append(it.getUnMedida()).append("</uCom>")
                    .append("<qCom>").append(String.format(Locale.US, "%.3f", it.getQuantidade())).append("</qCom>")
                    .append("<vUnCom>").append(String.format(Locale.US, "%.2f", it.getPrecoUnitario())).append("</vUnCom>")
                    .append("<indRegra>A</indRegra>")
                    .append("</prod>")
                    .append("<imposto>")
                    .append("<ICMS><ICMS00>")
                    .append("<Orig>").append(it.getOrigem()).append("</Orig>")
                    .append("<CST>").append(it.getCst()).append("</CST>");
                if (!"102".equals(it.getCst()) && !"300".equals(it.getCst()) && !"400".equals(it.getCst())) {
                    sb.append("<pICMS>").append(String.format(Locale.US, "%.2f", it.getAliqIcms())).append("</pICMS>");
                }
                sb.append("</ICMS00></ICMS>")
                    .append("<PIS><PISAliq>")
                    .append("<CST>01</CST>")
                    .append("<vBC>").append(String.format(Locale.US, "%.2f", it.getPrecoUnitario())).append("</vBC>")
                    .append("<pPIS>").append(String.format(Locale.US, "%.2f", it.getAliqPis())).append("</pPIS>")
                    .append("</PISAliq></PIS>")
                    .append("<COFINS><COFINSAliq>")
                    .append("<CST>01</CST>")
                    .append("<vBC>").append(String.format(Locale.US, "%.2f", it.getPrecoUnitario())).append("</vBC>")
                    .append("<pCOFINS>").append(String.format(Locale.US, "%.2f", it.getAliqCofins())).append("</pCOFINS>")
                    .append("</COFINSAliq></COFINS>")
                    .append("</imposto>")
                    .append("</det>");
            }

            double totalTributosAprox = r.getItens().stream().mapToDouble(it ->
                    it.getQuantidade().doubleValue() * it.getPrecoUnitario().doubleValue() *
                            ((it.getAliqPis().doubleValue() + it.getAliqCofins().doubleValue()) / 100.0)
            ).sum();

            sb.append("<total>")
                .append("<vCFeLei12741>").append(String.format(Locale.US, "%.2f", totalTributosAprox)).append("</vCFeLei12741>")
                .append("</total>");

            sb.append("<pag>")
                .append("<MP>")
                .append("<cMP>01</cMP>")
                .append("<vMP>").append(String.format(Locale.US, "%.2f", r.getTotal())).append("</vMP>")
                .append("</MP>")
                .append("</pag>");

            sb.append("<infAdic><infCpl>Obrigado pela preferencia!</infCpl></infAdic>");
            sb.append("</infCFe></CFe>");

            return sb.toString();
        } catch (Exception e) {
            throw new RuntimeException("Erro ao gerar XML SAT: " + e.getMessage(), e);
        }
    }
}

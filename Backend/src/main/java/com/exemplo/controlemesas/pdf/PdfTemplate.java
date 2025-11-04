package com.exemplo.controlemesas.pdf;

import com.exemplo.controlemesas.model.ComandaResumo;
import java.util.stream.Collectors;

public class PdfTemplate {

    public static String gerarHtml(ComandaResumo resumo) {
        StringBuilder html = new StringBuilder();
        html.append("<html><head><style>");
        html.append("body { font-family: monospace; font-size: 10pt; }");
        html.append("</style></head><body>");
        html.append("<h2 style='text-align:center;'>Cupom Fiscal</h2>");
        html.append("<p>Comanda: #" + resumo.getComanda().getId() + "</p>");
        html.append("<p>Data: " + resumo.getDataFechamento() + "</p>");
        html.append("<p>Cliente: " + resumo.getNomeCliente() + "</p>");
        html.append("<hr/>");

        for (var it : resumo.getItens()) {
            html.append("<div>");
            html.append(it.getQuantidade() + "x " + it.getDescricao() + " - R$ " + it.getSubtotal());
            html.append("</div>");
        }

        html.append("<hr/>");
        html.append("<p><strong>Total: R$ " + resumo.getTotal() + "</strong></p>");
        html.append("</body></html>");
        return html.toString();
    }
}

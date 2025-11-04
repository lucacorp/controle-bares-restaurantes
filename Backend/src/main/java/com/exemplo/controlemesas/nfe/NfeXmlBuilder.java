package com.exemplo.controlemesas.nfe;

import com.exemplo.controlemesas.dto.ConfiguracaoEnderecoDTO;
import com.exemplo.controlemesas.model.ComandaResumo;
import com.exemplo.controlemesas.model.ItemComandaResumo;
import com.exemplo.controlemesas.services.ConfiguracaoService;

import java.math.BigDecimal;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Random;

public class NfeXmlBuilder {

    private static final DateTimeFormatter FORMATO_DATA_EMISSAO = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ssXXX");
    private static final Random RANDOM = new Random();

    public static String buildNFe(ComandaResumo resumo, ConfiguracaoService cfg) {
        ZonedDateTime data = resumo.getDataFechamento().atZone(ZoneId.systemDefault());
        String dataEmissao = FORMATO_DATA_EMISSAO.format(data);

        // 1. DADOS DO EMITENTE
        String cnpj = cfg.get("empresa.cnpj", "00000000000000").replaceAll("\\D", "");
        String razao = cfg.get("empresa.razaoSocial", "Empresa Exemplo");
        String fantasia = cfg.get("empresa.nomeFantasia", "Fantasia");
        String ie = cfg.get("empresa.ie", "ISENTO").replaceAll("\\D", "");
        ConfiguracaoEnderecoDTO end = cfg.getEnderecoEmpresa();

        // 2. DADOS DA NOTA
        String cUF = "35";
        String aamm = String.format("%ty%<tm", resumo.getDataFechamento());
        String mod = "65";
        String serie = "1";
        String nNF = String.format("%09d", resumo.getId());
        String tpEmis = "1";
        String cNF = String.format("%08d", RANDOM.nextInt(100_000_000));

        // 3. CÁLCULO DA CHAVE DE ACESSO
        String baseChave = cUF + aamm + cnpj + mod + String.format("%03d", Integer.parseInt(serie)) + nNF + tpEmis + cNF;
        String cDV = String.valueOf(calcularDigitoVerificador(baseChave));
        String chave = baseChave + cDV;
        String id = "NFe" + chave;

        // 4. CONSTRUÇÃO DO XML
        StringBuilder sb = new StringBuilder();
        sb.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
        sb.append("<NFe xmlns=\"http://www.portalfiscal.inf.br/nfe\">");
        sb.append("<infNFe versao=\"4.00\" Id=\"").append(id).append("\">");

        // ide - Identificação da Nota
        sb.append("<ide>");
        sb.append("<cUF>").append(cUF).append("</cUF>");
        sb.append("<cNF>").append(cNF).append("</cNF>");
        sb.append("<natOp>VENDA</natOp>");
        sb.append("<mod>").append(mod).append("</mod>");
        sb.append("<serie>").append(serie).append("</serie>");
        sb.append("<nNF>").append(Long.parseLong(nNF)).append("</nNF>");
        sb.append("<dhEmi>").append(dataEmissao).append("</dhEmi>");
        sb.append("<tpNF>1</tpNF><idDest>1</idDest><cMunFG>").append(getCodigoMunicipio(end.getCidade())).append("</cMunFG>");
        sb.append("<tpImp>4</tpImp><tpEmis>").append(tpEmis).append("</tpEmis><cDV>").append(cDV).append("</cDV><tpAmb>2</tpAmb>");
        sb.append("<finNFe>1</finNFe><indFinal>1</indFinal><indPres>1</indPres>");
        sb.append("<procEmi>0</procEmi><verProc>1.0</verProc>");
        sb.append("</ide>");

        // emit - Emitente
        sb.append("<emit>");
        sb.append("<CNPJ>").append(cnpj).append("</CNPJ><xNome>").append(razao).append("</xNome><xFant>").append(fantasia).append("</xFant>");
        sb.append("<enderEmit>");
        sb.append("<xLgr>").append(end.getLogradouro()).append("</xLgr>");
        sb.append("<nro>").append(end.getNumero()).append("</nro>");
        sb.append("<xBairro>").append(end.getBairro()).append("</xBairro>");
        sb.append("<cMun>").append(getCodigoMunicipio(end.getCidade())).append("</cMun>");
        sb.append("<xMun>").append(end.getCidade() != null ? end.getCidade().toUpperCase() : "").append("</xMun>"); // ✅ GARANTE VALOR NAO NULO
        sb.append("<UF>").append(end.getUf().toUpperCase()).append("</UF>");
        sb.append("<CEP>").append(end.getCep().replaceAll("\\D", "")).append("</CEP>");
        sb.append("<cPais>1058</cPais><xPais>BRASIL</xPais>");
        sb.append("</enderEmit>");
        sb.append("<IE>").append(ie).append("</IE><CRT>1</CRT>");
        sb.append("</emit>");

        // dest - Destinatário
        sb.append("<dest><xNome>").append(resumo.getNomeCliente()).append("</xNome></dest>");

        // det - Detalhes dos Produtos
        int item = 1;
        BigDecimal totalProdutos = BigDecimal.ZERO; // ✅ VARIÁVEL PARA O VALOR TOTAL DOS PRODUTOS
        for (ItemComandaResumo i : resumo.getItens()) {
            sb.append("<det nItem=\"").append(item++).append("\">");
            sb.append("<prod><cProd>").append(i.getItemNo()).append("</cProd><xProd>").append(i.getDescricao()).append("</xProd>");
            sb.append("<NCM>").append(i.getNcm()).append("</NCM><CFOP>").append(i.getCfop()).append("</CFOP>");
            sb.append("<uCom>").append(i.getUnMedida()).append("</uCom><qCom>").append(i.getQuantidade()).append("</qCom>");
            sb.append("<vUnCom>").append(i.getPrecoUnitario()).append("</vUnCom><vProd>").append(i.getSubtotal()).append("</vProd><indTot>1</indTot></prod>");

            sb.append("<imposto>");
            sb.append("<ICMS><ICMSSN102><orig>").append(i.getOrigem()).append("</orig><CSOSN>").append(i.getCst()).append("</CSOSN></ICMSSN102></ICMS>");
            sb.append("<PIS><PISAliq><CST>01</CST><vBC>").append(i.getSubtotal()).append("</vBC><pPIS>").append(i.getAliqPis()).append("</pPIS><vPIS>").append(i.getValorPis()).append("</vPIS></PISAliq></PIS>");
            sb.append("<COFINS><COFINSAliq><CST>01</CST><vBC>").append(i.getSubtotal()).append("</vBC><pCOFINS>").append(i.getAliqCofins()).append("</pCOFINS><vCOFINS>").append(i.getValorCofins()).append("</vCOFINS></COFINSAliq></COFINS>");
            sb.append("</imposto></det>");
            
            totalProdutos = totalProdutos.add(i.getSubtotal()); // ✅ ACUMULA O TOTAL DOS PRODUTOS
        }

        // total - Totais da Nota
        sb.append("<total><ICMSTot><vBC>0.00</vBC><vICMS>0.00</vICMS>");
        sb.append("<vProd>").append(totalProdutos).append("</vProd>"); // ✅ CORRIGIDO: Usa o valor calculado
        sb.append("<vNF>").append(resumo.getTotal()).append("</vNF></ICMSTot></total>");

        // pag - Pagamento
        sb.append("<pag><detPag><tPag>01</tPag><vPag>").append(resumo.getTotal()).append("</vPag></detPag></pag>");

        // infAdic - Informações Adicionais
        sb.append("<infAdic><infCpl>Obrigado pela preferência!</infCpl></infAdic>");

        sb.append("</infNFe></NFe>");

        return sb.toString();
    }

    private static int calcularDigitoVerificador(String chave43) {
        int soma = 0;
        int peso = 2;
        for (int i = chave43.length() - 1; i >= 0; i--) {
            int num = Character.getNumericValue(chave43.charAt(i));
            soma += num * peso;
            peso = (peso == 9) ? 2 : peso + 1;
        }
        int mod = soma % 11;
        return (mod == 0 || mod == 1) ? 0 : 11 - mod;
    }

    private static String getCodigoMunicipio(String nomeCidade) {
        if (nomeCidade == null) return "3549706";
        String cidade = nomeCidade.trim().toUpperCase();
        return switch (cidade) {
            case "SÃO PAULO" -> "3550308";
            case "SÃO JOSÉ DO RIO PRETO" -> "3549805";
            case "SÃO JOSÉ DO RIO PARDO" -> "3549706";
            case "RIBEIRÃO PRETO" -> "3543402";
            default -> "3549706";
        };
    }
}

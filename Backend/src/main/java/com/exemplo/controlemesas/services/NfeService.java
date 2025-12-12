package com.exemplo.controlemesas.services;

import com.exemplo.controlemesas.model.ComandaResumo;
import com.exemplo.controlemesas.repository.ComandaResumoRepository;
import com.exemplo.controlemesas.util.FilesHelper;
import com.exemplo.controlemesas.util.PdfUtils;
import com.exemplo.controlemesas.nfe.NfeXmlBuilder;
import com.exemplo.controlemesas.nfe.AssinaturaDigital;
import com.exemplo.controlemesas.nfe.SefazSoapClient;
import com.exemplo.controlemesas.nfe.SefazEndpoints;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class NfeService {

    private final ComandaResumoRepository resumoRepo;
    private final ConfiguracaoService cfg;
    private final AssinaturaDigital assinaturaDigital;
    private final SefazSoapClient sefazClient;
    private final com.exemplo.controlemesas.nfe.NfeXmlValidator xmlValidator;

    public ComandaResumo emitir(long resumoId) throws IOException {
        ComandaResumo resumo = resumoRepo.findById(resumoId)
                .orElseThrow(() -> {
                    log.error("Resumo não encontrado para o id: {}", resumoId);
                    return new IllegalArgumentException("Resumo não encontrado");
                });

        if ("ENVIADO".equalsIgnoreCase(resumo.getStatusSat())) {
            log.warn("Cupom já emitido para o resumo id: {}", resumoId);
            throw new IllegalStateException("Cupom já emitido para esse resumo.");
        }

        // Validação básica dos dados do cliente e itens
        if (resumo.getItens() == null || resumo.getItens().isEmpty()) {
            log.error("Resumo id {} não possui itens para emissão de NFC-e.", resumoId);
            throw new IOException("Resumo sem itens para emissão de NFC-e.");
        }
        if (resumo.getNomeCliente() == null || resumo.getNomeCliente().isBlank()) {
            log.info("Resumo id {} sem nome de cliente, usando padrão 'CONSUMIDOR FINAL'.", resumoId);
        }

        // 1️⃣ Gera o XML da NFC-e
        String xml;
        try {
            xml = NfeXmlBuilder.buildNFe(resumo, cfg);
        } catch (Exception e) {
            log.error("Erro ao gerar XML da NFC-e para resumo id {}: {}", resumoId, e.getMessage(), e);
            throw new IOException("Falha ao gerar XML da NFC-e", e);
        }

        // 2️⃣ Assina digitalmente o XML
        String xmlAssinado;
        try {
            xmlAssinado = assinaturaDigital.assinar(xml);
        } catch (Exception e) {
            log.error("Erro ao assinar XML da NFC-e para resumo id {}: {}", resumoId, e.getMessage(), e);
            throw new IOException("Falha ao assinar XML da NFC-e", e);
        }

        // 3️⃣ Extrai a chave da NFe
        String chave;
        try {
            chave = extrairChave(xmlAssinado);
        } catch (Exception e) {
            log.error("Erro ao extrair chave de acesso do XML para resumo id {}: {}", resumoId, e.getMessage(), e);
            throw new IOException("Falha ao extrair chave de acesso do XML", e);
        }

        // 4️⃣ Salva o XML no disco
        Path caminhoXml;
        try {
            caminhoXml = FilesHelper.writeFile(
                    "data/nfe/xml",
                    "NFe_" + chave + ".xml",
                    xmlAssinado,
                    StandardCharsets.UTF_8
            );
            log.info("📄 XML salvo: {}", caminhoXml.toAbsolutePath());
        } catch (Exception e) {
            log.error("Erro ao salvar XML da NFC-e para resumo id {}: {}", resumoId, e.getMessage(), e);
            throw new IOException("Falha ao salvar XML da NFC-e", e);
        }

        // 4.5️⃣ Validação LOCAL do XML (opcional - se schemas XSD disponíveis)
        try {
            log.info("🔍 Validando XML localmente antes de enviar para SEFAZ...");
            // Extrai apenas a tag NFe do lote para validação
            String nfeParaValidar = extrairNFeDoLote(xmlAssinado);
            boolean valido = xmlValidator.validarXml(nfeParaValidar);
            if (!valido) {
                log.warn("⚠️ XML falhou na validação local! Enviando mesmo assim para SEFAZ...");
            } else {
                log.info("✅ XML passou na validação local!");
            }
        } catch (Exception e) {
            log.warn("⚠️ Erro na validação local (continuando): {}", e.getMessage());
        }

        // 5️⃣ Envia para a SEFAZ
        String uf = cfg.get("empresa.uf", "SP");
        boolean homologacao = cfg.getBoolean("nfe.homologacao", true);
        String urlAutorizacao = SefazEndpoints.getUrlAutorizacao(uf, homologacao);

        String respostaAutorizacao;
        try {
            // Monta o lote
            String lote = montarLote(xmlAssinado);
            respostaAutorizacao = sefazClient.enviarNFe(lote, urlAutorizacao);
            log.info("📥 Resposta SEFAZ recebida");
        } catch (Exception e) {
            log.error("Erro de comunicação com SEFAZ para resumo id {}: {}", resumoId, e.getMessage(), e);
            
            resumo.setStatusSat("ERRO");
            resumo.setXmlPath(caminhoXml.toString());
            resumo.setObservacoes("Erro de comunicação com SEFAZ: " + e.getMessage());
            
            try {
                return resumoRepo.save(resumo);
            } catch (Exception ex) {
                log.error("Erro ao persistir resumo id {} após falha de comunicação: {}", resumoId, ex.getMessage(), ex);
                throw new IOException("Falha ao persistir resumo após erro de comunicação com SEFAZ", ex);
            }
        }

        // 6️⃣ Processa resposta
        String codigoStatus;
        String mensagem;
        try {
            codigoStatus = sefazClient.extrairCodigoStatus(respostaAutorizacao);
            mensagem = sefazClient.extrairMensagem(respostaAutorizacao);
            
            log.info("Status SEFAZ: {} - {}", codigoStatus, mensagem);
        } catch (Exception e) {
            log.error("Erro ao processar resposta da SEFAZ para resumo id {}: {}", resumoId, e.getMessage(), e);
            throw new IOException("Falha ao processar resposta da SEFAZ", e);
        }

        // Código 100 = Autorizado o uso da NF-e
        // Código 103 = Lote recebido com sucesso (precisa consultar depois)
        if ("103".equals(codigoStatus)) {
            // Aguarda processamento assíncrono
            try {
                String numeroRecibo = sefazClient.extrairNumeroRecibo(respostaAutorizacao);
                log.info("Lote enviado. Recibo: {}. Aguardando processamento...", numeroRecibo);
                
                Thread.sleep(2000); // Aguarda 2 segundos
                
                String urlConsulta = SefazEndpoints.getUrlConsultaProtocolo(uf, homologacao);
                String respostaConsulta = sefazClient.consultarRecibo(numeroRecibo, urlConsulta);
                
                codigoStatus = sefazClient.extrairCodigoStatus(respostaConsulta);
                mensagem = sefazClient.extrairMensagem(respostaConsulta);
                
                log.info("Status consulta: {} - {}", codigoStatus, mensagem);
            } catch (Exception e) {
                log.error("Erro ao consultar recibo para resumo id {}: {}", resumoId, e.getMessage(), e);
                throw new IOException("Falha ao consultar recibo na SEFAZ", e);
            }
        }

        if (!"100".equals(codigoStatus)) {
            log.error("NFC-e não autorizada para resumo id {}. Código: {} - {}", resumoId, codigoStatus, mensagem);
            
            resumo.setStatusSat("ERRO");
            resumo.setXmlPath(caminhoXml.toString());
            resumo.setObservacoes("SEFAZ: " + codigoStatus + " - " + mensagem);
            
            try {
                return resumoRepo.save(resumo);
            } catch (Exception e) {
                log.error("Erro ao persistir resumo id {} após rejeição SEFAZ: {}", resumoId, e.getMessage(), e);
                throw new IOException("Falha ao persistir resumo após rejeição SEFAZ", e);
            }
        }

        // 7️⃣ Gera DANFE/PDF
        String pdfPath;
        try {
            pdfPath = gerarPdf(xmlAssinado, resumo.getId());
        } catch (Exception e) {
            log.error("Erro ao gerar DANFE/PDF para resumo id {}: {}", resumoId, e.getMessage(), e);
            throw new IOException("Falha ao gerar DANFE/PDF", e);
        }

        // 8️⃣ Atualiza o status e persiste
        resumo.setChaveSat(chave);
        resumo.setStatusSat("ENVIADO");
        resumo.setXmlPath(caminhoXml.toString());
        resumo.setPdfPath(pdfPath);
        resumo.setObservacoes("Autorizado: " + mensagem);

        try {
            return resumoRepo.save(resumo);
        } catch (Exception e) {
            log.error("Erro ao salvar resumo id {} após emissão da NFC-e: {}", resumoId, e.getMessage(), e);
            throw new IOException("Falha ao persistir resumo após emissão da NFC-e", e);
        }
    }

    /**
     * Monta o lote de NF-e para envio.
     */
    private String montarLote(String xmlNFeAssinado) {
        // ID único: timestamp + random para evitar cache SEFAZ
        String idLote = String.valueOf(System.currentTimeMillis()) + 
                        String.valueOf((int)(Math.random() * 10000));
        
        return "<enviNFe xmlns=\"http://www.portalfiscal.inf.br/nfe\" versao=\"4.00\">" +
                "<idLote>" + idLote + "</idLote>" +
                "<indSinc>1</indSinc>" + // Síncrono
                xmlNFeAssinado +
                "</enviNFe>";
    }

    /**
     * Gera o PDF DANFE a partir do XML.
     */
    protected String gerarPdf(String xml, long numero) throws IOException {
        // TODO: Implementar geração de DANFE real
        // Por enquanto, gera um PDF simples com o XML
        
        String html = "<html><body><h1>DANFE - NFC-e</h1><pre>" + 
                      xml.replace("<", "&lt;").replace(">", "&gt;") + 
                      "</pre></body></html>";
        
        byte[] pdf = PdfUtils.htmlToPdf(html);

        Path caminhoPdf = FilesHelper.writeFile(
                "data/nfe/pdf",
                String.format("DANFE_%d.pdf", numero),
                pdf
        );
        log.info("📄 DANFE gerado: {}", caminhoPdf.toAbsolutePath());

        return caminhoPdf.toString();
    }

    /**
     * Extrai a chave de acesso da NFe a partir do XML.
     */
    private String extrairChave(String xml) {
        try {
            var matcher = java.util.regex.Pattern
                    .compile("Id=\"NFe(\\d{44})\"")
                    .matcher(xml);

            if (!matcher.find()) {
                log.error("Chave de acesso não encontrada no XML.");
                throw new IllegalArgumentException("Chave de acesso não encontrada no XML.");
            }

            String chave = matcher.group(1);
            if (chave.length() != 44) {
                log.error("Chave de acesso inválida no XML. Chave: {}", chave);
                throw new IllegalArgumentException("Chave de acesso inválida no XML.");
            }
            return chave;
        } catch (Exception e) {
            log.error("Erro ao extrair chave de acesso do XML: {}", e.getMessage(), e);
            throw e;
        }
    }
    
    /**
     * Extrai apenas a tag NFe do lote para validação contra XSD.
     */
    private String extrairNFeDoLote(String xmlLoteAssinado) {
        try {
            // Extrai apenas o conteúdo entre <NFe> e </NFe>
            int inicio = xmlLoteAssinado.indexOf("<NFe");
            int fim = xmlLoteAssinado.indexOf("</NFe>") + 6;
            
            if (inicio == -1 || fim == 5) {
                log.warn("Tag <NFe> não encontrada no XML para validação");
                return xmlLoteAssinado; // Retorna o XML completo
            }
            
            return xmlLoteAssinado.substring(inicio, fim);
        } catch (Exception e) {
            log.warn("Erro ao extrair NFe do lote: {}", e.getMessage());
            return xmlLoteAssinado;
        }
    }
}

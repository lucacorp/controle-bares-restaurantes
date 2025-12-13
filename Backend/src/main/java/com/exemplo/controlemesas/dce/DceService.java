package com.exemplo.controlemesas.dce;

import com.exemplo.controlemesas.nfe.AssinaturaDigital;
import com.exemplo.controlemesas.nfe.CertificadoDigital;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Serviço principal para emissão de DC-e (Declaração de Conteúdo Eletrônica).
 * 
 * Orquestra todo o processo:
 * 1. Construção do XML
 * 2. Assinatura digital
 * 3. Envio para SEFAZ
 * 4. Consulta de recibo
 * 5. Armazenamento do XML
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class DceService {

    private final CertificadoDigital certificadoDigital;
    private final AssinaturaDigital assinaturaDigital;
    private final DceSoapClient dceSoapClient;

    @Value("${dce.ambiente:2}")
    private Integer ambiente;

    @Value("${dce.uf:SP}")
    private String uf;

    @Value("${dce.diretorio.xml:data/dce/xml}")
    private String diretorioXml;

    /**
     * Emite uma DC-e completa (construção, assinatura, envio).
     */
    public String emitirDCe(DadosDCe dados) throws Exception {
        log.info("=== INICIANDO EMISSÃO DC-e {} ===", dados.getNumero());

        // Valida se a UF suporta DC-e
        if (!DceEndpoints.ufSuportaDCe(uf)) {
            throw new IllegalStateException(
                "UF " + uf + " não suporta DC-e. Estados disponíveis: AC, AL, AP, DF, ES, PB, PI, RJ, RN, RO, RR, SC, SE, TO"
            );
        }

        // Define ambiente
        dados.setTipoAmbiente(ambiente);
        dados.setVersaoAplicativo("Controle Mesas 1.0");

        // 1. Constrói XML
        log.info("Construindo XML DC-e...");
        String xmlNaoAssinado = DceXmlBuilder.construirXmlDCe(dados);
        salvarXml(xmlNaoAssinado, "DCe_" + dados.getNumero() + "_nao_assinado.xml");

        // 2. Assina digitalmente
        log.info("Assinando XML DC-e...");
        String xmlAssinado = assinaturaDigital.assinar(xmlNaoAssinado);
        salvarXml(xmlAssinado, "DCe_" + dados.getNumero() + "_assinado.xml");

        // 3. Envia para SEFAZ
        log.info("Enviando DC-e para SEFAZ...");
        DceEndpoints.Ambiente amb = ambiente == 1 ? 
            DceEndpoints.Ambiente.PRODUCAO : DceEndpoints.Ambiente.HOMOLOGACAO;
        
        String urlAutorizacao = DceEndpoints.getUrlAutorizacao(uf, amb);
        String respostaAutorizacao = dceSoapClient.enviarDCe(xmlAssinado, urlAutorizacao);
        
        salvarXml(respostaAutorizacao, "DCe_" + dados.getNumero() + "_resposta_autorizacao.xml");

        // 4. Processa resposta
        String cStat = dceSoapClient.extrairCodigoStatus(respostaAutorizacao);
        String xMotivo = dceSoapClient.extrairMensagem(respostaAutorizacao);
        
        log.info("Resposta SEFAZ DC-e - cStat: {} - {}", cStat, xMotivo);

        // 5. Se retornou recibo, consulta
        if ("103".equals(cStat)) { // Lote recebido com sucesso
            String numeroRecibo = dceSoapClient.extrairNumeroRecibo(respostaAutorizacao);
            log.info("Recibo DC-e: {}", numeroRecibo);

            // Aguarda processamento (normalmente 2-3 segundos)
            Thread.sleep(3000);

            // Consulta recibo
            String urlConsulta = DceEndpoints.getUrlConsultaRecibo(uf, amb);
            String respostaConsulta = dceSoapClient.consultarRecibo(numeroRecibo, urlConsulta, ambiente);
            
            salvarXml(respostaConsulta, "DCe_" + dados.getNumero() + "_resposta_consulta.xml");

            String cStatConsulta = dceSoapClient.extrairCodigoStatus(respostaConsulta);
            String xMotivoConsulta = dceSoapClient.extrairMensagem(respostaConsulta);
            String chaveAcesso = dceSoapClient.extrairChaveAcesso(respostaConsulta);

            log.info("Consulta recibo DC-e - cStat: {} - {}", cStatConsulta, xMotivoConsulta);

            if ("100".equals(cStatConsulta)) { // Autorizada
                log.info("✅ DC-e AUTORIZADA! Chave: {}", chaveAcesso);
                return chaveAcesso;
            } else {
                log.error("❌ DC-e REJEITADA: {} - {}", cStatConsulta, xMotivoConsulta);
                throw new RuntimeException("DC-e rejeitada: " + cStatConsulta + " - " + xMotivoConsulta);
            }
        } else if ("100".equals(cStat)) { // Autorizada diretamente (raro)
            String chaveAcesso = dceSoapClient.extrairChaveAcesso(respostaAutorizacao);
            log.info("✅ DC-e AUTORIZADA DIRETAMENTE! Chave: {}", chaveAcesso);
            return chaveAcesso;
        } else {
            log.error("❌ Erro ao enviar DC-e: {} - {}", cStat, xMotivo);
            throw new RuntimeException("Erro DC-e: " + cStat + " - " + xMotivo);
        }
    }

    /**
     * Salva XML em arquivo para auditoria.
     */
    private void salvarXml(String xml, String nomeArquivo) {
        try {
            File dir = new File(diretorioXml);
            if (!dir.exists()) {
                dir.mkdirs();
            }

            String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
            String nomeCompleto = timestamp + "_" + nomeArquivo;
            
            File arquivo = new File(dir, nomeCompleto);
            try (FileWriter writer = new FileWriter(arquivo)) {
                writer.write(xml);
            }
            
            log.debug("XML salvo: {}", arquivo.getAbsolutePath());
        } catch (IOException e) {
            log.warn("Não foi possível salvar XML {}: {}", nomeArquivo, e.getMessage());
        }
    }

    /**
     * Valida configurações antes de emitir.
     */
    public void validarConfiguracoes() throws Exception {
        if (!certificadoDigital.isCarregado()) {
            throw new IllegalStateException("Certificado digital não carregado");
        }

        if (!DceEndpoints.ufSuportaDCe(uf)) {
            throw new IllegalStateException("UF " + uf + " não suporta DC-e");
        }

        log.info("Configurações DC-e validadas: UF={}, Ambiente={}", uf, ambiente);
    }
}

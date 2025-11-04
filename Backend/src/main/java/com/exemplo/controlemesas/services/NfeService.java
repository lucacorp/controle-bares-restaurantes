package com.exemplo.controlemesas.services;

import com.exemplo.controlemesas.model.ComandaResumo;
import com.exemplo.controlemesas.repository.ComandaResumoRepository;
import com.exemplo.controlemesas.util.FilesHelper;
import com.exemplo.controlemesas.util.PdfUtils;
import com.exemplo.controlemesas.nfe.NfeXmlBuilder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.*;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.time.format.DateTimeFormatter;
import java.util.Base64;
import java.util.Random;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class NfeService {

    private final ComandaResumoRepository resumoRepo;
    private final ConfiguracaoService cfg;

    private static final Random RND = new Random();
    private static final DateTimeFormatter DF = DateTimeFormatter.BASIC_ISO_DATE;

    public ComandaResumo emitir(long resumoId) throws IOException {
        ComandaResumo resumo = resumoRepo.findById(resumoId)
                .orElseThrow(() -> new IllegalArgumentException("Resumo não encontrado"));

        if ("ENVIADO".equalsIgnoreCase(resumo.getStatusSat())) {
            throw new IllegalStateException("Cupom já emitido para esse resumo.");
        }

        // 1. Geração do XML
        String xml = NfeXmlBuilder.buildNFe(resumo, cfg);

        // 2. Extrai chave da NFe
        String chave = extrairChave(xml);

        // 3. Salva XML no disco
        Path caminhoXml = FilesHelper.writeFile("data/nfe/xml", "NFe_" + chave + ".xml", xml, StandardCharsets.UTF_8);
        log.debug("📝 XML salvo: {}", caminhoXml.toAbsolutePath());

        // 4. Envia comando com parâmetros corretos (assinar, validar, enviar)
        String cmd = "NFe.CriarEnviarNFe(\"" + caminhoXml.toAbsolutePath() + "\",1,1,1)";
        String resp = enviarComando(cmd);
        log.debug("📥 Resposta ACBr: {}", resp);

        if (resp == null || !resp.startsWith("OK:")) {
            throw new IOException("Erro ao enviar NFC-e: " + resp);
        }

        String[] campos = resp.split("\\|");
        if (campos.length < 3) {
            throw new IOException("Resposta incompleta da SEFAZ: " + resp);
        }

        String codigo = campos[1];
        if (!"100".equals(codigo)) {
            throw new IOException("Erro da SEFAZ: " + resp);
        }

        // 5. Atualiza resumo com paths e status
        resumo.setChaveSat(chave);
        resumo.setStatusSat("ENVIADO");
        resumo.setXmlPath(caminhoXml.toString());
        resumo.setPdfPath(gerarPdf(chave, resumo.getId()));

        return resumoRepo.save(resumo);
    }

    private String enviarComando(String cmd) throws IOException {
        String host = cfg.get("sat.ip", "127.0.0.1");
        int port = cfg.getInt("sat.port", 3434);
        int timeout = 10000;

        log.debug("➡️ Enviando comando ACBr:\n{}", cmd);

        try (Socket socket = new Socket()) {
            socket.connect(new InetSocketAddress(host, port), timeout);
            socket.setSoTimeout(timeout);

            try (
                BufferedWriter out = new BufferedWriter(
                    new OutputStreamWriter(socket.getOutputStream(), StandardCharsets.ISO_8859_1));
                BufferedReader in = new BufferedReader(
                    new InputStreamReader(socket.getInputStream(), StandardCharsets.ISO_8859_1))
            ) {
                out.write(cmd);
                out.write("\r\n.\r\n"); // terminador obrigatório
                out.flush();

                String banner = in.readLine();
                String resposta = in.readLine();

                log.debug("📥 Resposta ACBr (banner): {}", banner);
                log.debug("📥 Resposta ACBr (real): {}", resposta);

                return (resposta == null || resposta.trim().isEmpty()) ? banner : resposta;
            }
        } catch (IOException e) {
            log.error("❌ Erro ao enviar comando ACBr: {}", e.getMessage(), e);
            throw e;
        }
    }

    private String gerarPdf(String chave, long numero) throws IOException {
        String resp = enviarComando("NFe.ImprimirDANFE(" + chave + ")");
        if (resp != null && resp.startsWith("OK:")) {
            return resp.substring(3);
        }

        // fallback: gerar HTML + PDF
        String html = enviarComando("NFe.GerarDanfe(" + chave + ")");
        if (html == null || !html.startsWith("OK:")) {
            throw new IOException("Erro ao gerar DANFE: " + html);
        }

        byte[] pdf = PdfUtils.htmlToPdf(html.substring(3));
        return FilesHelper.writeFile("data/nfe/pdf",
                "DANFE_" + numero + ".pdf",
                pdf).toString();
    }

    private String extrairChave(String xml) {
        int i = xml.indexOf("Id=\"NFe");
        if (i == -1) throw new IllegalArgumentException("Chave não encontrada no XML");
        return xml.substring(i + 7, i + 51); // "NFe" + 44 dígitos
    }
}

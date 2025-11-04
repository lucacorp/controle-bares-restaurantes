package com.exemplo.controlemesas.services;

import com.exemplo.controlemesas.model.ComandaResumo;
import com.exemplo.controlemesas.repository.ComandaResumoRepository;
import com.exemplo.controlemesas.sat.SatXmlBuilder;
import com.exemplo.controlemesas.util.FilesHelper;
import com.exemplo.controlemesas.util.PdfUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.*;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Base64;
import java.util.Random;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class SatService {

    private final ComandaResumoRepository resumoRepo;
    private final ConfiguracaoService cfg;

    private static final Random RND = new Random();
    private static final DateTimeFormatter DF = DateTimeFormatter.BASIC_ISO_DATE;

    public ComandaResumo emitir(long resumoId) throws IOException {

        ComandaResumo resumo = resumoRepo.findById(resumoId)
                .orElseThrow(() -> new IllegalArgumentException("Resumo não encontrado"));

        if ("ENVIADO".equalsIgnoreCase(resumo.getStatusSat()))
            throw new IllegalStateException("Cupom já emitido para esse resumo.");

        // 1. Gera XML da venda
        String xml = SatXmlBuilder.buildCFeVenda(resumo);

        // 2. Salva XML bruto para debug
        FilesHelper.writeFile("data/sat/xml", "DEBUG_" + System.currentTimeMillis() + ".xml",
                xml.getBytes(StandardCharsets.ISO_8859_1));

        // 3. Monta comando SAT
        int sessao = 100_000 + RND.nextInt(900_000);
        String xml64 = Base64.getEncoder()
                .encodeToString(xml.replace("\n", "").getBytes(StandardCharsets.ISO_8859_1));
        String cmdEnviar = "SAT.EnviarDadosVenda(" + sessao + "," + xml64 + ")";

        log.debug("Comando SAT enviado → {}", cmdEnviar);
        String resp = enviarComando(cmdEnviar);
        log.debug("Resposta ACBr → {}", resp);

        if (resp == null || !resp.startsWith("OK:"))
            throw new IOException("ACBr não aceitou comando: " + resp);

        String[] campos = resp.split("\\|");
        if (campos.length < 6 || !"10000".equals(campos[1]))
            throw new IOException("Erro interno ACBr/SAT: " + resp);

        String chaveCFe = campos[5];
        String nCFe = chaveCFe.substring(30);

        // 4. Gera PDF
        String pdfPath = gerarPdfViaACBr(chaveCFe, nCFe);

        // 5. Salva dados
        resumo.setNumeroCupom(nCFe);
        resumo.setChaveSat(chaveCFe);
        resumo.setStatusSat("ENVIADO");
        resumo.setXmlPath(salvarXml(xml, nCFe));
        resumo.setPdfPath(pdfPath);

        return resumoRepo.save(resumo);
    }

    private String enviarComando(String cmd) throws IOException {
        String host = cfg.get("sat.ip", "127.0.0.1");
        int port = cfg.getInt("sat.port", 3434);
        int wait = 10_000;

        try (Socket socket = new Socket()) {
            socket.connect(new InetSocketAddress(host, port), wait);
            socket.setSoTimeout(wait);

            try (BufferedWriter out = new BufferedWriter(
                    new OutputStreamWriter(socket.getOutputStream(), StandardCharsets.ISO_8859_1));
                BufferedReader in = new BufferedReader(
                    new InputStreamReader(socket.getInputStream(), StandardCharsets.ISO_8859_1))) {

               /* envia */
            	out.write(cmd);
            	out.write("\r\n.\r\n");  // ← CORRETO
            	log.debug("Comando final enviado:\n{}\n", cmd + "\r\n.\r\n");
            	out.flush();
            	


               String banner   = in.readLine();     // descartamos
               String resposta = in.readLine();     // efetiva
               if (resposta == null || resposta.trim().isEmpty()) {
                   resposta = banner;
               }
               log.debug("Banner ACBr  → {}", banner);
               log.debug("Resposta ACBr→ {}", resposta);
               return resposta;
           }

            }
        }
    

    private String gerarPdfViaACBr(String chave, String numero) throws IOException {
        String resp = enviarComando("SAT.ImprimirExtratoVenda(\"" + chave + "\")");

        if (resp != null && resp.startsWith("OK:"))
            return resp.substring(3);

        String html = enviarComando("SAT.GerarImpressaoXML(\"" + chave + "\")");
        if (html == null || !html.startsWith("OK:"))
            throw new IOException("Falhou gerar impressão: " + html);

        byte[] pdf = PdfUtils.htmlToPdf(html.substring(3));
        return FilesHelper.writeFile("data/sat/pdf",
                "CUPOM_" + numero + "_" + LocalDate.now().format(DF) + ".pdf",
                pdf).toString();
    }

    private String salvarXml(String xml, String numero) throws IOException {
        return FilesHelper.writeFile("data/sat/xml",
                "CFE_" + numero + "_" + LocalDate.now().format(DF) + ".xml",
                xml.getBytes(StandardCharsets.ISO_8859_1)).toString();
    }
}

package com.exemplo.controlemesas.services;

import com.exemplo.controlemesas.model.ComandaResumo;
import com.exemplo.controlemesas.model.ItemComandaResumo;
import com.exemplo.controlemesas.dto.ConfiguracaoEnderecoDTO;
import com.exemplo.controlemesas.repository.ComandaResumoRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.concurrent.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class NfeServiceIntegrationTest {

    private ServerSocket serverSocket;
    private ExecutorService executor;

    @BeforeEach
    void setup() throws IOException {
        serverSocket = new ServerSocket(0); // ephemeral port
        executor = Executors.newSingleThreadExecutor();
    }

    @AfterEach
    void tearDown() throws IOException {
        if (serverSocket != null && !serverSocket.isClosed()) serverSocket.close();
        if (executor != null) executor.shutdownNow();
    }

    private void startStubServer(String behavior) {
        // behavior: "imprimir-ok" or "imprimir-fail-then-gerar"
        executor.submit(() -> {
            try {
                // Accept up to three separate connections (one per ACBr command)
                for (int i = 0; i < 3; i++) {
                    try (Socket client = serverSocket.accept()) {
                        BufferedReader in = new BufferedReader(new InputStreamReader(client.getInputStream()));
                        BufferedWriter out = new BufferedWriter(new OutputStreamWriter(client.getOutputStream()));

                        // read until a line with a single dot '.' (terminator)
                        String line;
                        StringBuilder cmd = new StringBuilder();
                        while ((line = in.readLine()) != null) {
                            if (".".equals(line.trim())) break;
                            cmd.append(line).append("\n");
                        }
                        String c = cmd.toString();

                        // respond according to the command received
                        if (c.contains("CriarEnviarNFe")) {
                            out.write("BANNER\r\n");
                            out.write("OK:|100|PROTOCOLO\r\n");
                            out.flush();
                        } else if (c.contains("ImprimirDANFE")) {
                            out.write("BANNER2\r\n");
                            if ("imprimir-ok".equals(behavior)) {
                                out.write("OK:/tmp/fake.pdf\r\n");
                            } else {
                                out.write("ERRO: Impressora indisponivel\r\n");
                            }
                            out.flush();
                        } else if (c.contains("GerarDanfe")) {
                            out.write("BANNER3\r\n");
                            out.write("OK:<html><body>DANFE</body></html>\r\n");
                            out.flush();
                        }

                        // brief pause before accepting next connection
                        Thread.sleep(20);
                    }
                }
            } catch (Exception e) {
                // ignore in test stub
            }
        });
    }

    @Test
    void emitir_endToEnd_imprimirOk() throws Exception {
        int port = serverSocket.getLocalPort();
        startStubServer("imprimir-ok");

        ComandaResumo resumo = new ComandaResumo();
        resumo.setId(123L);
        resumo.setDataFechamento(LocalDateTime.now());
        ItemComandaResumo item = new ItemComandaResumo();
        item.setItemNo(1);
        item.setDescricao("Produto Teste");
        item.setUnMedida("UN");
        item.setQuantidade(java.math.BigDecimal.ONE);
        item.setPrecoUnitario(new java.math.BigDecimal("10.00"));
        item.setSubtotal(new java.math.BigDecimal("10.00"));
        resumo.setItens(Collections.singletonList(item));
        resumo.setTotal(new java.math.BigDecimal("10.00"));

        ComandaResumoRepository repo = mock(ComandaResumoRepository.class);
        when(repo.findById(123L)).thenReturn(java.util.Optional.of(resumo));
        when(repo.save(any(ComandaResumo.class))).thenAnswer(inv -> inv.getArgument(0));

        ConfiguracaoService cfg = mock(ConfiguracaoService.class);
        // return the provided default value for any cfg.get(key, default) calls to avoid NPEs
        when(cfg.get(anyString(), anyString())).thenAnswer(inv -> inv.getArgument(1));
        // explicit company config values used by NfeXmlBuilder
        when(cfg.get("empresa.cnpj", "00000000000000")).thenReturn("11111111111111");
        when(cfg.get("empresa.razaoSocial", "Empresa Exemplo")).thenReturn("Empresa Teste LTDA");
        when(cfg.get("empresa.nomeFantasia", "Fantasia")).thenReturn("Fantasia Teste");
        when(cfg.get("empresa.ie", "ISENTO")).thenReturn("ISENTO");
        when(cfg.get("sat.ip", "127.0.0.1")).thenReturn("127.0.0.1");
        when(cfg.getInt("sat.port", 3434)).thenReturn(port);
        when(cfg.getEnderecoEmpresa()).thenReturn(new ConfiguracaoEnderecoDTO("Rua Teste", "1", "Bairro", "Sao Paulo", "SP", "01001000"));

        NfeService svc = new NfeService(repo, cfg);
        ComandaResumo out = svc.emitir(123L);

        assertNotNull(out);
        assertEquals("ENVIADO", out.getStatusSat());
        assertNotNull(out.getPdfPath());
    }

    @Test
    void emitir_endToEnd_imprimirFails_thenGerarDanfe() throws Exception {
        int port = serverSocket.getLocalPort();
        startStubServer("imprimir-fail-then-gerar");

        ComandaResumo resumo = new ComandaResumo();
        resumo.setId(555L);
        resumo.setDataFechamento(LocalDateTime.now());
        ItemComandaResumo item2 = new ItemComandaResumo();
        item2.setItemNo(1);
        item2.setDescricao("Produto Teste");
        item2.setUnMedida("UN");
        item2.setQuantidade(java.math.BigDecimal.ONE);
        item2.setPrecoUnitario(new java.math.BigDecimal("12.50"));
        item2.setSubtotal(new java.math.BigDecimal("12.50"));
        resumo.setItens(Collections.singletonList(item2));
        resumo.setTotal(new java.math.BigDecimal("12.50"));

        ComandaResumoRepository repo = mock(ComandaResumoRepository.class);
        when(repo.findById(555L)).thenReturn(java.util.Optional.of(resumo));
        when(repo.save(any(ComandaResumo.class))).thenAnswer(inv -> inv.getArgument(0));

        ConfiguracaoService cfg = mock(ConfiguracaoService.class);
        // return the provided default value for any cfg.get(key, default) calls to avoid NPEs
        when(cfg.get(anyString(), anyString())).thenAnswer(inv -> inv.getArgument(1));
        // explicit company config values used by NfeXmlBuilder
        when(cfg.get("empresa.cnpj", "00000000000000")).thenReturn("11111111111111");
        when(cfg.get("empresa.razaoSocial", "Empresa Exemplo")).thenReturn("Empresa Teste LTDA");
        when(cfg.get("empresa.nomeFantasia", "Fantasia")).thenReturn("Fantasia Teste");
        when(cfg.get("empresa.ie", "ISENTO")).thenReturn("ISENTO");
        when(cfg.get("sat.ip", "127.0.0.1")).thenReturn("127.0.0.1");
        when(cfg.getInt("sat.port", 3434)).thenReturn(port);
        when(cfg.getEnderecoEmpresa()).thenReturn(new ConfiguracaoEnderecoDTO("Rua Teste", "1", "Bairro", "Sao Paulo", "SP", "01001000"));

        NfeService svc = new NfeService(repo, cfg);
        ComandaResumo out = svc.emitir(555L);

        assertNotNull(out);
        assertEquals("ENVIADO", out.getStatusSat());
        assertNotNull(out.getPdfPath());
    }
}
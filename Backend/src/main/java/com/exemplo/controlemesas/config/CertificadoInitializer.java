package com.exemplo.controlemesas.config;

import com.exemplo.controlemesas.nfe.CertificadoDigital;
import com.exemplo.controlemesas.services.ConfiguracaoService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.io.File;

/**
 * Inicializa o certificado digital na inicialização da aplicação.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class CertificadoInitializer implements CommandLineRunner {

    private final CertificadoDigital certificadoDigital;
    private final ConfiguracaoService cfg;

    @Override
    public void run(String... args) {
        try {
            String caminho = cfg.get("nfe.certificado.caminho", "");
            String senha = cfg.get("nfe.certificado.senha", "");

            if (caminho.isEmpty() || senha.isEmpty()) {
                log.warn("⚠️  Certificado digital NF-e não configurado. Configure as propriedades:");
                log.warn("   - nfe.certificado.caminho");
                log.warn("   - nfe.certificado.senha");
                return;
            }

            File arquivo = new File(caminho);
            if (!arquivo.exists()) {
                log.warn("⚠️  Arquivo de certificado não encontrado: {}", caminho);
                log.warn("   Emissão de NF-e não estará disponível até que o certificado seja configurado.");
                return;
            }

            certificadoDigital.carregar(caminho, senha);
            log.info("✅ Certificado digital carregado com sucesso!");
            
        } catch (Exception e) {
            log.error("❌ Erro ao carregar certificado digital: {}", e.getMessage());
            log.warn("   Emissão de NF-e não estará disponível até que o certificado seja configurado corretamente.");
        }
    }
}

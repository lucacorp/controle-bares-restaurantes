package com.exemplo.controlemesas.dce;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * Controller REST para emissão de DC-e (Declaração de Conteúdo Eletrônica).
 * 
 * Endpoints para integração com sistemas dos Correios ou aplicações que precisam
 * emitir DC-e para encomendas.
 */
@Slf4j
@RestController
@RequestMapping("/api/dce")
@RequiredArgsConstructor
public class DceController {

    private final DceService dceService;

    /**
     * Emite uma DC-e.
     * 
     * @param dados Dados da DC-e
     * @return Chave de acesso da DC-e autorizada
     */
    @PostMapping("/emitir")
    public ResponseEntity<Map<String, Object>> emitirDCe(@RequestBody DadosDCe dados) {
        log.info("Requisição para emitir DC-e número {}", dados.getNumero());
        
        Map<String, Object> response = new HashMap<>();
        
        try {
            String chaveAcesso = dceService.emitirDCe(dados);
            
            response.put("sucesso", true);
            response.put("chaveAcesso", chaveAcesso);
            response.put("mensagem", "DC-e autorizada com sucesso");
            
            log.info("DC-e {} emitida com sucesso. Chave: {}", dados.getNumero(), chaveAcesso);
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            log.error("Erro ao emitir DC-e {}: {}", dados.getNumero(), e.getMessage(), e);
            
            response.put("sucesso", false);
            response.put("erro", e.getMessage());
            
            return ResponseEntity.status(500).body(response);
        }
    }

    /**
     * Valida as configurações de DC-e.
     * 
     * @return Status das configurações
     */
    @GetMapping("/validar-config")
    public ResponseEntity<Map<String, Object>> validarConfiguracao() {
        log.info("Validando configurações DC-e");
        
        Map<String, Object> response = new HashMap<>();
        
        try {
            dceService.validarConfiguracoes();
            
            response.put("sucesso", true);
            response.put("mensagem", "Configurações DC-e válidas");
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            log.error("Erro ao validar configurações DC-e: {}", e.getMessage());
            
            response.put("sucesso", false);
            response.put("erro", e.getMessage());
            
            return ResponseEntity.status(500).body(response);
        }
    }

    /**
     * Verifica se uma UF suporta DC-e.
     * 
     * @param uf Sigla da UF
     * @return true se suporta, false caso contrário
     */
    @GetMapping("/verificar-uf/{uf}")
    public ResponseEntity<Map<String, Object>> verificarUF(@PathVariable String uf) {
        Map<String, Object> response = new HashMap<>();
        
        boolean suporta = DceEndpoints.ufSuportaDCe(uf);
        
        response.put("uf", uf.toUpperCase());
        response.put("suportaDCe", suporta);
        
        if (!suporta) {
            response.put("mensagem", "UF não suporta DC-e. Estados disponíveis: AC, AL, AP, DF, ES, PB, PI, RJ, RN, RO, RR, SC, SE, TO");
        }
        
        return ResponseEntity.ok(response);
    }

    /**
     * Health check do serviço DC-e.
     */
    @GetMapping("/health")
    public ResponseEntity<Map<String, String>> health() {
        Map<String, String> response = new HashMap<>();
        response.put("status", "UP");
        response.put("servico", "DC-e");
        response.put("versao", "1.0");
        return ResponseEntity.ok(response);
    }
}

package com.exemplo.controlemesas.controller;

import com.exemplo.controlemesas.nfe.NfeXmlValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

/**
 * Controller para validação de XMLs NFCe sem envio para SEFAZ.
 */
@Slf4j
@RestController
@RequestMapping("/api/nfe/validar")
@RequiredArgsConstructor
public class NfeValidatorController {

    private final NfeXmlValidator xmlValidator;

    /**
     * Valida um arquivo XML pelo caminho.
     * GET /api/nfe/validar?arquivo=data/nfe/xml/NFe_35251261134978000130650010000001431732039196.xml
     */
    @GetMapping
    public ResponseEntity<Map<String, Object>> validarArquivo(@RequestParam String arquivo) {
        log.info("🔍 Requisição de validação local do arquivo: {}", arquivo);
        
        Map<String, Object> response = new HashMap<>();
        
        try {
            File file = new File(arquivo);
            if (!file.exists()) {
                response.put("sucesso", false);
                response.put("erro", "Arquivo não encontrado: " + arquivo);
                return ResponseEntity.badRequest().body(response);
            }
            
            boolean valido = xmlValidator.validarArquivo(arquivo);
            
            response.put("sucesso", true);
            response.put("valido", valido);
            response.put("arquivo", arquivo);
            response.put("mensagem", valido ? 
                "✅ XML válido contra schema XSD!" : 
                "❌ XML inválido - verifique os logs para detalhes dos erros");
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            log.error("Erro ao validar arquivo: {}", e.getMessage(), e);
            response.put("sucesso", false);
            response.put("erro", e.getMessage());
            return ResponseEntity.internalServerError().body(response);
        }
    }

    /**
     * Valida XML enviado no corpo da requisição.
     * POST /api/nfe/validar
     * Body: conteúdo XML
     */
    @PostMapping(consumes = "application/xml", produces = "application/json")
    public ResponseEntity<Map<String, Object>> validarXml(@RequestBody String xml) {
        log.info("🔍 Requisição de validação local de XML (via POST)");
        
        Map<String, Object> response = new HashMap<>();
        
        try {
            boolean valido = xmlValidator.validarXml(xml);
            
            response.put("sucesso", true);
            response.put("valido", valido);
            response.put("mensagem", valido ? 
                "✅ XML válido contra schema XSD!" : 
                "❌ XML inválido - verifique os logs para detalhes dos erros");
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            log.error("Erro ao validar XML: {}", e.getMessage(), e);
            response.put("sucesso", false);
            response.put("erro", e.getMessage());
            return ResponseEntity.internalServerError().body(response);
        }
    }

    /**
     * Valida o último XML gerado.
     * GET /api/nfe/validar/ultimo
     */
    @GetMapping("/ultimo")
    public ResponseEntity<Map<String, Object>> validarUltimoXml() {
        log.info("🔍 Requisição de validação do último XML gerado");
        
        Map<String, Object> response = new HashMap<>();
        
        try {
            File dirXml = new File("data/nfe/xml");
            if (!dirXml.exists() || !dirXml.isDirectory()) {
                response.put("sucesso", false);
                response.put("erro", "Diretório de XMLs não encontrado");
                return ResponseEntity.badRequest().body(response);
            }
            
            File[] xmlFiles = dirXml.listFiles((dir, name) -> name.endsWith(".xml"));
            if (xmlFiles == null || xmlFiles.length == 0) {
                response.put("sucesso", false);
                response.put("erro", "Nenhum XML encontrado no diretório");
                return ResponseEntity.badRequest().body(response);
            }
            
            // Pega o arquivo mais recente
            File ultimoXml = xmlFiles[0];
            for (File f : xmlFiles) {
                if (f.lastModified() > ultimoXml.lastModified()) {
                    ultimoXml = f;
                }
            }
            
            log.info("Validando último XML: {}", ultimoXml.getName());
            
            boolean valido = xmlValidator.validarArquivo(ultimoXml.getAbsolutePath());
            
            response.put("sucesso", true);
            response.put("valido", valido);
            response.put("arquivo", ultimoXml.getName());
            response.put("caminho", ultimoXml.getAbsolutePath());
            response.put("mensagem", valido ? 
                "✅ XML válido contra schema XSD!" : 
                "❌ XML inválido - verifique os logs para detalhes dos erros");
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            log.error("Erro ao validar último XML: {}", e.getMessage(), e);
            response.put("sucesso", false);
            response.put("erro", e.getMessage());
            return ResponseEntity.internalServerError().body(response);
        }
    }
}

package com.exemplo.controlemesas.controller;

import com.exemplo.controlemesas.dto.ConfiguracaoDTO;
import com.exemplo.controlemesas.services.ConfiguracaoService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/configuracoes")
@CrossOrigin(origins = "http://localhost:5173")
public class ConfiguracaoController {

    private final ConfiguracaoService service;

    public ConfiguracaoController(ConfiguracaoService service) {
        this.service = service;
    }

    @GetMapping
    public List<ConfiguracaoDTO> listar(@RequestParam(name = "prefixo", required = false) String prefixo) {
        return service.listar(prefixo);
    }

    @PostMapping
    public ConfiguracaoDTO salvar(@RequestBody ConfiguracaoDTO dto) {
        return service.salvar(dto);
    }

    // ---------------- NOVOS ENDPOINTS PARA O MODO FISCAL ----------------

    @GetMapping("/modo-fiscal")
    public Map<String, String> getModoFiscal() {
        return Map.of("valor", service.getModoFiscal());
    }

    @PostMapping("/modo-fiscal")
    public void setModoFiscal(@RequestBody Map<String, String> body) {
        String modo = body.getOrDefault("valor", "SAT").toUpperCase();
        if (!modo.equals("SAT") && !modo.equals("NFCE")) {
            throw new IllegalArgumentException("Modo fiscal inválido. Use SAT ou NFCE.");
        }
        service.setModoFiscal(modo);
    }
}

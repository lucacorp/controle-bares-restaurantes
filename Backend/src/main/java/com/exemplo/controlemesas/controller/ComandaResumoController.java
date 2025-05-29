package com.exemplo.controlemesas.controller;

import com.exemplo.controlemesas.model.Comanda;
import com.exemplo.controlemesas.model.ComandaResumo;
import com.exemplo.controlemesas.repository.ComandaRepository;
import com.exemplo.controlemesas.repository.ComandaResumoRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/comandas")
@CrossOrigin(origins = "http://localhost:5173")
public class ComandaResumoController {

    private final ComandaResumoRepository resumoRepository;
    private final ComandaRepository comandaRepository;

    public ComandaResumoController(ComandaResumoRepository resumoRepository, ComandaRepository comandaRepository) {
        this.resumoRepository = resumoRepository;
        this.comandaRepository = comandaRepository;
    }

    // ✅ Listar TODOS os resumos
    @GetMapping("/resumos")
    public List<ComandaResumo> listarTodosResumos() {
        return resumoRepository.findAll();
    }

    // ✅ Listar resumos por comanda
    @GetMapping("/{id}/resumo")
    public ResponseEntity<?> listarResumosPorComanda(@PathVariable Long id) {
        List<ComandaResumo> resumos = resumoRepository.findByComandaId(id);
        if (resumos.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(resumos);
    }

    // ✅ Salvar novo resumo
    @PostMapping("/{id}/resumo")
    public ResponseEntity<?> salvarResumo(
            @PathVariable Long id,
            @RequestBody Map<String, String> payload
    ) {
        Comanda comanda = comandaRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Comanda não encontrada."));

        ComandaResumo resumo = new ComandaResumo();
        resumo.setComanda(comanda);
        resumo.setDataFechamento(LocalDateTime.now());

        try {
            String totalStr = payload.getOrDefault("total", "0");
            BigDecimal total = new BigDecimal(totalStr);
            resumo.setTotal(total);
        } catch (NumberFormatException e) {
            resumo.setTotal(BigDecimal.ZERO);
        }

        resumo.setNomeCliente(payload.getOrDefault("nomeCliente", ""));
        resumo.setObservacoes(payload.getOrDefault("observacoes", ""));

        ComandaResumo salvo = resumoRepository.save(resumo);
        return ResponseEntity.ok(salvo);
    }
}

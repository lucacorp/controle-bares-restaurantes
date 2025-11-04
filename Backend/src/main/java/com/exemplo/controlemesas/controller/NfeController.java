package com.exemplo.controlemesas.controller;

import com.exemplo.controlemesas.model.ComandaResumo;
import com.exemplo.controlemesas.services.NfeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/comanda-resumo")
@RequiredArgsConstructor
public class NfeController {

    private final NfeService nfeService;

    /**
     * Endpoint para emissão da NFC-e para uma comanda fechada.
     *
     * @param id ID do ComandaResumo (resumo de comanda já fechada)
     * @return ComandaResumo atualizado com status e paths
     */
    @PostMapping("/{id}/emitir-nfe")
    public ResponseEntity<?> emitirNfe(@PathVariable Long id) {
        try {
            ComandaResumo atualizado = nfeService.emitir(id);
            return ResponseEntity.ok(atualizado);
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Erro ao emitir NFC-e: " + e.getMessage());
        }
    }
}

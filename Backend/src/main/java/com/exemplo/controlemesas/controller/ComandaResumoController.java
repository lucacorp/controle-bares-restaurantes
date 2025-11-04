package com.exemplo.controlemesas.controller;

import com.exemplo.controlemesas.dto.ComandaResumoDTO;
import com.exemplo.controlemesas.model.ComandaResumo;
import com.exemplo.controlemesas.services.ComandaResumoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/comandas/resumo")
@RequiredArgsConstructor
public class ComandaResumoController {

    private final ComandaResumoService comandaResumoService;

    @GetMapping
    public ResponseEntity<List<ComandaResumoDTO>> listarResumos() {
        // ✅ CORREÇÃO: Converte a lista de entidades para DTOs antes de retornar
        List<ComandaResumo> resumos = comandaResumoService.listar();
        List<ComandaResumoDTO> resumosDTO = resumos.stream()
                .map(ComandaResumoDTO::new)
                .collect(Collectors.toList());
        return ResponseEntity.ok(resumosDTO);
    }
}

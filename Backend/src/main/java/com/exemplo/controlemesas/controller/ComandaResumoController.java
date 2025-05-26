package com.exemplo.controlemesas.controller;

import com.exemplo.controlemesas.dto.ComandaResumoDTO;
import com.exemplo.controlemesas.model.ComandaResumo;
import com.exemplo.controlemesas.repository.ComandaResumoRepository;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/comandas/{comandaId}/resumo")
@CrossOrigin(origins = "http://localhost:5173")
public class ComandaResumoController {

    private final ComandaResumoRepository resumoRepository;

    public ComandaResumoController(ComandaResumoRepository resumoRepository) {
        this.resumoRepository = resumoRepository;
    }

    @GetMapping
    public List<ComandaResumoDTO> listarPorComanda(@PathVariable Long comandaId) {
        List<ComandaResumo> resumos = resumoRepository.findByComandaId(comandaId);
        return resumos.stream().map(this::toDTO).collect(Collectors.toList());
    }

    private ComandaResumoDTO toDTO(ComandaResumo resumo) {
        ComandaResumoDTO dto = new ComandaResumoDTO();
        dto.setId(resumo.getId());
        dto.setComandaId(resumo.getComanda().getId());
        dto.setTotal(resumo.getTotal());
        dto.setDataFechamento(resumo.getDataFechamento());
        dto.setNomeCliente(resumo.getNomeCliente());
        dto.setObservacoes(resumo.getObservacoes());
        return dto;
    }
}

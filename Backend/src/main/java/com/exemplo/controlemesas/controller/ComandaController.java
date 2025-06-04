package com.exemplo.controlemesas.controller;

import com.exemplo.controlemesas.dto.ComandaDTO;
import com.exemplo.controlemesas.dto.ErrorResponse;
import com.exemplo.controlemesas.model.Comanda;
import com.exemplo.controlemesas.repository.ComandaRepository;
import com.exemplo.controlemesas.services.ComandaService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

import java.net.URI;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/comandas")
@CrossOrigin(origins = "http://localhost:5173")
public class ComandaController {

    @Autowired
    private ComandaRepository comandaRepository;

    @Autowired
    private ComandaService comandaService;

    private ComandaDTO toDTO(Comanda comanda) {
        ComandaDTO dto = new ComandaDTO();
        dto.setId(comanda.getId());
        dto.setMesaId(comanda.getMesa() != null ? comanda.getMesa().getId() : null);
        dto.setStatus(comanda.getStatus().name());
        dto.setDataAbertura(comanda.getDataAbertura() != null ? comanda.getDataAbertura().toString() : null);
        dto.setDataFechamento(comanda.getDataFechamento() != null ? comanda.getDataFechamento().toString() : null);
        return dto;
    }

    @GetMapping
    public List<ComandaDTO> listarTodas() {
        return comandaRepository.findAll().stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> buscarPorId(@PathVariable Long id) {
        return comandaRepository.findById(id)
                .map(comanda -> ResponseEntity.ok(toDTO(comanda)))
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/mesa/{mesaId}")
    public ResponseEntity<List<ComandaDTO>> listarPorMesa(@PathVariable Long mesaId) {
        List<Comanda> comandas = comandaRepository.findByMesaIdAndAtivoTrue(mesaId);
        List<ComandaDTO> dtos = comandas.stream().map(this::toDTO).collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }

    @PostMapping
    public ResponseEntity<?> criar(@Valid @RequestBody ComandaDTO dto) {
        try {
            Comanda comanda = comandaService.criarComanda(dto.getMesaId());
            return ResponseEntity.created(URI.create("/api/comandas/" + comanda.getId())).body(toDTO(comanda));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(new ErrorResponse(e.getMessage()));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> excluir(@PathVariable Long id) {
        return comandaRepository.findById(id).map(comanda -> {
            comanda.setAtivo(false);
            comandaRepository.save(comanda);
            return ResponseEntity.noContent().build();
        }).orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/{id}/fechar")
    public ResponseEntity<?> fecharComanda(
            @PathVariable Long id,
            @RequestParam(required = false) String nomeCliente,
            @RequestParam(required = false) String observacoes) {
        try {
            comandaService.fecharComanda(id, nomeCliente, observacoes);
            return ResponseEntity.ok(Map.of("message", "Comanda finalizada com sucesso!"));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("message", "Erro ao finalizar comanda."));
        }
    }
}

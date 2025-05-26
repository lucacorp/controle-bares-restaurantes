package com.exemplo.controlemesas.controller;

import com.exemplo.controlemesas.dto.ComandaDTO;
import com.exemplo.controlemesas.dto.ErrorResponse;
import com.exemplo.controlemesas.model.Comanda;
import com.exemplo.controlemesas.model.Mesa;
import com.exemplo.controlemesas.repository.ComandaRepository;
import com.exemplo.controlemesas.repository.MesaRepository;
import com.exemplo.controlemesas.services.ComandaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.net.URI;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/comandas")
@CrossOrigin(origins = "http://localhost:5173")
public class ComandaController {

    @Autowired
    private ComandaRepository comandaRepository;

    @Autowired
    private MesaRepository mesaRepository;

    @Autowired
    private ComandaService comandaService;  // ✅ FALTAVA ISSO!

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
        List<Comanda> comandas = comandaRepository.findByMesaId(mesaId);
        List<ComandaDTO> dtos = comandas.stream().map(this::toDTO).collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }

    @PostMapping
    public ResponseEntity<?> criar(@Valid @RequestBody ComandaDTO dto) {
        Optional<Mesa> mesaOpt = mesaRepository.findById(dto.getMesaId());
        if (mesaOpt.isEmpty()) {
            return ResponseEntity.badRequest().body(new ErrorResponse("Mesa não encontrada."));
        }

        Mesa mesa = mesaOpt.get();

        Comanda comanda = new Comanda();
        comanda.setMesa(mesa);
        comanda.setDataAbertura(LocalDateTime.now());
        comanda.setStatus(Comanda.StatusComanda.ABERTA);

        Comanda salva = comandaRepository.save(comanda);

        return ResponseEntity.created(URI.create("/api/comandas/" + salva.getId())).body(toDTO(salva));
    }

    @PostMapping("/{id}/fechar")
    public ResponseEntity<?> fecharComanda(@PathVariable Long id) {
        try {
            comandaService.fecharComanda(id);  // ✅ aqui está a chamada correta
            return ResponseEntity.ok(Map.of("message", "Comanda finalizada com sucesso!"));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("message", "Erro ao finalizar comanda."));
        }
    }
}

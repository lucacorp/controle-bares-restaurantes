package com.exemplo.controlemesas.controller;

import com.exemplo.controlemesas.dto.ComandaDTO;
import com.exemplo.controlemesas.dto.ErrorResponse;
import com.exemplo.controlemesas.model.Comanda;
import com.exemplo.controlemesas.model.Mesa;
import com.exemplo.controlemesas.repository.ComandaRepository;
import com.exemplo.controlemesas.repository.MesaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.time.format.DateTimeFormatter;
import java.util.List;
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

    private DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    private ComandaDTO toDTO(Comanda comanda) {
        ComandaDTO dto = new ComandaDTO();
        dto.setId(comanda.getId());
        dto.setMesaId(comanda.getMesa() != null ? comanda.getMesa().getId() : null);
        dto.setStatus(comanda.getStatus().name());
        if (comanda.getDataAbertura() != null)
            dto.setDataAbertura(formatter.format(comanda.getDataAbertura()));
        if (comanda.getDataFechamento() != null)
            dto.setDataFechamento(formatter.format(comanda.getDataFechamento()));
        return dto;
    }

    @GetMapping
    public List<ComandaDTO> listarTodas() {
        return comandaRepository.findAll().stream().map(this::toDTO).collect(Collectors.toList());
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> buscarPorId(@PathVariable Long id) {
        return comandaRepository.findById(id)
                .map(c -> ResponseEntity.ok(toDTO(c)))
                .orElse(ResponseEntity.status(404).body(new ErrorResponse("Comanda não encontrada")));
    }

    @PostMapping
    public ResponseEntity<?> criar(@RequestBody ComandaDTO dto) {
        if (dto.getMesaId() != null) {
            Optional<Mesa> mesaOpt = mesaRepository.findById(dto.getMesaId());
            if (mesaOpt.isEmpty()) {
                return ResponseEntity.badRequest().body(new ErrorResponse("Mesa não encontrada para o ID: " + dto.getMesaId()));
            }

            Mesa mesa = mesaOpt.get();
            if (mesa.isOcupada()) {
                return ResponseEntity.badRequest().body(new ErrorResponse("Mesa já está ocupada"));
            }

            mesa.setOcupada(true);
            mesaRepository.save(mesa);

            Comanda comanda = new Comanda();
            comanda.setMesa(mesa);
            comanda.setDataAbertura(java.time.LocalDateTime.now());
            comanda.setStatus(Comanda.StatusComanda.ABERTA);

            Comanda salva = comandaRepository.save(comanda);
            return ResponseEntity.created(URI.create("/api/comandas/" + salva.getId()))
                    .body(toDTO(salva));
        }

        return ResponseEntity.badRequest().body(new ErrorResponse("É necessário informar o ID da mesa"));
    }

    @PutMapping("/{id}/fechar")
    public ResponseEntity<?> fechar(@PathVariable Long id) {
        Optional<Comanda> comandaOpt = comandaRepository.findById(id);
        if (comandaOpt.isEmpty()) {
            return ResponseEntity.status(404).body(new ErrorResponse("Comanda não encontrada"));
        }

        Comanda comanda = comandaOpt.get();
        comanda.setStatus(Comanda.StatusComanda.FECHADA);
        comanda.setDataFechamento(java.time.LocalDateTime.now());

        Mesa mesa = comanda.getMesa();
        if (mesa != null) {
            mesa.setOcupada(false);
            mesaRepository.save(mesa);
        }

        return ResponseEntity.ok(toDTO(comandaRepository.save(comanda)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> excluir(@PathVariable Long id) {
        if (!comandaRepository.existsById(id)) {
            return ResponseEntity.status(404).body(new ErrorResponse("Comanda não encontrada"));
        }

        comandaRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}

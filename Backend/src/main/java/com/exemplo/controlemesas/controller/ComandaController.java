package com.exemplo.controlemesas.controller;

import com.exemplo.controlemesas.dto.ComandaDTO;
import com.exemplo.controlemesas.dto.ErrorResponse;
import com.exemplo.controlemesas.model.Comanda;
import com.exemplo.controlemesas.model.ComandaResumo;
import com.exemplo.controlemesas.model.ItemComanda;
import com.exemplo.controlemesas.model.Mesa;
import com.exemplo.controlemesas.repository.ComandaRepository;
import com.exemplo.controlemesas.repository.ComandaResumoRepository;
import com.exemplo.controlemesas.repository.MesaRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.math.BigDecimal;
import java.net.URI;
import java.time.LocalDateTime;
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

    @Autowired
    private ComandaResumoRepository resumoRepository;

    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

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
        return comandaRepository.findAll()
                .stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> buscarPorId(@PathVariable Long id) {
        Optional<Comanda> comandaOpt = comandaRepository.findById(id);
        if (comandaOpt.isPresent()) {
            return ResponseEntity.ok().body(toDTO(comandaOpt.get()));
        } else {
            return ResponseEntity.status(404).body(new ErrorResponse("Comanda não encontrada"));
        }
    }

    @PostMapping
    public ResponseEntity<?> criar(@Valid @RequestBody ComandaDTO dto) {
        if (dto.getMesaId() == null) {
            return ResponseEntity.badRequest().body(new ErrorResponse("ID da mesa é obrigatório"));
        }

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
        comanda.setDataAbertura(LocalDateTime.now());
        comanda.setStatus(Comanda.StatusComanda.ABERTA);

        Comanda salva = comandaRepository.save(comanda);
        return ResponseEntity.created(URI.create("/api/comandas/" + salva.getId())).body(toDTO(salva));
    }

    @PutMapping("/{id}/fechar")
    public ResponseEntity<?> fechar(@PathVariable Long id) {
        Optional<Comanda> comandaOpt = comandaRepository.findById(id);
        if (comandaOpt.isEmpty()) {
            return ResponseEntity.status(404).body(new ErrorResponse("Comanda não encontrada"));
        }

        Comanda comanda = comandaOpt.get();
        comanda.setStatus(Comanda.StatusComanda.FECHADA);
        comanda.setDataFechamento(LocalDateTime.now());

        Mesa mesa = comanda.getMesa();
        if (mesa != null) {
            mesa.setOcupada(false);
            mesaRepository.save(mesa);
        }

        comandaRepository.save(comanda);

        // Calcula o total da comanda
        BigDecimal total = BigDecimal.ZERO;
        List<ItemComanda> itens = comanda.getItens();
        if (itens != null) {
            total = itens.stream()
                    .map(i -> BigDecimal.valueOf(i.getPrecoUnitario()).multiply(BigDecimal.valueOf(i.getQuantidade())))
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
        }

        // Salva resumo
        ComandaResumo resumo = new ComandaResumo();
        resumo.setComanda(comanda);
        resumo.setDataFechamento(LocalDateTime.now());
        resumo.setTotal(total);
        resumo.setNomeCliente("Consumidor");
        resumo.setObservacoes("Fechamento automático via frontend");

        resumoRepository.save(resumo);

        return ResponseEntity.ok().body(toDTO(comanda));
    }

    @GetMapping("/{id}/resumo")
    public ResponseEntity<?> buscarResumo(@PathVariable Long id) {
        if (!comandaRepository.existsById(id)) {
            return ResponseEntity.status(404).body(new ErrorResponse("Comanda não encontrada"));
        }

        List<ComandaResumo> resumos = resumoRepository.findByComandaId(id);
        return ResponseEntity.ok(resumos);
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

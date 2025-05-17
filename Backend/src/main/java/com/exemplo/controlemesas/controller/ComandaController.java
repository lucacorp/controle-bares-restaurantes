package com.exemplo.controlemesas.controller;

import com.exemplo.controlemesas.model.Comanda;
import com.exemplo.controlemesas.repository.ComandaRepository;
import com.exemplo.controlemesas.repository.MesaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/comandas")
@CrossOrigin(origins = "http://localhost:5173")
public class ComandaController {

    @Autowired
    private ComandaRepository comandaRepository;

    @Autowired
    private MesaRepository mesaRepository;

    @GetMapping
    public List<Comanda> listarTodas() {
        return comandaRepository.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Comanda> buscarPorId(@PathVariable Long id) {
        return comandaRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Comanda> criar(@RequestBody Comanda comanda) {
        if (comanda.getMesa() != null && comanda.getMesa().getId() != null) {
            var mesa = mesaRepository.findById(comanda.getMesa().getId());
            if (mesa.isEmpty()) {
                return ResponseEntity.badRequest().build();
            }
        }

        comanda.setDataAbertura(LocalDateTime.now());
        comanda.setStatus(Comanda.StatusComanda.ABERTA);
        Comanda salva = comandaRepository.save(comanda);
        return ResponseEntity.status(201).body(salva);
    }

    @PutMapping("/{id}/fechar")
    public ResponseEntity<Comanda> fechar(@PathVariable Long id) {
        Optional<Comanda> comandaOpt = comandaRepository.findById(id);
        if (comandaOpt.isEmpty()) return ResponseEntity.notFound().build();

        Comanda comanda = comandaOpt.get();
        comanda.setStatus(Comanda.StatusComanda.FECHADA);
        comanda.setDataFechamento(LocalDateTime.now());
        return ResponseEntity.ok(comandaRepository.save(comanda));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluir(@PathVariable Long id) {
        if (!comandaRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        comandaRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}

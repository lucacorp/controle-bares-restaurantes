package com.exemplo.controlemesas.controller;

import com.exemplo.controlemesas.model.Mesa;
import com.exemplo.controlemesas.repository.ComandaRepository;
import com.exemplo.controlemesas.repository.MesaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/mesas")
@CrossOrigin(origins = "http://localhost:5173")
public class MesaController {

    @Autowired
    private MesaRepository mesaRepository;

    @Autowired
    private ComandaRepository comandaRepository;

    @GetMapping
    public List<Mesa> listarTodas() {
        return mesaRepository.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Mesa> buscarPorId(@PathVariable Long id) {
        Optional<Mesa> mesaOpt = mesaRepository.findById(id);
        return mesaOpt.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Mesa> criar(@RequestBody Mesa mesa) {
        Mesa salva = mesaRepository.save(mesa);
        return ResponseEntity.ok(salva);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Mesa> atualizar(@PathVariable Long id, @RequestBody Mesa mesaAtualizada) {
        return mesaRepository.findById(id).map(mesa -> {
            mesa.setDescricao(mesaAtualizada.getDescricao());
            mesa.setOcupada(mesaAtualizada.isOcupada());
            mesaRepository.save(mesa);
            return ResponseEntity.ok(mesa);
        }).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> excluir(@PathVariable Long id) {
        if (!mesaRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }

        if (comandaRepository.existsByMesaId(id)) {
            return ResponseEntity.status(409).body("Não é possível excluir. Existem comandas associadas a esta mesa.");
        }

        mesaRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}

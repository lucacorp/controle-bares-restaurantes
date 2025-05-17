// MesaController adaptado com verificação de comandas
package com.exemplo.controlemesas.controller;

import com.exemplo.controlemesas.model.Mesa;
import com.exemplo.controlemesas.repository.MesaRepository;
import com.exemplo.controlemesas.repository.ComandaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/mesas")
@CrossOrigin(origins = "http://localhost:5173")
public class MesaController {

    @Autowired
    private MesaRepository mesaRepository;

    @Autowired
    private ComandaRepository comandaRepository;

    @GetMapping
    public List<Mesa> listarMesas() {
        return mesaRepository.findAll();
    }

    @PostMapping
    public ResponseEntity<Mesa> criarMesa(@RequestBody Mesa mesa) {
        Mesa novaMesa = mesaRepository.save(mesa);
        return ResponseEntity.status(201).body(novaMesa);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Mesa> atualizarMesa(@PathVariable Long id, @RequestBody Mesa mesaAtualizada) {
        return mesaRepository.findById(id)
                .map(m -> {
                    m.setDescricao(mesaAtualizada.getDescricao());
                    m.setOcupada(mesaAtualizada.isOcupada());
                    Mesa mesaSalva = mesaRepository.save(m);
                    return ResponseEntity.ok(mesaSalva);
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletarMesa(@PathVariable Long id) {
        if (!mesaRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }

        if (comandaRepository.existsByMesaId(id)) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }

        mesaRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}

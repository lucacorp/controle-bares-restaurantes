package com.exemplo.controlemesas.controller;

import com.exemplo.controlemesas.model.Mesa;
import com.exemplo.controlemesas.repository.MesaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/mesas")
public class MesaController {

    @Autowired
    private MesaRepository mesaRepository;

    @GetMapping
    public List<Mesa> listarMesas() {
        return mesaRepository.findAll();
    }

    @PostMapping
    public ResponseEntity<Mesa> criarMesa(@RequestBody Mesa mesa) {
        Mesa novaMesa = mesaRepository.save(mesa);
        return ResponseEntity.status(201).body(novaMesa); // Retorna 201 Created
    }

    @PutMapping("/{id}")
    public ResponseEntity<Mesa> atualizarMesa(@PathVariable Long id, @RequestBody Mesa mesaAtualizada) {
        return mesaRepository.findById(id)
                .map(m -> {
                    m.setDescricao(mesaAtualizada.getDescricao());
                    m.setOcupada(mesaAtualizada.isOcupada());
                    Mesa mesaSalva = mesaRepository.save(m);
                    return ResponseEntity.ok(mesaSalva); // Retorna 200 OK
                })
                .orElse(ResponseEntity.notFound().build()); // Retorna 404 se não encontrar
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletarMesa(@PathVariable Long id) {
        if (!mesaRepository.existsById(id)) {
            return ResponseEntity.notFound().build(); // Retorna 404 se não encontrar
        }
        mesaRepository.deleteById(id);
        return ResponseEntity.noContent().build(); // Retorna 204 No Content
    }
}

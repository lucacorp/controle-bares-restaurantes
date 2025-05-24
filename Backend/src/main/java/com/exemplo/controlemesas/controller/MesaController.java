package com.exemplo.controlemesas.controller;

import com.exemplo.controlemesas.model.Mesa;
import com.exemplo.controlemesas.repository.MesaRepository;
import com.exemplo.controlemesas.repository.ComandaRepository;
import com.exemplo.controlemesas.dto.ErrorResponse;

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

    @GetMapping("/{id}")
    public ResponseEntity<Object> buscarPorId(@PathVariable Long id) {
        return mesaRepository.findById(id)
                .<ResponseEntity<Object>>map(m -> ResponseEntity.ok().body(m))
                .orElseGet(() -> ResponseEntity.status(404).body(new ErrorResponse("Mesa não encontrada")));
    }

    @PostMapping
    public ResponseEntity<Mesa> criarMesa(@RequestBody Mesa mesa) {
        Mesa novaMesa = mesaRepository.save(mesa);
        return ResponseEntity.status(HttpStatus.CREATED).body(novaMesa);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Object> atualizarMesa(@PathVariable Long id, @RequestBody Mesa mesaAtualizada) {
        return mesaRepository.findById(id)
                .<ResponseEntity<Object>>map(m -> {
                    m.setDescricao(mesaAtualizada.getDescricao());
                    m.setOcupada(mesaAtualizada.isOcupada());
                    Mesa mesaSalva = mesaRepository.save(m);
                    return ResponseEntity.ok().body(mesaSalva);
                })
                .orElseGet(() -> ResponseEntity.status(404).body(new ErrorResponse("Mesa não encontrada")));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Object> deletarMesa(@PathVariable Long id) {
        if (!mesaRepository.existsById(id)) {
            return ResponseEntity.status(404).body(new ErrorResponse("Mesa não encontrada"));
        }

        if (comandaRepository.existsByMesaId(id)) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(new ErrorResponse("Não é possível excluir: há comandas associadas a esta mesa."));
        }

        mesaRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}

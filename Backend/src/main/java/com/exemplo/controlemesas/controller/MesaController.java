package com.exemplo.controlemesas.controller;

import com.exemplo.controlemesas.model.Mesa;
import com.exemplo.controlemesas.repository.MesaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "http://localhost:3000")

@RequestMapping("/mesas")
@RestController

public class MesaController {

    @Autowired
    private MesaRepository mesaRepository;

    @GetMapping
    public List<Mesa> listarMesas() {
        return mesaRepository.findAll();
    }

    @PostMapping
    public Mesa criarMesa(@RequestBody Mesa mesa) {
        return mesaRepository.save(mesa);
    }

    @PutMapping("/{id}")
    public Mesa atualizarMesa(@PathVariable Long id, @RequestBody Mesa mesaAtualizada) {
        return mesaRepository.findById(id)
                .map(m -> {
                    m.setDescricao(mesaAtualizada.getDescricao());
                    m.setOcupada(mesaAtualizada.isOcupada());
                    return mesaRepository.save(m);
                })
                .orElseThrow(() -> new RuntimeException("Mesa não encontrada com ID: " + id));
    }

    @DeleteMapping("/{id}")
    public void deletarMesa(@PathVariable Long id) {
        mesaRepository.deleteById(id);
    }
}

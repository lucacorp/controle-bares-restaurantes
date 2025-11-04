package com.exemplo.controlemesas.controller;

import com.exemplo.controlemesas.model.Mesa;
import com.exemplo.controlemesas.repository.MesaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/mesas")
public class MesaController {

    @Autowired
    private MesaRepository mesaRepository;

    @GetMapping
    public ResponseEntity<List<Mesa>> listar() {
        return ResponseEntity.ok(mesaRepository.findAll());
    }

    @PostMapping
    public ResponseEntity<Mesa> criar(@RequestBody Mesa mesa) {
        return ResponseEntity.ok(mesaRepository.save(mesa));
    }
}

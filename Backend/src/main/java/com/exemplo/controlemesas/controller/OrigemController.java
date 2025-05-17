package com.exemplo.controlemesas.controller;

import com.exemplo.controlemesas.model.Origem;
import com.exemplo.controlemesas.repository.OrigemRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/origem")
@CrossOrigin(origins = "http://localhost:5173") // 🔁 Para permitir acesso do frontend
public class OrigemController {

    @Autowired
    private OrigemRepository origemRepository;

    // Lista todas as origens
    @GetMapping
    public List<Origem> listarTodos() {
        return origemRepository.findAll();
    }
}

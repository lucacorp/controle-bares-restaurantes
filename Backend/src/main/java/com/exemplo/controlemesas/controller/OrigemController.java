package com.exemplo.controlemesas.controller;
import com.exemplo.controlemesas.repository.OrigemRepository;

import com.exemplo.controlemesas.model.Origem;
import com.exemplo.controlemesas.repository.OrigemRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/origem")
public class OrigemController {

    @Autowired
    private OrigemRepository origemRepository;

    // Endpoint para listar todas as Origens
    @GetMapping
    public List<Origem> listarTodos() {
        return origemRepository.findAll();
    }
}

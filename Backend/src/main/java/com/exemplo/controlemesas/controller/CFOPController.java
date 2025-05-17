package com.exemplo.controlemesas.controller;

import com.exemplo.controlemesas.model.CFOP;
import com.exemplo.controlemesas.repository.CFOPRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/cfop")
@CrossOrigin(origins = "http://localhost:5173")
public class CFOPController {

    @Autowired
    private CFOPRepository cfopRepository;

    @GetMapping
    public List<CFOP> listarTodos() {
        return cfopRepository.findAll();
    }
}

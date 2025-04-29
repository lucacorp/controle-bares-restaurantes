package com.exemplo.controlemesas.controller;

import com.exemplo.controlemesas.repository.CstRepository;
import com.exemplo.controlemesas.model.CST;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/cst")
public class CSTController {

    @Autowired
    private CstRepository cstRepository;

    // Endpoint para listar todos os CSTs
    @GetMapping
    public List<CST> listarTodos() {
        return cstRepository.findAll();
    }
}

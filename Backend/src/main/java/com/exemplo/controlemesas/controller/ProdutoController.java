package com.exemplo.controlemesas.controller;

import com.exemplo.controlemesas.dto.ProdutoDTO;

import com.exemplo.controlemesas.model.Produto;
import com.exemplo.controlemesas.repository.ProdutoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/produtos")
public class ProdutoController {

    @Autowired
    private ProdutoRepository produtoRepository;

    @GetMapping
    public List<ProdutoDTO> listarTodos() {
        return produtoRepository.findAll()
                .stream()
                .map(p -> new ProdutoDTO(p)) // usando construtor do DTO
                .collect(Collectors.toList());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProdutoDTO> buscarPorId(@PathVariable Long id) {
        return produtoRepository.findById(id)
                .map(p -> ResponseEntity.ok(new ProdutoDTO(p)))
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<ProdutoDTO> salvar(@RequestBody Produto produto) {
        Produto salvo = produtoRepository.save(produto);
        return ResponseEntity.ok(new ProdutoDTO(salvo));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        if (!produtoRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        produtoRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}

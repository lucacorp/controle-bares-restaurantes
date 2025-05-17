package com.exemplo.controlemesas.controller;

import com.exemplo.controlemesas.dto.ReceitaDTO;
import com.exemplo.controlemesas.dto.ReceitaItemDTO;
import com.exemplo.controlemesas.model.Produto;
import com.exemplo.controlemesas.model.Receita;
import com.exemplo.controlemesas.model.ReceitaItem;
import com.exemplo.controlemesas.repository.ProdutoRepository;
import com.exemplo.controlemesas.repository.ReceitaItemRepository;
import com.exemplo.controlemesas.repository.ReceitaRepository;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Validated
@RestController
@RequestMapping("/api/receitas")
@CrossOrigin(origins = "http://localhost:5173") // 🔁 CORS mais seguro e específico
public class ReceitaController {

    private static final Logger logger = LoggerFactory.getLogger(ReceitaController.class);

    @Autowired
    private ReceitaRepository receitaRepository;

    @Autowired
    private ProdutoRepository produtoRepository;

    @Autowired
    private ReceitaItemRepository receitaItemRepository;

    // Listar todas as receitas
    @GetMapping
    public List<Receita> listar() {
        return receitaRepository.findAll();
    }

    // Buscar uma receita por ID
    @GetMapping("/{id}")
    public ResponseEntity<ReceitaDTO> buscarPorId(@PathVariable Long id) {
        Optional<Receita> receitaOpt = receitaRepository.findById(id);
        return receitaOpt.map(receita -> ResponseEntity.ok(toDTO(receita)))
                         .orElse(ResponseEntity.notFound().build());
    }

    // Criar uma nova receita
    @PostMapping
    public ResponseEntity<ReceitaDTO> salvar(@RequestBody @Valid ReceitaDTO dto) {
        Receita receita = fromDTO(dto);
        receita = receitaRepository.save(receita);
        return ResponseEntity.status(201).body(toDTO(receita)); // 201 Created
    }

    // Atualizar uma receita existente
    @PutMapping("/{id}")
    public ResponseEntity<ReceitaDTO> atualizar(@PathVariable Long id, @RequestBody @Valid ReceitaDTO dto) {
        Optional<Receita> receitaOpt = receitaRepository.findById(id);
        if (receitaOpt.isEmpty()) return ResponseEntity.notFound().build();

        Receita receita = receitaOpt.get();
        receita.setNome(dto.getNome());
        receita.setAdicional(dto.getAdicional());

        Produto produtoFinal = produtoRepository.findById(dto.getProdutoFinalId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Produto final não encontrado"));
        receita.setProdutoFinal(produtoFinal);

        receita.getItens().clear();
        for (ReceitaItemDTO itemDTO : dto.getItens()) {
            Produto produto = produtoRepository.findById(itemDTO.getProdutoId())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Produto não encontrado"));

            ReceitaItem item = new ReceitaItem();
            item.setProduto(produto);
            item.setQuantidade(itemDTO.getQuantidade());
            item.setReceita(receita);
            receita.getItens().add(item);
        }

        receita = receitaRepository.save(receita);
        return ResponseEntity.ok(toDTO(receita));
    }

    // Deletar uma receita
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        if (!receitaRepository.existsById(id)) return ResponseEntity.notFound().build();
        receitaRepository.deleteById(id);
        return ResponseEntity.noContent().build(); // 204 No Content
    }

    // Converte de entidade para DTO
    private ReceitaDTO toDTO(Receita receita) {
        ReceitaDTO dto = new ReceitaDTO();
        dto.setId(receita.getId());
        dto.setNome(receita.getNome());
        dto.setAdicional(receita.getAdicional());
        dto.setProdutoFinalId(receita.getProdutoFinal().getId());

        List<ReceitaItemDTO> itensDTO = receita.getItens().stream().map(item -> {
            ReceitaItemDTO itemDTO = new ReceitaItemDTO();
            itemDTO.setProdutoId(item.getProduto().getId());
            itemDTO.setQuantidade(item.getQuantidade());
            return itemDTO;
        }).collect(Collectors.toList());

        dto.setItens(itensDTO);
        return dto;
    }

    // Converte de DTO para entidade
    private Receita fromDTO(ReceitaDTO dto) {
        Receita receita = new Receita();
        receita.setNome(dto.getNome());
        receita.setAdicional(dto.getAdicional());

        Produto produtoFinal = produtoRepository.findById(dto.getProdutoFinalId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Produto final não encontrado"));
        receita.setProdutoFinal(produtoFinal);

        List<ReceitaItem> itens = new ArrayList<>();
        for (ReceitaItemDTO itemDTO : dto.getItens()) {
            Produto produto = produtoRepository.findById(itemDTO.getProdutoId())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Produto não encontrado"));
            ReceitaItem item = new ReceitaItem();
            item.setProduto(produto);
            item.setQuantidade(itemDTO.getQuantidade());
            item.setReceita(receita);
            itens.add(item);
        }

        receita.setItens(itens);
        return receita;
    }
}

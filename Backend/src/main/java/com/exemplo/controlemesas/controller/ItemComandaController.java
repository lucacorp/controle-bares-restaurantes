package com.exemplo.controlemesas.controller;

import com.exemplo.controlemesas.dto.ItemComandaDTO;
import com.exemplo.controlemesas.model.Comanda;
import com.exemplo.controlemesas.model.ItemComanda;
import com.exemplo.controlemesas.model.Produto;
import com.exemplo.controlemesas.services.ComandaService;
import com.exemplo.controlemesas.services.ItemComandaService;
import com.exemplo.controlemesas.services.ProdutoService;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/itens-comanda")
@CrossOrigin(origins = "http://localhost:5173")
public class ItemComandaController {

    private final ItemComandaService itemComandaService;
    private final ComandaService comandaService;
    private final ProdutoService produtoService;

    public ItemComandaController(ItemComandaService itemComandaService,
                                 ComandaService comandaService,
                                 ProdutoService produtoService) {
        this.itemComandaService = itemComandaService;
        this.comandaService = comandaService;
        this.produtoService = produtoService;
    }

    @GetMapping("/{comandaId}")
    public List<ItemComandaDTO> listarPorComanda(@PathVariable Long comandaId) {
        List<ItemComanda> itens = itemComandaService.listarPorComanda(comandaId);
        return itens.stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    @PostMapping
    public ResponseEntity<?> adicionarItem(@RequestBody ItemComandaDTO dto) {
        try {
            ItemComanda item = fromDTO(dto);
            ItemComanda salvo = itemComandaService.adicionarItem(item);
            return ResponseEntity.ok(toDTO(salvo));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> removerItem(@PathVariable Long id) {
        try {
            itemComandaService.removerItem(id);
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        }
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<?> handleIllegalArgument(IllegalArgumentException ex) {
        return ResponseEntity.badRequest().body(Map.of("message", ex.getMessage()));
    }

    // ✅ Conversão de Entity -> DTO
    private ItemComandaDTO toDTO(ItemComanda item) {
        ItemComandaDTO dto = new ItemComandaDTO();
        dto.setId(item.getId());
        dto.setComandaId(item.getComanda().getId());
        dto.setProdutoId(item.getProduto().getId());
        dto.setProdutoDescricao(item.getProduto().getDescricao());
        dto.setQuantidade(item.getQuantidade());
        dto.setPrecoUnitario(item.getPrecoUnitario());
        return dto;
    }

    // ✅ Conversão de DTO -> Entity
    private ItemComanda fromDTO(ItemComandaDTO dto) {
        Comanda comanda = comandaService.buscarPorId(dto.getComandaId())
                .orElseThrow(() -> new IllegalArgumentException("Comanda não encontrada"));

        Produto produto = produtoService.buscarPorId(dto.getProdutoId())
                .orElseThrow(() -> new IllegalArgumentException("Produto não encontrado"));

        ItemComanda item = new ItemComanda();
        item.setComanda(comanda);
        item.setProduto(produto);
        item.setQuantidade(dto.getQuantidade());
        item.setPrecoUnitario(dto.getPrecoUnitario());
        return item;
    }
}

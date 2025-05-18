package com.exemplo.controlemesas.controller;

import com.exemplo.controlemesas.model.ItemComanda;
import com.exemplo.controlemesas.service.ItemComandaService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "http://localhost:5173")
@RestController
@RequestMapping("/api/itens-comanda")
public class ItemComandaController {

    private final ItemComandaService itemComandaService;

    public ItemComandaController(ItemComandaService itemComandaService) {
        this.itemComandaService = itemComandaService;
    }

    // Listar todos os itens de uma comanda específica
    @GetMapping("/{comandaId}")
    public List<ItemComanda> listarPorComanda(@PathVariable Long comandaId) {
        return itemComandaService.listarPorComanda(comandaId);
    }

    // Adicionar novo item à comanda
    @PostMapping
    public ItemComanda adicionarItem(@RequestBody ItemComanda itemComanda) {
        return itemComandaService.adicionarItem(itemComanda);
    }

    // Remover item
    @DeleteMapping("/{id}")
    public void removerItem(@PathVariable Long id) {
        itemComandaService.removerItem(id);
    }
}

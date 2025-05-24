package com.exemplo.controlemesas.controller;

import com.exemplo.controlemesas.dto.ItemComandaDTO;
import com.exemplo.controlemesas.model.ItemComanda;
import com.exemplo.controlemesas.service.ItemComandaService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@CrossOrigin(origins = "http://localhost:5173")
@RestController
@RequestMapping("/api/itens-comanda")
public class ItemComandaController {

    private final ItemComandaService itemComandaService;

    public ItemComandaController(ItemComandaService itemComandaService) {
        this.itemComandaService = itemComandaService;
    }

    // GET - listar por comanda
    @GetMapping("/{comandaId}")
    public List<ItemComandaDTO> listarPorComanda(@PathVariable Long comandaId) {
        List<ItemComanda> itens = itemComandaService.listarPorComanda(comandaId);
        return itens.stream().map(this::toDTO).collect(Collectors.toList());
    }

    // POST - adicionar item
    @PostMapping
    public ItemComanda adicionarItem(@RequestBody ItemComanda itemComanda) {
        return itemComandaService.adicionarItem(itemComanda);
    }

    // DELETE
    @DeleteMapping("/{id}")
    public void removerItem(@PathVariable Long id) {
        itemComandaService.removerItem(id);
    }

    private ItemComandaDTO toDTO(ItemComanda item) {
        ItemComandaDTO dto = new ItemComandaDTO();
        dto.setId(item.getId());
        dto.setProdutoId(item.getProduto().getId());
        dto.setProdutoNome(item.getProduto().getNome());
        dto.setQuantidade(item.getQuantidade());
        dto.setPrecoUnitario(item.getPrecoUnitario());
        return dto;
    }
}

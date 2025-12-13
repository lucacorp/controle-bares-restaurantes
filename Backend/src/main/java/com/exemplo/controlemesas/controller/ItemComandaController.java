package com.exemplo.controlemesas.controller;

import com.exemplo.controlemesas.dto.ItemComandaDTO;
import com.exemplo.controlemesas.dto.ItemComandaRequestDTO;
import com.exemplo.controlemesas.model.enums.StatusItemComanda;
import com.exemplo.controlemesas.services.ItemComandaService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/itens-comanda")
@RequiredArgsConstructor
@CrossOrigin(origins = "*") // Permite requisições de qualquer origem
public class ItemComandaController {

    private final ItemComandaService itemComandaService;

    @PostMapping
    public ResponseEntity<?> adicionarItem(@Valid @RequestBody ItemComandaRequestDTO dto) {
        try {
            ItemComandaDTO novoItem = itemComandaService.adicionarItem(dto);
            return new ResponseEntity<>(novoItem, HttpStatus.CREATED);
        } catch (IllegalArgumentException e) {
            Map<String, String> erro = new HashMap<>();
            erro.put("erro", e.getMessage());
            return new ResponseEntity<>(erro, HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            Map<String, String> erro = new HashMap<>();
            erro.put("erro", "Erro interno ao adicionar item");
            return new ResponseEntity<>(erro, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/comanda/{comandaId}")
    public ResponseEntity<List<ItemComandaDTO>> listarItensPorComanda(@PathVariable Long comandaId) {
        List<ItemComandaDTO> itens = itemComandaService.listarItensPorComanda(comandaId);
        return ResponseEntity.ok(itens);
    }

    @PutMapping("/{itemId}/status/{status}")
    public ResponseEntity<?> atualizarStatus(@PathVariable Long itemId, @PathVariable StatusItemComanda status) {
        Optional<ItemComandaDTO> itemAtualizado = itemComandaService.atualizarStatus(itemId, status);
        return itemAtualizado.<ResponseEntity<?>>map(ResponseEntity::ok)
                .orElseGet(() -> {
                    Map<String, String> erro = new HashMap<>();
                    erro.put("erro", "Item não encontrado");
                    return new ResponseEntity<>(erro, HttpStatus.NOT_FOUND);
                });
    }

    @DeleteMapping("/{itemId}")
    public ResponseEntity<?> excluirItem(@PathVariable Long itemId) {
        if (itemComandaService.excluir(itemId)) {
            return ResponseEntity.noContent().build();
        }
        Map<String, String> erro = new HashMap<>();
        erro.put("erro", "Item não encontrado");
        return new ResponseEntity<>(erro, HttpStatus.NOT_FOUND);
    }
}
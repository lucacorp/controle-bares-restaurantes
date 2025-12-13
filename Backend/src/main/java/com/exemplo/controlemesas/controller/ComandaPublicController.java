package com.exemplo.controlemesas.controller;

import com.exemplo.controlemesas.dto.ItemComandaDTO;
import com.exemplo.controlemesas.dto.ItemComandaRequestDTO;
import com.exemplo.controlemesas.services.ItemComandaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/public/comanda")
public class ComandaPublicController {

    @Autowired
    private ItemComandaService itemComandaService;

    @GetMapping("/{comandaId}/itens")
    public ResponseEntity<List<ItemComandaDTO>> listarItens(@PathVariable Long comandaId) {
        return ResponseEntity.ok(itemComandaService.listarItensPorComanda(comandaId));
    }

    @PostMapping("/{comandaId}/pedido")
    public ResponseEntity<ItemComandaDTO> adicionarPedido(@PathVariable Long comandaId,
                                                          @Valid @RequestBody ItemComandaRequestDTO dto) {
        dto.setComandaId(comandaId);
        return ResponseEntity.ok(itemComandaService.adicionarItem(dto));
    }
}
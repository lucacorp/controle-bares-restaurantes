package com.exemplo.controlemesas.controller;

import com.exemplo.controlemesas.dto.ItemComandaRequestDTO;
import com.exemplo.controlemesas.model.Comanda;
import com.exemplo.controlemesas.services.ComandaService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/garcom")
@RequiredArgsConstructor
public class GarcomController {

    private final ComandaService comandaService;

    @GetMapping("/comandas/abertas")
    public ResponseEntity<List<Comanda>> listarComandasAbertas() {
        return ResponseEntity.ok(comandaService.listarComandasAbertas());
    }

    @PostMapping("/comandas/abrir")
    public ResponseEntity<Comanda> abrirComanda(@RequestBody Comanda comanda) {
        return ResponseEntity.ok(comandaService.abrirComanda(comanda));
    }
    
    // ✅ REMOVIDO: Método 'adicionarItem' que não existe no service
    // @PostMapping("/comandas/{comandaId}/itens")
    // public ResponseEntity<Comanda> adicionarItem(
    //         @PathVariable Long comandaId,
    //         @RequestBody ItemComandaRequestDTO itemRequest,
    //         @RequestHeader("X-Usuario-Id") String garcomId
    // ) {
    //      Comanda comandaAtualizada = comandaService.adicionarItem(comandaId, itemRequest, garcomId);
    //      return ResponseEntity.ok(comandaAtualizada);
    // }

    // ✅ REMOVIDO: Método 'removerItem' que não existe no service
    // @DeleteMapping("/comandas/itens/{itemId}")
    // public ResponseEntity<Comanda> removerItem(@PathVariable Long itemId) {
    //     Comanda comandaAtualizada = comandaService.removerItem(itemId);
    //     return ResponseEntity.ok(comandaAtualizada);
    // }
}

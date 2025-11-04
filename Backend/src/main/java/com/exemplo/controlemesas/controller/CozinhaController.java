package com.exemplo.controlemesas.controller;

import com.exemplo.controlemesas.dto.ItemComandaCozinhaDTO;
import com.exemplo.controlemesas.model.ItemComanda;
import com.exemplo.controlemesas.model.enums.StatusItemComanda;
import com.exemplo.controlemesas.repository.ItemComandaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/cozinha")
@CrossOrigin(origins = {
    "http://localhost:5173",
    "http://192.168.200.107:5173"
})
@PreAuthorize("isAuthenticated()")
public class CozinhaController {

    @Autowired
    private ItemComandaRepository itemComandaRepository;

    @GetMapping("/pendentes")
    public List<ItemComandaCozinhaDTO> listarPendentes() {
        List<ItemComanda> pendentes = itemComandaRepository.findByStatus(StatusItemComanda.PENDENTE);
        return pendentes.stream()
                .map(ItemComandaCozinhaDTO::fromEntity)
                .collect(Collectors.toList());
    }

    @PutMapping("/item/{id}/preparado")
    public ResponseEntity<?> marcarComoPreparado(@PathVariable Long id) {
        return itemComandaRepository.findById(id).map(item -> {
            item.setStatus(StatusItemComanda.PREPARADO);
            itemComandaRepository.save(item);
            return ResponseEntity.ok().build();
        }).orElse(ResponseEntity.notFound().build());
    }
}
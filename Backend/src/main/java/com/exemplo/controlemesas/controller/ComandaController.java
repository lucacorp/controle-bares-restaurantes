// ComandaController.java
// Nenhuma alteração é necessária aqui, pois a rota já chama o método de serviço correto.

package com.exemplo.controlemesas.controller;

import com.exemplo.controlemesas.dto.ComandaResumoDTO;
import com.exemplo.controlemesas.model.Comanda;
import com.exemplo.controlemesas.services.ComandaService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.stream.Collectors;
import java.io.IOException;

@RestController
@RequestMapping("/api/comandas")
@RequiredArgsConstructor
@CrossOrigin(origins = "*") 
public class ComandaController {

    private final ComandaService comandaService;

    @GetMapping("/abertas")
    public ResponseEntity<List<Comanda>> listarComandasAbertas() {
        return ResponseEntity.ok(comandaService.listarComandasAbertas());
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<Comanda> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(comandaService.buscarPorId(id));
    }

    // ✅ A rota continua a chamar o método de serviço, que agora foi corrigido para buscar por status
    @GetMapping("/mesa/{mesaId}")
    public ResponseEntity<List<Comanda>> buscarComandasAtivasPorMesa(@PathVariable Long mesaId) {
        List<Comanda> comandasAtivas = comandaService.buscarComandasAtivasPorMesa(mesaId);
        return ResponseEntity.ok(comandasAtivas);
    }

    @PostMapping("/criar/{numeroMesa}")
    public ResponseEntity<Comanda> criarComandaPorMesa(@PathVariable Integer numeroMesa) {
        Comanda novaComanda = comandaService.abrirComanda(numeroMesa);
        return new ResponseEntity<>(novaComanda, HttpStatus.CREATED);
    }

    @PostMapping("/abrir")
    public ResponseEntity<Comanda> abrirComanda(@RequestBody Comanda comanda) {
        Comanda novaComanda = comandaService.abrirComanda(comanda);
        return new ResponseEntity<>(novaComanda, HttpStatus.CREATED);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluirComanda(@PathVariable Long id) {
        comandaService.excluirComanda(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/fechar")
    public ResponseEntity<ComandaResumoDTO> fecharComanda(
            @PathVariable Long id,
            @RequestParam(required = false) String nomeCliente,
            @RequestParam(required = false) String observacoes
    ) throws IOException {
        ComandaResumoDTO resumo = comandaService.fecharComanda(id, nomeCliente, observacoes);
        return ResponseEntity.ok(resumo);
    }
}
package com.exemplo.controlemesas.controller;

import com.exemplo.controlemesas.model.Comanda;
import com.exemplo.controlemesas.model.Comanda.StatusComanda;
import com.exemplo.controlemesas.model.Mesa;
import com.exemplo.controlemesas.model.Mesa.StatusMesa;
import com.exemplo.controlemesas.repository.ComandaRepository;
import com.exemplo.controlemesas.repository.MesaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/mesas")
@CrossOrigin(origins = "http://localhost:5173")
public class MesaController {

    @Autowired
    private MesaRepository mesaRepository;

    @Autowired
    private ComandaRepository comandaRepository;

    @GetMapping
    public List<Mesa> listarTodas() {
        return mesaRepository.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Mesa> buscarPorId(@PathVariable Long id) {
        Optional<Mesa> mesaOpt = mesaRepository.findById(id);
        return mesaOpt.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping("/{id}/finalizar")
    public ResponseEntity<?> finalizarMesa(
            @PathVariable Long id,
            @RequestParam(required = false) Integer dividirPor) {

        Mesa mesa = mesaRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Mesa não encontrada."));

        List<Comanda> comandasAbertas = comandaRepository.findByMesaIdAndStatusAndAtivoTrue(id, StatusComanda.ABERTA);

        BigDecimal total = BigDecimal.ZERO;

        for (Comanda comanda : comandasAbertas) {
            comanda.setStatus(StatusComanda.FECHADA);
            comanda.setDataFechamento(LocalDateTime.now());
            comanda.setAtivo(false);
            comandaRepository.save(comanda);

            BigDecimal totalComanda = comanda.getItens().stream()
                    .map(item -> BigDecimal.valueOf(item.getPrecoUnitario())
                            .multiply(BigDecimal.valueOf(item.getQuantidade())))
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            total = total.add(totalComanda);
        }

        mesa.setStatus(StatusMesa.LIVRE);
        mesa.setOcupada(false);
        mesaRepository.save(mesa);

        BigDecimal valorPorPessoa = dividirPor != null && dividirPor > 0
                ? total.divide(BigDecimal.valueOf(dividirPor), 2, RoundingMode.HALF_UP)
                : total;

        Map<String, Object> response = Map.of(
                "message", "Finalização realizada com sucesso!",
                "total", total,
                "valorPorPessoa", valorPorPessoa
        );

        return ResponseEntity.ok(response);
    }

    @PostMapping
    public ResponseEntity<Mesa> criar(@RequestBody Mesa mesa) {
        mesa.setStatus(StatusMesa.LIVRE);
        mesa.setOcupada(false);
        Mesa salva = mesaRepository.save(mesa);
        return ResponseEntity.ok(salva);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Mesa> atualizar(@PathVariable Long id, @RequestBody Mesa mesaAtualizada) {
        return mesaRepository.findById(id).map(mesa -> {
            mesa.setDescricao(mesaAtualizada.getDescricao());
            mesa.setOcupada(mesaAtualizada.isOcupada());
            mesa.setStatus(mesaAtualizada.getStatus());
            mesaRepository.save(mesa);
            return ResponseEntity.ok(mesa);
        }).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> excluir(@PathVariable Long id) {
        if (!mesaRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }

        if (comandaRepository.existsByMesaId(id)) {
            return ResponseEntity.status(409).body("Não é possível excluir. Existem comandas associadas a esta mesa.");
        }

        mesaRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}

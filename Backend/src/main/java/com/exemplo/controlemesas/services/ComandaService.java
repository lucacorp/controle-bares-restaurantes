package com.exemplo.controlemesas.services;

import com.exemplo.controlemesas.model.Comanda;
import com.exemplo.controlemesas.model.ComandaResumo;
import com.exemplo.controlemesas.repository.ComandaRepository;
import com.exemplo.controlemesas.repository.ComandaResumoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class ComandaService {

    @Autowired
    private ComandaRepository comandaRepository;

    @Autowired
    private ComandaResumoRepository comandaResumoRepository;

    public List<Comanda> listarComandasAbertas() {
        return comandaRepository.findByDataFechamentoIsNull();
    }

    public Optional<Comanda> buscarPorId(Long id) {
        return comandaRepository.findById(id);
    }

    public Comanda fecharComanda(Long id, String nomeCliente, String observacoes) {
        Comanda comanda = comandaRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Comanda não encontrada"));

        if (comanda.getDataFechamento() != null) {
            throw new IllegalArgumentException("Comanda já está finalizada.");
        }

        comanda.setDataFechamento(LocalDateTime.now());
        comanda.setStatus(Comanda.StatusComanda.FECHADA);

        BigDecimal total = comanda.getItens().stream()
                .map(item -> BigDecimal.valueOf(item.getPrecoUnitario())
                        .multiply(BigDecimal.valueOf(item.getQuantidade())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        ComandaResumo resumo = new ComandaResumo();
        resumo.setComanda(comanda);
        resumo.setDataFechamento(comanda.getDataFechamento());
        resumo.setTotal(total);
        resumo.setNomeCliente(nomeCliente);
        resumo.setObservacoes(observacoes);

        comandaResumoRepository.save(resumo);
        return comandaRepository.save(comanda);
    }
}

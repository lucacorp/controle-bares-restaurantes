package com.exemplo.controlemesas.services;

import com.exemplo.controlemesas.model.Comanda;
import com.exemplo.controlemesas.repository.ComandaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class ComandaService {

    @Autowired
    private ComandaRepository comandaRepository;

    public List<Comanda> listarComandasAbertas() {
        return comandaRepository.findByDataFechamentoIsNull();
    }

    public Optional<Comanda> buscarPorId(Long id) {
        return comandaRepository.findById(id);
    }

    public Comanda fecharComanda(Long id) {
        Comanda comanda = comandaRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Comanda não encontrada"));

        if (comanda.getDataFechamento() != null) {
            throw new IllegalArgumentException("Comanda já está finalizada.");
        }

        comanda.setDataFechamento(LocalDateTime.now());
        comanda.setStatus(Comanda.StatusComanda.FECHADA);

        return comandaRepository.save(comanda);
    }
}

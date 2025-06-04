package com.exemplo.controlemesas.services;

import com.exemplo.controlemesas.model.Comanda;
import com.exemplo.controlemesas.model.ComandaResumo;
import com.exemplo.controlemesas.model.Mesa;
import com.exemplo.controlemesas.repository.ComandaRepository;
import com.exemplo.controlemesas.repository.ComandaResumoRepository;
import com.exemplo.controlemesas.repository.MesaRepository;

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

    @Autowired
    private MesaRepository mesaRepository;

    /**
     * Lista apenas comandas abertas e ativas.
     */
    public List<Comanda> listarComandasAbertas() {
        return comandaRepository.findByStatusAndAtivoTrue(Comanda.StatusComanda.ABERTA);
    }

    public Optional<Comanda> buscarPorId(Long id) {
        return comandaRepository.findById(id)
                .filter(Comanda::isAtivo);
    }

    /**
     * Fecha uma comanda ativa, cria resumo e marca como fechada.
     */
    public Comanda fecharComanda(Long id, String nomeCliente, String observacoes) {
        Comanda comanda = comandaRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Comanda não encontrada"));

        if (comanda.getStatus() == Comanda.StatusComanda.FECHADA) {
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

    /**
     * Soft delete: desativa a comanda.
     */
    public void desativarComanda(Long id) {
        Comanda comanda = comandaRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Comanda não encontrada"));

        comanda.setAtivo(false);
        comandaRepository.save(comanda);
    }

    /**
     * Cria uma comanda e altera status da mesa para OCUPADA.
     */
    public Comanda criarComanda(Long mesaId) {
        Mesa mesa = mesaRepository.findById(mesaId)
                .orElseThrow(() -> new IllegalArgumentException("Mesa não encontrada."));

        if (mesa.getStatus() != Mesa.StatusMesa.OCUPADA) {
            mesa.setStatus(Mesa.StatusMesa.OCUPADA);
            mesa.setOcupada(true);
            mesaRepository.save(mesa);
        }

        Comanda comanda = new Comanda();
        comanda.setMesa(mesa);
        comanda.setDataAbertura(LocalDateTime.now());
        comanda.setStatus(Comanda.StatusComanda.ABERTA);
        comanda.setAtivo(true);

        return comandaRepository.save(comanda);
    }
}

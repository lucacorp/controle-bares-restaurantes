package com.exemplo.controlemesas.service;

import com.exemplo.controlemesas.model.Comanda;
import com.exemplo.controlemesas.model.Comanda.StatusComanda;
import com.exemplo.controlemesas.repository.ComandaRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class ComandaService {

    private final ComandaRepository comandaRepository;

    public ComandaService(ComandaRepository comandaRepository) {
        this.comandaRepository = comandaRepository;
    }

    // Listar todas as comandas
    public List<Comanda> listarTodas() {
        return comandaRepository.findAll();
    }

    // Buscar comanda por ID
    public Optional<Comanda> buscarPorId(Long id) {
        return comandaRepository.findById(id);
    }

    // Abrir uma nova comanda, validando se já existe comanda aberta para a mesa
    public Comanda abrirComanda(Comanda comanda) {
        if (comanda.getMesa() == null || comanda.getMesa().getId() == null) {
            throw new IllegalArgumentException("Mesa obrigatória para abrir uma comanda.");
        }

        Optional<Comanda> existente = comandaRepository.findByMesaIdAndStatus(
                comanda.getMesa().getId(), StatusComanda.ABERTA);

        if (existente.isPresent()) {
            throw new RuntimeException("Já existe uma comanda aberta para esta mesa.");
        }

        comanda.setDataAbertura(LocalDateTime.now());
        comanda.setStatus(StatusComanda.ABERTA);
        return comandaRepository.save(comanda);
    }

    // Fechar uma comanda (definindo data de fechamento e status)
    public Comanda fecharComanda(Long id) {
        Comanda comanda = comandaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Comanda não encontrada"));

        if (comanda.getStatus() == StatusComanda.FECHADA) {
            throw new RuntimeException("A comanda já está fechada.");
        }

        comanda.setDataFechamento(LocalDateTime.now());
        comanda.setStatus(StatusComanda.FECHADA);
        return comandaRepository.save(comanda);
    }

    // Deletar comanda por ID
    public void deletar(Long id) {
        if (!comandaRepository.existsById(id)) {
            throw new RuntimeException("Comanda não encontrada.");
        }
        comandaRepository.deleteById(id);
    }

    // Buscar comandas por status
    public List<Comanda> buscarPorStatus(StatusComanda status) {
        return comandaRepository.findByStatus(status);
    }

    // Buscar comandas por mesa
    public List<Comanda> buscarPorMesa(Long mesaId) {
        return comandaRepository.findByMesaId(mesaId);
    }

    // Buscar comandas abertas (sem data de fechamento)
    public List<Comanda> buscarAbertas() {
        return comandaRepository.findByDataFechamentoIsNull();
    }
}

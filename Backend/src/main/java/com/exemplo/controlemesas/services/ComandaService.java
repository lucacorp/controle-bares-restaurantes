// ComandaService.java

package com.exemplo.controlemesas.services;

import com.exemplo.controlemesas.dto.ComandaResumoDTO;
import com.exemplo.controlemesas.model.Comanda;
import com.exemplo.controlemesas.model.ComandaResumo;
import com.exemplo.controlemesas.model.ItemComanda;
import com.exemplo.controlemesas.model.ItemComandaResumo;
import com.exemplo.controlemesas.model.enums.StatusComanda;
import com.exemplo.controlemesas.repository.ComandaRepository;
import com.exemplo.controlemesas.repository.ComandaResumoRepository;
import com.exemplo.controlemesas.repository.ItemComandaRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ComandaService {

    private final ComandaRepository comandaRepository;
    private final ItemComandaRepository itemComandaRepository;
    private final ComandaResumoRepository comandaResumoRepository;
    private final NfeService nfeService;

    public List<Comanda> listarComandasAbertas() {
        return comandaRepository.findByStatus(StatusComanda.ABERTA);
    }

    public Comanda abrirComanda(Comanda comanda) {
        if (comanda.getId() != null && comanda.getStatus() == StatusComanda.ABERTA) {
            throw new IllegalArgumentException("Comanda já está aberta.");
        }
        comanda.setStatus(StatusComanda.ABERTA);
        comanda.setDataAbertura(LocalDateTime.now());
        return comandaRepository.save(comanda);
    }
    
    @Transactional 
    public Comanda abrirComanda(Integer numeroMesa) {
        Comanda comanda = new Comanda();
        comanda.setNumeroMesa(numeroMesa);
        comanda.setStatus(StatusComanda.ABERTA);
        comanda.setDataAbertura(LocalDateTime.now());
        return comandaRepository.save(comanda);
    }

    public Comanda buscarPorId(Long id) {
        return comandaRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Comanda não encontrada com o ID: " + id));
    }
    
    // ✅ CORREÇÃO CRÍTICA: O método agora retorna a lista, mesmo se estiver vazia.
    // O backend agora não lança mais uma exceção.
    public List<Comanda> buscarComandasAtivasPorMesa(Long mesaId) {
        return comandaRepository.findByMesaIdAndStatus(mesaId, StatusComanda.ABERTA);
    }

    public void excluirComanda(Long id) {
        Comanda comanda = comandaRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Comanda não encontrada."));

        if (comanda.getStatus() == StatusComanda.FECHADA) {
            throw new IllegalArgumentException("Comanda fechada não pode ser excluída.");
        }
        comandaRepository.deleteById(id);
    }

    @Transactional
    public ComandaResumoDTO fecharComanda(Long id, String nomeCliente, String observacoes) {
        try {
            Comanda comanda = comandaRepository.findById(id)
                    .orElseThrow(() -> new IllegalArgumentException("Comanda não encontrada"));

            if (comanda.getStatus() == StatusComanda.FECHADA) {
                throw new IllegalArgumentException("Comanda já está finalizada.");
            }

            comanda.setDataFechamento(LocalDateTime.now());
            comanda.setStatus(StatusComanda.FECHADA);
            
            List<ItemComanda> itens = itemComandaRepository.findByComandaId(comanda.getId());
            
            BigDecimal total = itens.stream()
                    .map(item -> item.getTotal() != null ? item.getTotal() : BigDecimal.ZERO)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            ComandaResumo resumo = new ComandaResumo();
            resumo.setComanda(comanda);
            resumo.setDataFechamento(comanda.getDataFechamento());
            resumo.setTotal(total);
            resumo.setNomeCliente(nomeCliente);
            resumo.setObservacoes(observacoes);
            
            resumo.setItens(itens.stream()
                .map(item -> {
                    ItemComandaResumo resumoItem = new ItemComandaResumo();
                    resumoItem.setProdutoId(item.getProduto().getId());
                    resumoItem.setDescricao(item.getProduto().getNome());
                    resumoItem.setUnMedida("UN"); 
                    resumoItem.setQuantidade(new BigDecimal(item.getQuantidade()));
                    resumoItem.setPrecoUnitario(item.getPrecoVenda());
                    resumoItem.setSubtotal(item.getTotal());
                    return resumoItem;
                })
                .collect(Collectors.toList()));
            
            comandaResumoRepository.save(resumo);
            comandaRepository.save(comanda);
            
            nfeService.emitir(comanda.getId());

            return new ComandaResumoDTO(resumo);

        } catch (IOException e) {
            log.error("Erro ao se comunicar com o serviço de NFC-e: {}", e.getMessage(), e);
            throw new RuntimeException("Erro ao processar a nota fiscal. Verifique o status da nota mais tarde.", e);
        }
    }
}

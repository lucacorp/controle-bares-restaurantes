package com.exemplo.controlemesas.services;

import com.exemplo.controlemesas.dto.ComandaResumoDTO;
import com.exemplo.controlemesas.model.*;
import com.exemplo.controlemesas.model.enums.StatusComanda;
import com.exemplo.controlemesas.repository.*;
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
    private final MesaRepository mesaRepository; // ✅ necessário para associar mesa
    private final ItemComandaRepository itemComandaRepository;
    private final ComandaResumoRepository comandaResumoRepository;
    private final NfeService nfeService;

    /**
     * Lista todas as comandas abertas.
     */
    public List<Comanda> listarComandasAbertas() {
        return comandaRepository.findByStatus(StatusComanda.ABERTA);
    }

    /**
     * Abre uma nova comanda a partir de um objeto recebido do frontend.
     */
    @Transactional
    public Comanda abrirComanda(Comanda comanda) {
        if (comanda.getId() != null && comanda.getStatus() == StatusComanda.ABERTA) {
            throw new IllegalArgumentException("Comanda já está aberta.");
        }

        // Se o frontend enviar número da mesa, busca a mesa correspondente
        if (comanda.getNumeroMesa() != null) {
            Mesa mesa = mesaRepository.findByNumero(comanda.getNumeroMesa())
                    .orElseThrow(() -> new RuntimeException("Mesa não encontrada: " + comanda.getNumeroMesa()));
            comanda.setMesa(mesa);
        }

        comanda.setStatus(StatusComanda.ABERTA);
        comanda.setDataAbertura(LocalDateTime.now());
        comanda.setAtiva(true);

        return comandaRepository.save(comanda);
    }

    /**
     * Abre uma nova comanda diretamente pelo número da mesa.
     */
    @Transactional
    public Comanda abrirComanda(Integer numeroMesa) {
        Mesa mesa = mesaRepository.findByNumero(numeroMesa)
                .orElseThrow(() -> new RuntimeException("Mesa não encontrada: " + numeroMesa));

        Comanda comanda = new Comanda();
        comanda.setMesa(mesa);
        comanda.setNumeroMesa(numeroMesa);
        comanda.setStatus(StatusComanda.ABERTA);
        comanda.setDataAbertura(LocalDateTime.now());
        comanda.setAtiva(true);

        return comandaRepository.save(comanda);
    }


    /**
     * Busca uma comanda pelo ID.
     */
    public Comanda buscarPorId(Long id) {
        return comandaRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Comanda não encontrada com o ID: " + id));
    }

    /**
     * Retorna todas as comandas ativas (abertas) de uma mesa.
     */
    public List<Comanda> buscarComandasAtivasPorMesa(Long mesaId) {
        return comandaRepository.findByMesaIdAndStatus(mesaId, StatusComanda.ABERTA);
    }

    /**
     * Exclui uma comanda, se ela estiver aberta.
     */
    public void excluirComanda(Long id) {
        Comanda comanda = comandaRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Comanda não encontrada."));

        if (comanda.getStatus() == StatusComanda.FECHADA) {
            throw new IllegalArgumentException("Comanda fechada não pode ser excluída.");
        }

        comandaRepository.deleteById(id);
    }

    /**
     * Fecha uma comanda, gera o resumo e emite a NFC-e.
     */
    @Transactional
    public ComandaResumoDTO fecharComanda(Long id, String nomeCliente, String observacoes) {
        Comanda comanda = comandaRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Comanda não encontrada."));

        if (comanda.getStatus() == StatusComanda.FECHADA) {
            throw new IllegalArgumentException("Comanda já está finalizada.");
        }

        ComandaResumo resumo = null;
        try {
            comanda.setDataFechamento(LocalDateTime.now());
            comanda.setStatus(StatusComanda.FECHADA);

            List<ItemComanda> itens = itemComandaRepository.findByComandaId(comanda.getId());

            BigDecimal total = itens.stream()
                    .map(item -> item.getTotal() != null ? item.getTotal() : BigDecimal.ZERO)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            resumo = new ComandaResumo();
            resumo.setComanda(comanda);
            resumo.setDataFechamento(comanda.getDataFechamento());
            resumo.setTotal(total);
            resumo.setNomeCliente(nomeCliente);
            resumo.setObservacoes(observacoes);

            // create a final local reference so it can be used inside the lambda
            final ComandaResumo resumoLocal = resumo;

            // Build itens list and ensure itemNo is set (DB column is NOT NULL)
            List<ItemComandaResumo> resumoItens = new java.util.ArrayList<>();
            int seq = 1;
            for (ItemComanda item : itens) {
                ItemComandaResumo resumoItem = new ItemComandaResumo();
                resumoItem.setComandaResumo(resumoLocal);
                resumoItem.setProdutoId(item.getProduto() != null ? item.getProduto().getId() : null);
                resumoItem.setDescricao(item.getProduto() != null ? item.getProduto().getNome() : null);
                resumoItem.setUnMedida("UN");
                resumoItem.setQuantidade(item.getQuantidade() != null ? BigDecimal.valueOf(item.getQuantidade()) : BigDecimal.ZERO);
                resumoItem.setPrecoUnitario(item.getPrecoVenda());
                resumoItem.setSubtotal(item.getTotal());
                resumoItem.setItemNo(seq++);
                resumoItens.add(resumoItem);
            }
            resumo.setItens(resumoItens);

            // persist resumo and comanda; use saveAndFlush to ensure ID is generated immediately
            resumo = comandaResumoRepository.saveAndFlush(resumo);
            comandaRepository.save(comanda);
            log.debug("Resumo salvo com id={}", resumo.getId());

            // Attempt to emit NFC-e using the resumo id if available
            Long resumoId = resumo.getId();
            if (resumoId == null) {
                log.warn("Resumo salvo sem id, pulando emissão de NFe por enquanto (resumo={})", resumo);
            } else {
                try {
                    emitNfe(resumoId);
                } catch (IOException e) {
                    log.error("Erro de IO ao emitir NFC-e para resumo {}: {}", resumo.getId(), e.getMessage(), e);
                    try {
                        resumo.setStatusFiscal("ERRO");
                        comandaResumoRepository.save(resumo);
                    } catch (Exception ex) {
                        log.warn("Falha ao atualizar status fiscal do resumo {} para ERRO: {}", resumo.getId(), ex.getMessage(), ex);
                    }
                    // Não propagar a exceção para o cliente — retornamos o resumo marcado como ERRO
                    log.info("Retornando resumo com status ERRO para comanda {} após falha na emissão da NFe.", comanda.getId());
                    return new ComandaResumoDTO(resumo);
                } catch (IllegalArgumentException | IllegalStateException e) {
                    log.error("Erro ao emitir NFC-e para resumo {}: {}", resumo.getId(), e.getMessage(), e);
                    try {
                        resumo.setStatusFiscal("ERRO");
                        comandaResumoRepository.save(resumo);
                    } catch (Exception ex) {
                        log.warn("Falha ao atualizar status fiscal do resumo {} para ERRO: {}", resumo.getId(), ex.getMessage(), ex);
                    }
                    log.info("Retornando resumo com status ERRO para comanda {} após erro na emissão da NFe.", comanda.getId());
                    return new ComandaResumoDTO(resumo);
                }
            }

            return new ComandaResumoDTO(resumo);
        } catch (Throwable t) {
            // If it's a runtime exception (we intentionally rethrew for IO/illegal-state), propagate it
            if (t instanceof RuntimeException) {
                throw (RuntimeException) t;
            }
            log.error("Erro ao fechar comanda {}: {}", id, t.getMessage(), t);
            // attempt to persist an error resumo if possível
            try {
                if (resumo == null) {
                    resumo = new ComandaResumo();
                    resumo.setComanda(comanda);
                    resumo.setDataFechamento(LocalDateTime.now());
                }
                resumo.setStatusFiscal("ERRO");
                resumo = comandaResumoRepository.save(resumo);
            } catch (Exception ex) {
                log.warn("Falha ao salvar resumo de erro para comanda {}: {}", id, ex.getMessage(), ex);
            }
            return new ComandaResumoDTO(resumo);
        }
    }

    /**
     * Wrapper around nfeService.emitir to make it overridable in tests.
     */
    protected void emitNfe(Long resumoId) throws IOException {
        if (resumoId == null) {
            // defensive: nothing to emit
            log.warn("emitNfe called with null resumoId, skipping emission");
            return;
        }
        nfeService.emitir(resumoId);
    }
}
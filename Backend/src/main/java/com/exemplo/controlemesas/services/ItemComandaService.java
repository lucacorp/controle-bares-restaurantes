package com.exemplo.controlemesas.services;

import com.exemplo.controlemesas.dto.ItemComandaDTO;
import com.exemplo.controlemesas.dto.ItemComandaRequestDTO;
import com.exemplo.controlemesas.model.Comanda;
import com.exemplo.controlemesas.model.ItemComanda;
import com.exemplo.controlemesas.model.Produto;
import com.exemplo.controlemesas.model.enums.StatusComanda;
import com.exemplo.controlemesas.model.enums.StatusItemComanda;
import com.exemplo.controlemesas.repository.ComandaRepository;
import com.exemplo.controlemesas.repository.ItemComandaRepository;
import com.exemplo.controlemesas.repository.ProdutoRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ItemComandaService {

    private final ItemComandaRepository itemComandaRepository;
    private final ComandaRepository comandaRepository;
    private final ProdutoRepository produtoRepository;

    @Transactional
    public ItemComandaDTO adicionarItem(ItemComandaRequestDTO dto) {
        log.info("Adicionando item à comanda com ID: {}", dto.getComandaId());

        Comanda comanda = comandaRepository.findById(dto.getComandaId())
                .orElseThrow(() -> {
                    log.error("Comanda não encontrada com o ID: {}", dto.getComandaId());
                    return new IllegalArgumentException("Comanda não encontrada");
                });

        // ✅ Verificação extra: só permite adicionar itens se a comanda estiver ABERTA
        if (!StatusComanda.ABERTA.equals(comanda.getStatus())) {
            throw new IllegalArgumentException("Apenas comandas abertas podem receber itens");
        }

        Produto produto = produtoRepository.findById(dto.getProdutoId())
                .orElseThrow(() -> new IllegalArgumentException("Produto não encontrado"));

        ItemComanda item = new ItemComanda();
        item.setComanda(comanda);
        item.setProduto(produto);
        item.setQuantidade(dto.getQuantidade());
        item.setDataRegistro(LocalDateTime.now());
        item.setStatus(StatusItemComanda.PENDENTE);

        BigDecimal precoVenda = produto.getPrecoVenda();
        item.setPrecoVenda(precoVenda);

        BigDecimal total = precoVenda.multiply(BigDecimal.valueOf(dto.getQuantidade()));
        item.setTotal(total);

        ItemComanda salvo = itemComandaRepository.save(item);
        log.info("Item adicionado com sucesso à comanda {}", comanda.getId());
        return new ItemComandaDTO(salvo);
    }

    public List<ItemComandaDTO> listarItensPorComanda(Long comandaId) {
        List<ItemComanda> itens = itemComandaRepository.findByComandaId(comandaId);
        return itens.stream().map(ItemComandaDTO::new).collect(Collectors.toList());
    }

    @Transactional
    public Optional<ItemComandaDTO> atualizarStatus(Long itemId, StatusItemComanda status) {
        return itemComandaRepository.findById(itemId).map(item -> {
            item.setStatus(status);
            return new ItemComandaDTO(itemComandaRepository.save(item));
        });
    }

    @Transactional
    public boolean excluir(Long itemId) {
        return itemComandaRepository.findById(itemId).map(item -> {
            itemComandaRepository.delete(item);
            return true;
        }).orElse(false);
    }
}

package com.exemplo.controlemesas.services;

import com.exemplo.controlemesas.model.Comanda;
import com.exemplo.controlemesas.model.ItemComanda;
import com.exemplo.controlemesas.model.Produto;
import com.exemplo.controlemesas.repository.ComandaRepository;
import com.exemplo.controlemesas.repository.ItemComandaRepository;
import com.exemplo.controlemesas.repository.ProdutoRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ItemComandaService {

    private final ItemComandaRepository itemComandaRepository;
    private final ComandaRepository comandaRepository;
    private final ProdutoRepository produtoRepository;

    public ItemComandaService(ItemComandaRepository itemComandaRepository, 
                              ComandaRepository comandaRepository,
                              ProdutoRepository produtoRepository) {
        this.itemComandaRepository = itemComandaRepository;
        this.comandaRepository = comandaRepository;
        this.produtoRepository = produtoRepository;
    }

    public List<ItemComanda> listarPorComanda(Long comandaId) {
        return itemComandaRepository.findByComandaId(comandaId);
    }

    public ItemComanda adicionarItem(ItemComanda itemComanda) {

        if (itemComanda.getComanda() == null || itemComanda.getComanda().getId() == null) {
            throw new IllegalArgumentException("Comanda inválida.");
        }

        if (itemComanda.getProduto() == null || itemComanda.getProduto().getId() == null) {
            throw new IllegalArgumentException("Produto inválido.");
        }

        if (itemComanda.getPrecoUnitario() <= 0) {
            throw new IllegalArgumentException("Preço inválido.");
        }

        // Busca Comanda e Produto do banco para garantir integridade
        Comanda comanda = comandaRepository.findById(itemComanda.getComanda().getId())
                .orElseThrow(() -> new IllegalArgumentException("Comanda não encontrada"));

        Produto produto = produtoRepository.findById(itemComanda.getProduto().getId())
                .orElseThrow(() -> new IllegalArgumentException("Produto não encontrado"));

        itemComanda.setComanda(comanda);
        itemComanda.setProduto(produto);

        return itemComandaRepository.save(itemComanda);
    }

    public void removerItem(Long id) {
        itemComandaRepository.deleteById(id);
    }
}

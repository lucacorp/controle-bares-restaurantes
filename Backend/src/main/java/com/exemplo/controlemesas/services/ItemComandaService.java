package com.exemplo.controlemesas.service;

import com.exemplo.controlemesas.model.ItemComanda;
import com.exemplo.controlemesas.repository.ItemComandaRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ItemComandaService {

    private final ItemComandaRepository itemComandaRepository;

    public ItemComandaService(ItemComandaRepository itemComandaRepository) {
        this.itemComandaRepository = itemComandaRepository;
    }

    public List<ItemComanda> listarPorComanda(Long comandaId) {
        return itemComandaRepository.findByComandaId(comandaId);
    }

    public ItemComanda adicionarItem(ItemComanda itemComanda) {
        // Você pode aplicar regras aqui, como:
        // - validar se a comanda está aberta
        // - buscar o preço do produto do banco (para manter consistência)
        return itemComandaRepository.save(itemComanda);
    }

    public void removerItem(Long id) {
        itemComandaRepository.deleteById(id);
    }
}

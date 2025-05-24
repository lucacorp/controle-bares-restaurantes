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
        return itemComandaRepository.save(itemComanda);
    }

    public void removerItem(Long id) {
        itemComandaRepository.deleteById(id);
    }
}

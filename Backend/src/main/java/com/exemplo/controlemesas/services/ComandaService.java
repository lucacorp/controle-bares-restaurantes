package com.exemplo.controlemesas.services;

import com.exemplo.controlemesas.model.Comanda;
import com.exemplo.controlemesas.repository.ComandaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ComandaService {

    @Autowired
    private ComandaRepository comandaRepository;

    public List<Comanda> listarComandasAbertas() {
        return comandaRepository.findByDataFechamentoIsNull();
    }
}

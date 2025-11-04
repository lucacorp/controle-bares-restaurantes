package com.exemplo.controlemesas.services;

import com.exemplo.controlemesas.model.ComandaResumo;
import com.exemplo.controlemesas.repository.ComandaResumoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ComandaResumoService {

    private final ComandaResumoRepository comandaResumoRepository;

    public List<ComandaResumo> listar() {
        return comandaResumoRepository.findAll();
    }
}

package com.exemplo.controlemesas.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.exemplo.controlemesas.model.CFOP;  // Certifique-se de importar o modelo correto

public interface CFOPRepository extends JpaRepository<CFOP, Long> {
    // O método findAll() é fornecido automaticamente pela interface JpaRepository
}

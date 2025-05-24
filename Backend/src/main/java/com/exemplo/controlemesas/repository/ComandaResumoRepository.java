package com.exemplo.controlemesas.repository;

import com.exemplo.controlemesas.model.ComandaResumo;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ComandaResumoRepository extends JpaRepository<ComandaResumo, Long> {
    List<ComandaResumo> findByComandaId(Long comandaId);
}

package com.exemplo.controlemesas.repository;

import com.exemplo.controlemesas.model.Comanda;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ComandaRepository extends JpaRepository<Comanda, Long> {

    boolean existsByMesaId(Long mesaId);

    List<Comanda> findByDataFechamentoIsNull();

    List<Comanda> findByMesaId(Long mesaId);
}

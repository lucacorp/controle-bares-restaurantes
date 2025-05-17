package com.exemplo.controlemesas.repository;

import com.exemplo.controlemesas.model.Comanda;
import com.exemplo.controlemesas.model.Comanda.StatusComanda;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ComandaRepository extends JpaRepository<Comanda, Long> {

    List<Comanda> findByStatus(StatusComanda status);

    Optional<Comanda> findByMesaIdAndStatus(Long mesaId, StatusComanda status);

    List<Comanda> findByMesaId(Long mesaId);

    List<Comanda> findByDataFechamentoIsNull();

    
    boolean existsByMesaId(Long mesaId);
}

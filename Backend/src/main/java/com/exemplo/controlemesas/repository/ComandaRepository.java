package com.exemplo.controlemesas.repository;

import com.exemplo.controlemesas.model.Comanda;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ComandaRepository extends JpaRepository<Comanda, Long> {

    Optional<Comanda> findByMesaIdAndDataFechamentoIsNull(Long mesaId);

    List<Comanda> findByDataFechamentoIsNull();

    boolean existsByMesaId(Long mesaId);
}

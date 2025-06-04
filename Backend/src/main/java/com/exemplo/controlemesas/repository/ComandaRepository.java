package com.exemplo.controlemesas.repository;

import com.exemplo.controlemesas.model.Comanda;
import com.exemplo.controlemesas.model.Comanda.StatusComanda;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ComandaRepository extends JpaRepository<Comanda, Long> {

    List<Comanda> findByStatusAndAtivoTrue(Comanda.StatusComanda status);

    List<Comanda> findByMesaIdAndStatusAndAtivoTrue(Long mesaId, Comanda.StatusComanda status);

    List<Comanda> findByMesaIdAndAtivoTrue(Long mesaId);

    boolean existsByMesaId(Long mesaId);

    List<Comanda> findByDataFechamentoIsNull();

    boolean existsByMesaIdAndAtivoTrue(Long mesaId);
}

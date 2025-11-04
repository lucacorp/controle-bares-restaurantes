// ComandaRepository.java
// O método foi alterado para usar o campo 'status', que é o correto na lógica da aplicação.

package com.exemplo.controlemesas.repository;

import com.exemplo.controlemesas.model.Comanda;
import com.exemplo.controlemesas.model.enums.StatusComanda;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ComandaRepository extends JpaRepository<Comanda, Long> {

    List<Comanda> findByStatus(StatusComanda status);

    List<Comanda> findByAtivaTrue();

    List<Comanda> findByStatusAndAtivaTrue(StatusComanda status);

    // ✅ CORREÇÃO CRÍTICA: A consulta agora usa o status da comanda para encontrar as ativas
    List<Comanda> findByMesaIdAndStatus(Long mesaId, StatusComanda status);

    List<Comanda> findByNumeroMesa(Integer numeroMesa);
}
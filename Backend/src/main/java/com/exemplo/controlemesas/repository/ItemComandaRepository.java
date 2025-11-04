package com.exemplo.controlemesas.repository;

import com.exemplo.controlemesas.model.ItemComanda;
import com.exemplo.controlemesas.model.enums.StatusItemComanda;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ItemComandaRepository extends JpaRepository<ItemComanda, Long> {
    List<ItemComanda> findByComandaId(Long comandaId);
    List<ItemComanda> findByStatus(StatusItemComanda status);
    
}
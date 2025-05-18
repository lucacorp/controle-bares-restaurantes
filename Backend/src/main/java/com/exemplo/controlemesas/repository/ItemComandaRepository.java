package com.exemplo.controlemesas.repository;

import com.exemplo.controlemesas.model.ItemComanda;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ItemComandaRepository extends JpaRepository<ItemComanda, Long> {

    List<ItemComanda> findByComandaId(Long comandaId);
}

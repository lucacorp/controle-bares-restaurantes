package com.exemplo.controlemesas.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.exemplo.controlemesas.model.Mesa;

@Repository
public interface MesaRepository extends JpaRepository<Mesa, Long> {
}

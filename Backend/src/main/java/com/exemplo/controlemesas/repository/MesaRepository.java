// MesaRepository.java
package com.exemplo.controlemesas.repository;

import com.exemplo.controlemesas.model.Mesa;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MesaRepository extends JpaRepository<Mesa, Long> {
}

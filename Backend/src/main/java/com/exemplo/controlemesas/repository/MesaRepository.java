// MesaRepository.java
package com.exemplo.controlemesas.repository;

import com.exemplo.controlemesas.model.Mesa;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface MesaRepository extends JpaRepository<Mesa, Long> {
	
	Optional<Mesa> findByNumero(Integer numero);
}
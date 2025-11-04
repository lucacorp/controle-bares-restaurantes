// src/main/java/com/exemplo/controlemesas/repository/ReceitaRepository.java
package com.exemplo.controlemesas.repository;

import com.exemplo.controlemesas.model.Receita;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ReceitaRepository extends JpaRepository<Receita, Long> {

	// ⬇️ CORRETO: navega pela propriedade produtoFinal e depois pelo id
	Optional<Receita> findByProdutoFinalId(Long produtoId);
}

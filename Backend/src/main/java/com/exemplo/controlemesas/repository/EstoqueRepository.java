// src/main/java/com/exemplo/controlemesas/repository/EstoqueRepository.java
package com.exemplo.controlemesas.repository;

import com.exemplo.controlemesas.model.Estoque;
import com.exemplo.controlemesas.model.Produto;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface EstoqueRepository extends JpaRepository<Estoque, Long> {
	Optional<Estoque> findByProduto(Produto produto);
}
package com.exemplo.controlemesas.repository;

import com.exemplo.controlemesas.model.ReceitaItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ReceitaItemRepository extends JpaRepository<ReceitaItem, Long> {

	boolean existsByProdutoId(Long produtoId);
}

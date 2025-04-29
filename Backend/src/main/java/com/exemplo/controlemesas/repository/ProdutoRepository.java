package com.exemplo.controlemesas.repository;

import com.exemplo.controlemesas.model.Produto;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProdutoRepository extends JpaRepository<Produto, Long> {
}

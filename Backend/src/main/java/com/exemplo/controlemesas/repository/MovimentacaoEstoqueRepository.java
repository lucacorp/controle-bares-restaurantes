package com.exemplo.controlemesas.repository;

import com.exemplo.controlemesas.model.MovimentacaoEstoque;
import com.exemplo.controlemesas.model.Produto;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MovimentacaoEstoqueRepository extends JpaRepository<MovimentacaoEstoque, Long> {
	List<MovimentacaoEstoque> findByProduto(Produto produto);
}

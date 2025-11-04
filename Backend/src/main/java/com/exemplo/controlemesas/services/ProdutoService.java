package com.exemplo.controlemesas.services;

import com.exemplo.controlemesas.model.Produto;
import com.exemplo.controlemesas.repository.ProdutoRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class ProdutoService {

	private final ProdutoRepository produtoRepository;

	public ProdutoService(ProdutoRepository produtoRepository) {
		this.produtoRepository = produtoRepository;
	}

	public Optional<Produto> buscarPorId(Long id) {
		return produtoRepository.findById(id);
	}
}

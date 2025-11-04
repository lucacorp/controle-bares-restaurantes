package com.exemplo.controlemesas.services;

import com.exemplo.controlemesas.model.Estoque;
import com.exemplo.controlemesas.model.Produto;
import com.exemplo.controlemesas.repository.EstoqueRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
public class EstoqueService {

	private final EstoqueRepository estoqueRepository;

	public EstoqueService(EstoqueRepository estoqueRepository) {
		this.estoqueRepository = estoqueRepository;
	}

	/**
	 * Ajusta o saldo do produto.
	 *
	 * @param produto    Produto a ser movimentado (já vindo do banco)
	 * @param quantidade Quantidade movimentada
	 * @param entrada    true = ENTRADA (soma) false = SAÍDA (subtrai – com
	 *                   validação de saldo)
	 */
	@Transactional
	public void ajustarEstoque(Produto produto, BigDecimal quantidade, boolean entrada) {

		// Busca o registro de estoque ou cria um novo zerado
		Estoque estoque = estoqueRepository.findByProduto(produto).orElseGet(() -> {
			Estoque novo = new Estoque();
			novo.setProduto(produto);
			novo.setQuantidade(BigDecimal.ZERO);
			return novo;
		});

		BigDecimal saldoAtual = estoque.getQuantidade();
		BigDecimal novoSaldo = entrada ? saldoAtual.add(quantidade) : saldoAtual.subtract(quantidade);

		if (novoSaldo.compareTo(BigDecimal.ZERO) < 0) {
			throw new IllegalArgumentException("Estoque insuficiente para realizar a saída.");
		}

		estoque.setQuantidade(novoSaldo);
		estoqueRepository.save(estoque);
	}

	/**
	 * Consulta saldo atual de um produto.
	 */
	public BigDecimal consultarSaldo(Produto produto) {
		return estoqueRepository.findByProduto(produto).map(Estoque::getQuantidade).orElse(BigDecimal.ZERO);
	}
}

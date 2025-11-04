package com.exemplo.controlemesas.dto;

import java.math.BigDecimal;

public class EstoqueDTO {
	private Long id;
	private Long produtoId;
	private String produtoNome;
	private BigDecimal saldo;

	public EstoqueDTO(Long id, Long produtoId, String produtoNome, BigDecimal saldo) {
		this.id = id;
		this.produtoId = produtoId;
		this.produtoNome = produtoNome;
		this.saldo = saldo;
	}

	// Getters e setters
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getProdutoId() {
		return produtoId;
	}

	public void setProdutoId(Long produtoId) {
		this.produtoId = produtoId;
	}

	public String getProdutoNome() {
		return produtoNome;
	}

	public void setProdutoNome(String produtoNome) {
		this.produtoNome = produtoNome;
	}

	public BigDecimal getSaldo() {
		return saldo;
	}

	public void setSaldo(BigDecimal saldo) {
		this.saldo = saldo;
	}
}

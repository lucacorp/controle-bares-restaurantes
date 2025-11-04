package com.exemplo.controlemesas.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;

import java.math.BigDecimal;

@Entity
@Table(name = "estoques")
public class Estoque {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@OneToOne
	@JoinColumn(name = "produto_id", unique = true, nullable = false)
	@JsonIgnoreProperties({ "hibernateLazyInitializer", "handler" }) // evita problemas na serialização
	private Produto produto;

	@Column(nullable = false)
	private BigDecimal quantidade = BigDecimal.ZERO;

	// Getters e Setters

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Produto getProduto() {
		return produto;
	}

	public void setProduto(Produto produto) {
		this.produto = produto;
	}

	public BigDecimal getQuantidade() {
		return quantidade;
	}

	public void setQuantidade(BigDecimal quantidade) {
		this.quantidade = quantidade;
	}
}

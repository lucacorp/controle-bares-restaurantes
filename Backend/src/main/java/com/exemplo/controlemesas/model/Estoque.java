package com.exemplo.controlemesas.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.util.Objects;

@Entity
@Table(name = "estoques")
public class Estoque {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "produto_id", unique = true, nullable = false)
	@JsonIgnoreProperties({ "hibernateLazyInitializer", "handler" }) // evita problemas na serialização
	private Produto produto;

	@Column(nullable = false)
	private BigDecimal quantidade = BigDecimal.ZERO;

	@Version
	private Long version;

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

	public Long getVersion() {
		return version;
	}

	public void setVersion(Long version) {
		this.version = version;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof Estoque)) return false;
		Estoque estoque = (Estoque) o;
		return id != null && Objects.equals(id, estoque.id);
	}

	@Override
	public int hashCode() {
		// use id when available to satisfy equals/hashCode contract
		return java.util.Objects.hashCode(id);
	}
}
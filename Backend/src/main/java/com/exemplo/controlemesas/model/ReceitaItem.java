package com.exemplo.controlemesas.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
public class ReceitaItem {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne
	@JoinColumn(name = "produto_id")
	private Produto produto;

	private BigDecimal quantidade;

	@ManyToOne
	@JoinColumn(name = "receita_id")
	@JsonBackReference
	private Receita receita;

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

	public Receita getReceita() {
		return receita;
	}

	public void setReceita(Receita receita) {
		this.receita = receita;
	}
}


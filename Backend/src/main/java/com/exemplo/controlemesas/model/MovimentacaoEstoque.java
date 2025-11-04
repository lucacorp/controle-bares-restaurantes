package com.exemplo.controlemesas.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import jakarta.persistence.*;

@Entity
@Table(name = "movimentacoes_estoque")
public class MovimentacaoEstoque {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne(optional = false)
	@JoinColumn(name = "produto_id")
	private Produto produto;

	@Column(nullable = false, scale = 3, precision = 15)
	private BigDecimal quantidade;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false, length = 10)
	private TipoMovimentacao tipo;

	@Column(name = "data_movimentacao", nullable = false)
	private LocalDateTime dataMovimentacao;

	@Column(length = 255)
	private String observacao;

	/* --- enum --- */
	public enum TipoMovimentacao {
		ENTRADA, SAIDA
	}

	/* --- getters / setters --- */

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

	public TipoMovimentacao getTipo() {
		return tipo;
	}

	public void setTipo(TipoMovimentacao tipo) {
		this.tipo = tipo;
	}

	public LocalDateTime getDataMovimentacao() {
		return dataMovimentacao;
	}

	public void setDataMovimentacao(LocalDateTime dataMovimentacao) {
		this.dataMovimentacao = dataMovimentacao;
	}

	public String getObservacao() {
		return observacao;
	}

	public void setObservacao(String observacao) {
		this.observacao = observacao;
	}
}

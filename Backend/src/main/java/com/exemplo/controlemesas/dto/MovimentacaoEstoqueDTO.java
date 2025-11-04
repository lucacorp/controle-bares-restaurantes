package com.exemplo.controlemesas.dto;

import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

public class MovimentacaoEstoqueDTO {

	@NotNull(message = "O ID do produto é obrigatório")
	private Long produtoId;

	@NotNull(message = "A quantidade é obrigatória")
	@DecimalMin(value = "0.01", inclusive = true, message = "A quantidade deve ser maior que zero")
	private BigDecimal quantidade;

	@NotBlank(message = "O tipo da movimentação é obrigatório (ENTRADA ou SAIDA)")
	private String tipo;

	private String observacao;

	private LocalDateTime dataMovimentacao;

	// Getters e Setters

	public Long getProdutoId() {
		return produtoId;
	}

	public void setProdutoId(Long produtoId) {
		this.produtoId = produtoId;
	}

	public BigDecimal getQuantidade() {
		return quantidade;
	}

	public void setQuantidade(BigDecimal quantidade) {
		this.quantidade = quantidade;
	}

	public String getTipo() {
		return tipo;
	}

	public void setTipo(String tipo) {
		this.tipo = tipo;
	}

	public String getObservacao() {
		return observacao;
	}

	public void setObservacao(String observacao) {
		this.observacao = observacao;
	}

	public LocalDateTime getDataMovimentacao() {
		return dataMovimentacao;
	}

	public void setDataMovimentacao(LocalDateTime dataMovimentacao) {
		this.dataMovimentacao = dataMovimentacao;
	}
}

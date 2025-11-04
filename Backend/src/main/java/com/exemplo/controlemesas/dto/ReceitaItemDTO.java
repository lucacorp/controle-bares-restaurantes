package com.exemplo.controlemesas.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
// Remova esta linha se estiver presente: import java.math.BigDecimal; // <-- REMOVER ESTA IMPORTAÇÃO

public class ReceitaItemDTO {

	@NotNull(message = "O produto é obrigatório.")
	private Long produtoId;

	@NotNull(message = "Quantidade é obrigatória.")
	@Positive(message = "A quantidade deve ser maior que 0.")
	private Double quantidade; // <--- CORRETO: DECLARAÇÃO COMO Double

	public Long getProdutoId() {
		return produtoId;
	}

	public void setProdutoId(Long produtoId) {
		this.produtoId = produtoId;
	}

	public Double getQuantidade() { // <--- CORREÇÃO AQUI: DEVE RETORNAR Double
		return quantidade;
	}

	public void setQuantidade(Double quantidade) { // <--- CORREÇÃO AQUI: DEVE RECEBER Double
		this.quantidade = quantidade;
	}
}
package com.exemplo.controlemesas.dto;

import jakarta.validation.constraints.PositiveOrZero;
import java.math.BigDecimal;

/**
 * Payload enviado pelo front‑end ao clicar “Fechar Comanda”. Valores nulos
 * significam “usar padrão/zero”.
 */
public class FechamentoRequest {

	private String nomeCliente;
	private String observacoes;

	@PositiveOrZero
	private Integer qtdPessoas;

	@PositiveOrZero
	private BigDecimal desconto;

	@PositiveOrZero
	private BigDecimal acrescimo;

	// ===== Getters & Setters =====
	public String getNomeCliente() {
		return nomeCliente;
	}

	public void setNomeCliente(String nomeCliente) {
		this.nomeCliente = nomeCliente;
	}

	public String getObservacoes() {
		return observacoes;
	}

	public void setObservacoes(String observacoes) {
		this.observacoes = observacoes;
	}

	public Integer getQtdPessoas() {
		return qtdPessoas;
	}

	public void setQtdPessoas(Integer qtdPessoas) {
		this.qtdPessoas = qtdPessoas;
	}

	public BigDecimal getDesconto() {
		return desconto;
	}

	public void setDesconto(BigDecimal desconto) {
		this.desconto = desconto;
	}

	public BigDecimal getAcrescimo() {
		return acrescimo;
	}

	public void setAcrescimo(BigDecimal acrescimo) {
		this.acrescimo = acrescimo;
	}
}

// dto/MovimentacaoEstoqueResponseDTO.java
package com.exemplo.controlemesas.dto;

import com.exemplo.controlemesas.model.MovimentacaoEstoque;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class MovimentacaoEstoqueResponseDTO {

	private Long id;
	private Long produtoId; // Adicionei de volta, pois é útil para a resposta
	private String produtoNome; // Adicionei de volta, pois é útil para a resposta
	private BigDecimal quantidade;
	private String tipo;
	private String observacao;
	private LocalDateTime dataMovimentacao; // **** CAMPO CORRETO NO DTO ****

	public MovimentacaoEstoqueResponseDTO(MovimentacaoEstoque mov) {
		this.id = mov.getId();
		this.produtoId = mov.getProduto() != null ? mov.getProduto().getId() : null; // Verificação de nulo
		this.produtoNome = mov.getProduto() != null ? mov.getProduto().getNome() : "Produto Desconhecido"; // Verificação
																											// de nulo
		this.quantidade = mov.getQuantidade();
		this.tipo = mov.getTipo().name();
		this.observacao = mov.getObservacao();
		this.dataMovimentacao = mov.getDataMovimentacao(); // **** CORREÇÃO AQUI ****
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

	// **** GETTER E SETTER PADRONIZADOS COM dataMovimentacao ****
	public LocalDateTime getDataMovimentacao() {
		return dataMovimentacao;
	}

	public void setDataMovimentacao(LocalDateTime dataMovimentacao) {
		this.dataMovimentacao = dataMovimentacao;
	}
}
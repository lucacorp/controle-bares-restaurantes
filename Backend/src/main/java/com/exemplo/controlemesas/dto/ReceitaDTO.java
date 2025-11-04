package com.exemplo.controlemesas.dto;

import jakarta.validation.constraints.*; // Importação das anotações de validação
// Remova esta linha se estiver presente: import java.math.BigDecimal; // <-- REMOVER ESTA IMPORTAÇÃO

import java.util.List;

public class ReceitaDTO {
	private Long id;

	@NotBlank(message = "O nome da receita é obrigatório.")
	private String nome;

	@NotNull(message = "O adicional é obrigatório.")
	@Min(value = 0L, message = "O adicional deve ser no mínimo 0.") // Note: 0L para long se value for long
	private Double adicional; // <--- CORRETO: DECLARAÇÃO COMO Double

	@NotNull(message = "Produto final é obrigatório.")
	private Long produtoFinalId;

	@NotEmpty(message = "A receita deve conter pelo menos um item.")
	private List<ReceitaItemDTO> itens;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	public Double getAdicional() { // <--- CORREÇÃO AQUI: DEVE RETORNAR Double
		return adicional;
	}

	public void setAdicional(Double adicional) { // <--- CORREÇÃO AQUI: DEVE RECEBER Double
		this.adicional = adicional;
	}

	public Long getProdutoFinalId() {
		return produtoFinalId;
	}

	public void setProdutoFinalId(Long produtoFinalId) {
		this.produtoFinalId = produtoFinalId;
	}

	public List<ReceitaItemDTO> getItens() {
		return itens;
	}

	public void setItens(List<ReceitaItemDTO> itens) {
		this.itens = itens;
	}
}
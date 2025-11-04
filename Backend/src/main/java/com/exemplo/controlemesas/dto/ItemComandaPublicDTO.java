package com.exemplo.controlemesas.dto;

import java.math.BigDecimal;

public class ItemComandaPublicDTO {
    private Long comandaId;
    private Long produtoId;
    private String descricao;
    private String responsavel;
    private int quantidade;
    private BigDecimal precoUnitario;

    // Getters e Setters
    public Long getComandaId() { return comandaId; }
    public void setComandaId(Long comandaId) { this.comandaId = comandaId; }

    public Long getProdutoId() { return produtoId; }
    public void setProdutoId(Long produtoId) { this.produtoId = produtoId; }

    public String getDescricao() { return descricao; }
    public void setDescricao(String descricao) { this.descricao = descricao; }

    public String getResponsavel() { return responsavel; }
    public void setResponsavel(String responsavel) { this.responsavel = responsavel; }

    public int getQuantidade() { return quantidade; }
    public void setQuantidade(int quantidade) { this.quantidade = quantidade; }

    public BigDecimal getPrecoUnitario() { return precoUnitario; }
    public void setPrecoUnitario(BigDecimal precoUnitario) { this.precoUnitario = precoUnitario; }
}
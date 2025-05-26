package com.exemplo.controlemesas.dto;

public class ItemComandaDTO {
    private Long id;  // Também é útil retornar o ID do item para remoção
    private Long comandaId;
    private Long produtoId;
    private String produtoDescricao;  // ✅ NOVO campo para facilitar exibição
    private int quantidade;
    private double precoUnitario;

    // Getters e Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getComandaId() { return comandaId; }
    public void setComandaId(Long comandaId) { this.comandaId = comandaId; }

    public Long getProdutoId() { return produtoId; }
    public void setProdutoId(Long produtoId) { this.produtoId = produtoId; }

    public String getProdutoDescricao() { return produtoDescricao; }
    public void setProdutoDescricao(String produtoDescricao) { this.produtoDescricao = produtoDescricao; }

    public int getQuantidade() { return quantidade; }
    public void setQuantidade(int quantidade) { this.quantidade = quantidade; }

    public double getPrecoUnitario() { return precoUnitario; }
    public void setPrecoUnitario(double precoUnitario) { this.precoUnitario = precoUnitario; }
}

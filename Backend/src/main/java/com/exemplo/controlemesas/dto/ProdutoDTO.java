package com.exemplo.controlemesas.dto;

import com.exemplo.controlemesas.model.Produto;

import java.math.BigDecimal;

public class ProdutoDTO {

    private Long id;
    private String codigoBarras;
    private String nome;
    private String grupo;
    private String unidade;
    private BigDecimal precoVenda;
    private String categoria;
    private String descricao;

    // Construtor a partir da entidade Produto
    public ProdutoDTO(Produto produto) {
        this.id = produto.getId();
        this.codigoBarras = produto.getCodigoBarras();
        this.nome = produto.getNome();
        this.grupo = produto.getGrupo();
        this.unidade = produto.getUnidade();
        this.precoVenda = produto.getPrecoVenda();
        this.categoria = produto.getCategoria();
        this.descricao = produto.getDescricao();
    }

    // Getters e Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getCodigoBarras() { return codigoBarras; }
    public void setCodigoBarras(String codigoBarras) { this.codigoBarras = codigoBarras; }

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    public String getGrupo() { return grupo; }
    public void setGrupo(String grupo) { this.grupo = grupo; }

    public String getUnidade() { return unidade; }
    public void setUnidade(String unidade) { this.unidade = unidade; }

    public BigDecimal getPrecoVenda() { return precoVenda; }
    public void setPrecoVenda(BigDecimal precoVenda) { this.precoVenda = precoVenda; }

    public String getCategoria() { return categoria; }
    public void setCategoria(String categoria) { this.categoria = categoria; }

    public String getDescricao() { return descricao; }
    public void setDescricao(String descricao) { this.descricao = descricao; }
}

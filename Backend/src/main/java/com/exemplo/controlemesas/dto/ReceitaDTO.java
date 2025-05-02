package com.exemplo.controlemesas.dto;

import java.math.BigDecimal;
import java.util.List;

public class ReceitaDTO {
    private Long id;
    private String nome;
    private BigDecimal adicional;
    private Long produtoFinalId;
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

    public BigDecimal getAdicional() {
        return adicional;
    }

    public void setAdicional(BigDecimal adicional) {
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

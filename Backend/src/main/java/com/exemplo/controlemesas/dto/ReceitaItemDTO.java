package com.exemplo.controlemesas.dto;

import java.math.BigDecimal;

public class ReceitaItemDTO {
    private Long produtoId;
    private BigDecimal quantidade;

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
}

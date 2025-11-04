package com.exemplo.controlemesas.dto;

import java.math.BigDecimal;

public class ItemPreparoDTO {

    private Long itemId;
    private String produtoNome;
    private BigDecimal quantidade;
    private String status;
    private String mesa;

    public ItemPreparoDTO() {
    }

    public ItemPreparoDTO(Long itemId, String produtoNome, BigDecimal quantidade, String status, String mesa) {
        this.itemId = itemId;
        this.produtoNome = produtoNome;
        this.quantidade = quantidade;
        this.status = status;
        this.mesa = mesa;
    }

    public Long getItemId() {
        return itemId;
    }

    public void setItemId(Long itemId) {
        this.itemId = itemId;
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

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getMesa() {
        return mesa;
    }

    public void setMesa(String mesa) {
        this.mesa = mesa;
    }
}

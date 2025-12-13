package com.exemplo.controlemesas.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.Map;

public class ItemComandaRequestDTO {

    @NotNull
    private Long comandaId;

    @NotNull
    private Long produtoId;

    @NotNull
    @Min(1)
    private Integer quantidade;

    private String responsavel;

    public ItemComandaRequestDTO() {
    }

    public Long getComandaId() {
        return comandaId;
    }

    public void setComandaId(Long comandaId) {
        this.comandaId = comandaId;
    }

    // Support frontend sending nested object: { "comanda": { "id": 64 } }
    @JsonProperty("comanda")
    public void unpackComanda(Map<String, Object> comanda) {
        if (comanda != null && comanda.get("id") != null) {
            Object id = comanda.get("id");
            if (id instanceof Number) this.comandaId = ((Number) id).longValue();
            else this.comandaId = Long.valueOf(id.toString());
        }
    }

    public Long getProdutoId() {
        return produtoId;
    }

    public void setProdutoId(Long produtoId) {
        this.produtoId = produtoId;
    }

    // Support frontend sending nested object: { "produto": { "id": 6 } }
    @JsonProperty("produto")
    public void unpackProduto(Map<String, Object> produto) {
        if (produto != null && produto.get("id") != null) {
            Object id = produto.get("id");
            if (id instanceof Number) this.produtoId = ((Number) id).longValue();
            else this.produtoId = Long.valueOf(id.toString());
        }
    }

    public Integer getQuantidade() {
        return quantidade;
    }

    public void setQuantidade(Integer quantidade) {
        this.quantidade = quantidade;
    }

    public String getResponsavel() {
        return responsavel;
    }

    public void setResponsavel(String responsavel) {
        this.responsavel = responsavel;
    }
}
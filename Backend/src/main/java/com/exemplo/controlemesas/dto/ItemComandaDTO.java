package com.exemplo.controlemesas.dto;

import com.exemplo.controlemesas.model.ItemComanda;
import com.exemplo.controlemesas.model.enums.StatusItemComanda;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class ItemComandaDTO {

    private Long id;
    private Long produtoId;
    private String produtoNome;
    private int quantidade;
    private BigDecimal precoVenda;
    private BigDecimal total;
    private StatusItemComanda status;
    private String responsavel;
    private LocalDateTime dataRegistro;

    public ItemComandaDTO() {}

    // 🔹 Construtor que faltava
    public ItemComandaDTO(ItemComanda item) {
        this.id = item.getId();
        this.produtoId = item.getProduto().getId();
        this.produtoNome = item.getProduto().getNome();
        this.quantidade = item.getQuantidade();
        this.precoVenda = item.getPrecoVenda();
        this.total = item.getTotal();
        this.status = item.getStatus();
        this.responsavel = item.getResponsavel();
        this.dataRegistro = item.getDataRegistro();
    }

    // ===== Getters e Setters =====
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getProdutoId() { return produtoId; }
    public void setProdutoId(Long produtoId) { this.produtoId = produtoId; }

    public String getProdutoNome() { return produtoNome; }
    public void setProdutoNome(String produtoNome) { this.produtoNome = produtoNome; }

    public int getQuantidade() { return quantidade; }
    public void setQuantidade(int quantidade) { this.quantidade = quantidade; }

    public BigDecimal getPrecoVenda() { return precoVenda; }
    public void setPrecoVenda(BigDecimal precoVenda) { this.precoVenda = precoVenda; }

    public BigDecimal getTotal() { return total; }
    public void setTotal(BigDecimal total) { this.total = total; }

    public StatusItemComanda getStatus() { return status; }
    public void setStatus(StatusItemComanda status) { this.status = status; }

    public String getResponsavel() { return responsavel; }
    public void setResponsavel(String responsavel) { this.responsavel = responsavel; }

    public LocalDateTime getDataRegistro() { return dataRegistro; }
    public void setDataRegistro(LocalDateTime dataRegistro) { this.dataRegistro = dataRegistro; }

}
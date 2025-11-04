package com.exemplo.controlemesas.model;

import com.exemplo.controlemesas.model.enums.StatusItemComanda;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import com.fasterxml.jackson.annotation.JsonIgnore;


@Entity
@Table(name = "itens_comanda")
public class ItemComanda {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER) // ✅ Corrija aqui
    @JoinColumn(name = "produto_id")
    private Produto produto;
    
    @Column(name = "fabricacao_propria")
    private boolean fabricacaoPropria;

    @Column(name = "preco_unitario", nullable = false)
    private BigDecimal precoVenda;

    private Integer quantidade;

    private BigDecimal total;

    private String responsavel;

    private LocalDateTime dataRegistro;

    @Enumerated(EnumType.STRING)
    private StatusItemComanda status = StatusItemComanda.PENDENTE;

   
    @ManyToOne
    @JoinColumn(name = "comanda_id")
    @JsonIgnore
    private Comanda comanda;

    // Construtor
    public ItemComanda() {
        this.dataRegistro = LocalDateTime.now();
    }

    // Lógica
    public void calcularTotal() {
        if (precoVenda != null && quantidade != null) {
            this.total = precoVenda.multiply(BigDecimal.valueOf(quantidade));
        } else {
            this.total = BigDecimal.ZERO;
        }
    }

    // Getters e Setters
    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }

    public Produto getProduto() {
        return produto;
    }
    public void setProduto(Produto produto) {
        this.produto = produto;
    }

    public BigDecimal getPrecoVenda() {
        return precoVenda;
    }
    public void setPrecoVenda(BigDecimal precoVenda) {
        this.precoVenda = precoVenda;
        calcularTotal();
    }

    public Integer getQuantidade() {
        return quantidade;
    }
    public void setQuantidade(Integer quantidade) {
        this.quantidade = quantidade;
        calcularTotal();
    }

    public BigDecimal getTotal() {
        return total;
    }
    public void setTotal(BigDecimal total) {
        this.total = total;
    }

    public String getResponsavel() {
        return responsavel;
    }
    public void setResponsavel(String responsavel) {
        this.responsavel = responsavel;
    }

    public LocalDateTime getDataRegistro() {
        return dataRegistro;
    }
    public void setDataRegistro(LocalDateTime dataRegistro) {
        this.dataRegistro = dataRegistro;
    }

    public StatusItemComanda getStatus() {
        return status;
    }
    public void setStatus(StatusItemComanda status) {
        this.status = status;
    }

    public Comanda getComanda() {
        return comanda;
    }
    public void setComanda(Comanda comanda) {
        this.comanda = comanda;
    }
    public boolean isFabricacaoPropria() {
        return fabricacaoPropria;
    }

    public void setFabricacaoPropria(boolean fabricacaoPropria) {
        this.fabricacaoPropria = fabricacaoPropria;
    }
}
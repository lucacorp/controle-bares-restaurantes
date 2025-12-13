package com.exemplo.controlemesas.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.math.RoundingMode;

@Entity
@Table(name = "item_comanda_resumo")
public class ItemComandaResumo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "comanda_resumo_id", nullable = false)
    @com.fasterxml.jackson.annotation.JsonIgnore
    private ComandaResumo comandaResumo;

    private Integer itemNo; // posição no cupom

    private Long produtoId; // opcional: FK do produto

    private String descricao;

    private String unMedida; // UN, KG, LT...

    private BigDecimal quantidade;

    private BigDecimal precoUnitario;

    private BigDecimal subtotal;

    // ▾ Campos fiscais resolvidos
    private String cfop;
    private String cst;
    private String origem;
    private String ncm;
    private BigDecimal aliqIcms;
    private BigDecimal aliqPis;
    private BigDecimal aliqCofins;
    private BigDecimal valorIcms;
    private BigDecimal valorPis;
    private BigDecimal valorCofins;

    public ItemComandaResumo() {
        // Construtor padrão necessário pelo JPA
    }

    // ✅ CORREÇÃO: Construtor que aceita um objeto ItemComanda, mapeando corretamente os campos
    public ItemComandaResumo(ItemComanda item) {
        this.produtoId = item.getProduto() != null ? item.getProduto().getId() : null;
        this.descricao = item.getProduto() != null && item.getProduto().getNome() != null ? item.getProduto().getNome() : "PRODUTO";
        this.unMedida = item.getProduto() != null && item.getProduto().getUnidade() != null ? item.getProduto().getUnidade() : "UN";

        // Converte a quantidade de Integer para BigDecimal com fallback 1
        int qt = item.getQuantidade() != null ? item.getQuantidade() : 1;
        this.quantidade = BigDecimal.valueOf(qt);

        // Preço unitário: preferencialmente do item, senão do produto, senão zero
        if (item.getPrecoVenda() != null) {
            this.precoUnitario = item.getPrecoVenda();
        } else if (item.getProduto() != null && item.getProduto().getPrecoVenda() != null) {
            this.precoUnitario = item.getProduto().getPrecoVenda();
        } else {
            this.precoUnitario = BigDecimal.ZERO;
        }

        // Subtotal: assegura que existe um valor coerente
        if (item.getTotal() != null) {
            this.subtotal = item.getTotal();
        } else {
            this.subtotal = this.precoUnitario.multiply(this.quantidade);
        }

        // ====== Campos fiscais: preenche a partir do produto se existir, caso contrário usa defaults ======
        if (item.getProduto() != null) {
            Produto p = item.getProduto();
            this.cfop = p.getCfop() != null && p.getCfop().getCodigo() != null ? p.getCfop().getCodigo() : "5102";
            this.cst = p.getCst() != null && p.getCst().getCodigo() != null ? p.getCst().getCodigo() : "102";
            this.origem = p.getOrigem() != null && p.getOrigem().getCodigo() != null ? p.getOrigem().getCodigo() : "0";

            // Produto não tem NCM no modelo atual — mantém nulo ou default para validação
            this.ncm = "00000000";

            // Alíquotas ICMS podem estar em Produto (Double). Converte para BigDecimal com escala.
            if (p.getAliquotaIcms() != null) {
                this.aliqIcms = BigDecimal.valueOf(p.getAliquotaIcms()).setScale(2, RoundingMode.HALF_UP);
            } else {
                this.aliqIcms = BigDecimal.ZERO;
            }

            // PIS/COFINS não estão no Produto por padrão — usar 0.00 como fallback
            this.aliqPis = BigDecimal.ZERO;
            this.aliqCofins = BigDecimal.ZERO;

        } else {
            // Sem produto: usa defaults simples para permitir validação
            this.cfop = "5102";
            this.cst = "102";
            this.origem = "0";
            this.ncm = "00000000";
            this.aliqIcms = BigDecimal.ZERO;
            this.aliqPis = BigDecimal.ZERO;
            this.aliqCofins = BigDecimal.ZERO;
        }

        // Calcula valores dos tributos com base no subtotal e nas alíquotas (dividindo por 100)
        this.valorIcms = this.subtotal.multiply(this.aliqIcms).divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
        this.valorPis = this.subtotal.multiply(this.aliqPis).divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
        this.valorCofins = this.subtotal.multiply(this.aliqCofins).divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
    }

    /* =================== Getters & Setters =================== */
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public ComandaResumo getComandaResumo() {
        return comandaResumo;
    }

    public void setComandaResumo(ComandaResumo comandaResumo) {
        this.comandaResumo = comandaResumo;
    }

    public Integer getItemNo() {
        return itemNo;
    }

    public void setItemNo(Integer itemNo) {
        this.itemNo = itemNo;
    }

    public Long getProdutoId() {
        return produtoId;
    }

    public void setProdutoId(Long produtoId) {
        this.produtoId = produtoId;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public String getUnMedida() {
        return unMedida;
    }

    public void setUnMedida(String unMedida) {
        this.unMedida = unMedida;
    }

    public BigDecimal getQuantidade() {
        return quantidade;
    }

    public void setQuantidade(BigDecimal quantidade) {
        this.quantidade = quantidade;
    }

    public BigDecimal getPrecoUnitario() {
        return precoUnitario;
    }

    public void setPrecoUnitario(BigDecimal precoUnitario) {
        this.precoUnitario = precoUnitario;
    }

    public BigDecimal getSubtotal() {
        return subtotal;
    }

    public void setSubtotal(BigDecimal subtotal) {
        this.subtotal = subtotal;
    }

    public String getCfop() {
        return cfop;
    }

    public void setCfop(String cfop) {
        this.cfop = cfop;
    }

    public String getCst() {
        return cst;
    }

    public void setCst(String cst) {
        this.cst = cst;
    }

    public String getOrigem() {
        return origem;
    }

    public void setOrigem(String origem) {
        this.origem = origem;
    }

    public String getNcm() {
        return ncm;
    }

    public void setNcm(String ncm) {
        this.ncm = ncm;
    }

    public BigDecimal getAliqIcms() {
        return aliqIcms;
    }

    public void setAliqIcms(BigDecimal aliqIcms) {
        this.aliqIcms = aliqIcms;
    }

    public BigDecimal getAliqPis() {
        return aliqPis;
    }

    public void setAliqPis(BigDecimal aliqPis) {
        this.aliqPis = aliqPis;
    }

    public BigDecimal getAliqCofins() {
        return aliqCofins;
    }

    public void setAliqCofins(BigDecimal aliqCofins) {
        this.aliqCofins = aliqCofins;
    }

    public BigDecimal getValorIcms() {
        return valorIcms;
    }

    public void setValorIcms(BigDecimal valorIcms) {
        this.valorIcms = valorIcms;
    }

    public BigDecimal getValorPis() {
        return valorPis;
    }

    public void setValorPis(BigDecimal valorPis) {
        this.valorPis = valorPis;
    }

    public BigDecimal getValorCofins() {
        return valorCofins;
    }

    public void setValorCofins(BigDecimal valorCofins) {
        this.valorCofins = valorCofins;
    }
}
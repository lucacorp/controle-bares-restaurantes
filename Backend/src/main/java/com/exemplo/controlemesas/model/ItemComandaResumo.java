package com.exemplo.controlemesas.model;

import jakarta.persistence.*;
import java.math.BigDecimal;

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
        this.produtoId = item.getProduto().getId();
        this.descricao = item.getProduto().getNome();
        this.unMedida = "UN"; // Exemplo, ajuste conforme a sua lógica
        
        // CORREÇÃO: Converte a quantidade de Integer para BigDecimal
        this.quantidade = new BigDecimal(item.getQuantidade());
        
        // CORREÇÃO: Usa o método getPrecoVenda() que existe na classe ItemComanda
        this.precoUnitario = item.getPrecoVenda();
        this.subtotal = item.getTotal();
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
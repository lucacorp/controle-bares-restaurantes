package com.exemplo.controlemesas.model;

import jakarta.persistence.*;

@Entity
@Table(name = "produtos")
public class Produto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String codigoBarras;
    private String nome;
    private String grupo;
    private String unidade;

    @Column(name = "preco_venda")
    private Double precoVenda;

    private String categoria;
    private String descricao;
    private Double preco;

    // Dados fiscais
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cfop_id")
    private CFOP cfop;  // Relacionamento com a tabela CFOP

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cst_id")
    private CST cst;  // Relacionamento com a tabela CST

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "origem_id")
    private Origem origem;  // Relacionamento com a tabela Origem

    private Double aliquotaIcms;
    private Double aliquotaIpi;

    // Getters e Setters

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCodigo() {
        return codigoBarras;
    }

    public void setCodigo(String codigoBarras) {
        this.codigoBarras = codigoBarras;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getGrupo() {
        return grupo;
    }

    public void setGrupo(String grupo) {
        this.grupo = grupo;
    }

    public String getUnidade() {
        return unidade;
    }

    public void setUnidade(String unidade) {
        this.unidade = unidade;
    }

    public Double getPrecoVenda() {
        return precoVenda;
    }

    public void setPrecoVenda(Double precoVenda) {
        this.precoVenda = precoVenda;
    }

    public String getCategoria() {
        return categoria;
    }

    public void setCategoria(String categoria) {
        this.categoria = categoria;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public Double getPreco() {
        return preco;
    }

    public void setPreco(Double preco) {
        this.preco = preco;
    }

    // Dados fiscais
    public CFOP getCfop() {
        return cfop;
    }

    public void setCfop(CFOP cfop) {
        this.cfop = cfop;
    }

    public CST getCst() {
        return cst;
    }

    public void setCst(CST cst) {
        this.cst = cst;
    }

    public Origem getOrigem() {
        return origem;
    }

    public void setOrigem(Origem origem) {
        this.origem = origem;
    }

    public Double getAliquotaIcms() {
        return aliquotaIcms;
    }

    public void setAliquotaIcms(Double aliquotaIcms) {
        this.aliquotaIcms = aliquotaIcms;
    }

    public Double getAliquotaIpi() {
        return aliquotaIpi;
    }

    public void setAliquotaIpi(Double aliquotaIpi) {
        this.aliquotaIpi = aliquotaIpi;
    }
}

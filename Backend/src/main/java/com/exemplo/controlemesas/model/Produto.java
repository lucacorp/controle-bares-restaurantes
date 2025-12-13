package com.exemplo.controlemesas.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.util.Objects;

@Entity
@Table(name = "produtos")
public class Produto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "fabricacao_propria")
    private boolean fabricacaoPropria;
    private String codigoBarras;
    private String nome;
    private String grupo;
    private String unidade;

    @Column(name = "preco_venda", precision = 10, scale = 2)
    private BigDecimal precoVenda;

    private String categoria;
    private String descricao;

    @Column(precision = 10, scale = 2)
    private BigDecimal preco;

    // Dados fiscais
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cfop_id")
    private CFOP cfop;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cst_id")
    private CST cst;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "origem_id")
    private Origem origem;

    private Double aliquotaIcms;
    private Double aliquotaIpi;

    @Version
    private Long version;

    // ===== Getters e Setters =====
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

    public BigDecimal getPreco() { return preco; }
    public void setPreco(BigDecimal preco) { this.preco = preco; }

    public CFOP getCfop() { return cfop; }
    public void setCfop(CFOP cfop) { this.cfop = cfop; }

    public CST getCst() { return cst; }
    public void setCst(CST cst) { this.cst = cst; }

    public Origem getOrigem() { return origem; }
    public void setOrigem(Origem origem) { this.origem = origem; }

    public Double getAliquotaIcms() { return aliquotaIcms; }
    public void setAliquotaIcms(Double aliquotaIcms) { this.aliquotaIcms = aliquotaIcms; }

    public Double getAliquotaIpi() { return aliquotaIpi; }
    public void setAliquotaIpi(Double aliquotaIpi) { this.aliquotaIpi = aliquotaIpi; }
    public boolean isFabricacaoPropria() {
        return fabricacaoPropria;
    }

    public void setFabricacaoPropria(boolean fabricacaoPropria) {
        this.fabricacaoPropria = fabricacaoPropria;
    }

    public Long getVersion() { return version; }
    public void setVersion(Long version) { this.version = version; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Produto)) return false;
        Produto produto = (Produto) o;
        return id != null && Objects.equals(id, produto.id);
    }

    @Override
    public int hashCode() {
        // use id when available to satisfy equals/hashCode contract
        return Objects.hashCode(id);
    }
}
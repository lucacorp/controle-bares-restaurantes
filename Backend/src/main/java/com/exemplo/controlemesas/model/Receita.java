package com.exemplo.controlemesas.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Entity
public class Receita {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nome;

    private BigDecimal adicional;

    @ManyToOne
    @JoinColumn(name = "produto_final_id")
    private Produto produtoFinal;

    @OneToMany(mappedBy = "receita", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ReceitaItem> itens = new ArrayList<>();

    // Getters e Setters
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

    public Produto getProdutoFinal() {
        return produtoFinal;
    }

    public void setProdutoFinal(Produto produtoFinal) {
        this.produtoFinal = produtoFinal;
    }

    public List<ReceitaItem> getItens() {
        return itens;
    }

    public void setItens(List<ReceitaItem> itens) {
        this.itens = itens;
    }
}

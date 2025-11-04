package com.exemplo.controlemesas.model;

import com.exemplo.controlemesas.model.enums.StatusComanda;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "Comandas")
public class Comanda {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "mesa_id", nullable = false)
    private Mesa mesa;

    private Integer numeroMesa;
    private LocalDateTime dataAbertura;
    private LocalDateTime dataFechamento;
    private boolean ativa = true;

    // 🔹 CAMPOS ADICIONADOS
    @Enumerated(EnumType.STRING)
    private StatusComanda status; 

    @OneToMany(mappedBy = "comanda", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    private List<ItemComanda> itens = new ArrayList<>();

    // ===== Construtores =====
    public Comanda() {
        this.dataAbertura = LocalDateTime.now();
        this.ativa = true;
    }

    public Comanda(Integer numeroMesa) {
        this.numeroMesa = numeroMesa;
        this.dataAbertura = LocalDateTime.now();
        this.ativa = true;
    }
    
    // ===== MÉTODOS ADICIONADOS =====
    public StatusComanda getStatus() {
        return status;
    }

    public void setStatus(StatusComanda status) {
        this.status = status;
    }

    // ===== Métodos utilitários =====
    public void adicionarItem(ItemComanda item) {
        itens.add(item);
        item.setComanda(this);
    }

    public void removerItem(ItemComanda item) {
        itens.remove(item);
        item.setComanda(null);
    }

    // ===== Getters e Setters =====
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
    
    public Mesa getMesa() {
        return mesa;
    }

    public void setMesa(Mesa mesa) {
        this.mesa = mesa;
    }

    public Integer getNumeroMesa() {
        return numeroMesa;
    }

    public void setNumeroMesa(Integer numeroMesa) {
        this.numeroMesa = numeroMesa;
    }

    public LocalDateTime getDataAbertura() {
        return dataAbertura;
    }

    public void setDataAbertura(LocalDateTime dataAbertura) {
        this.dataAbertura = dataAbertura;
    }

    public LocalDateTime getDataFechamento() {
        return dataFechamento;
    }

    public void setDataFechamento(LocalDateTime dataFechamento) {
        this.dataFechamento = dataFechamento;
    }

    public boolean isAtiva() {
        return ativa;
    }

    public void setAtiva(boolean ativa) {
        this.ativa = ativa;
    }

    public List<ItemComanda> getItens() {
        return itens;
    }

    public void setItens(List<ItemComanda> itens) {
        this.itens = itens;
    }
}
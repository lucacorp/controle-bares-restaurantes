package com.exemplo.controlemesas.model;

import jakarta.validation.constraints.NotNull;
import com.exemplo.controlemesas.model.enums.StatusMesa;
import jakarta.persistence.*;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
@Entity
@Table(name = "mesas")
public class Mesa {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String descricao;
    
    private String nome;

    private Integer numero; // changed from Long to Integer

    private boolean ocupada;

    @NotNull
    @Enumerated(EnumType.STRING)
    private StatusMesa status;

    @Version
    private Long version;

    // --- Getters e Setters CORRETOS para 'id' ---
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    // --- Getters e Setters para 'descricao' ---
    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    // --- Getters e Setters para 'numero' ---
    public Integer getNumero() { // changed type
        return numero;
    }

    public void setNumero(Integer numero) { // changed type
        this.numero = numero;
    }

    // --- Getters e Setters para 'ocupada' ---
    public boolean isOcupada() {
        return ocupada;
    }

    public void setOcupada(boolean ocupada) {
        this.ocupada = ocupada;
    }

    // --- Getters e Setters para 'status' ---
    public StatusMesa getStatus() {
        return status;
    }

    public void setStatus(StatusMesa status) {
        this.status = status;
    }
    
    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public Long getVersion() {
        return version;
    }

    public void setVersion(Long version) {
        this.version = version;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Mesa)) return false;
        Mesa mesa = (Mesa) o;
        return id != null && Objects.equals(id, mesa.id);
    }

    @Override
    public int hashCode() {
        // use id when available to satisfy equals/hashCode contract
        return Objects.hashCode(id);
    }
}
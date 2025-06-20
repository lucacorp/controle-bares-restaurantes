package com.exemplo.controlemesas.model;
import jakarta.validation.constraints.NotNull;

import jakarta.persistence.*;

@Entity
@Table(name = "mesas")
public class Mesa {

	
    public enum StatusMesa {
        LIVRE,
        OCUPADA,
        FECHADA
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String descricao;

    private boolean ocupada;
   
    @NotNull
    @Enumerated(EnumType.STRING)
    private StatusMesa status;

    // Getters e Setters

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public boolean isOcupada() {
        return ocupada;
    }

    public void setOcupada(boolean ocupada) {
        this.ocupada = ocupada;
    }

    public StatusMesa getStatus() {
        return status;
    }

    public void setStatus(StatusMesa status) {
        this.status = status;
    }
}

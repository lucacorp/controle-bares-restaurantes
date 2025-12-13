package com.exemplo.controlemesas.dto;

import com.exemplo.controlemesas.model.enums.StatusMesa;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Min;

public class MesaDTO {

    private Long id;
    private String descricao;
    private String nome;

    @Min(1)
    private Integer numero;

    private boolean ocupada;

    @NotNull
    private StatusMesa status;

    public MesaDTO() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getDescricao() { return descricao; }
    public void setDescricao(String descricao) { this.descricao = descricao; }

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    public Integer getNumero() { return numero; }
    public void setNumero(Integer numero) { this.numero = numero; }

    public boolean isOcupada() { return ocupada; }
    public void setOcupada(boolean ocupada) { this.ocupada = ocupada; }

    public StatusMesa getStatus() { return status; }
    public void setStatus(StatusMesa status) { this.status = status; }
}
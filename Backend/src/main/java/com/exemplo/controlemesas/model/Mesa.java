package com.exemplo.controlemesas.model;

import jakarta.validation.constraints.NotNull;
import com.exemplo.controlemesas.model.enums.StatusMesa;
import jakarta.persistence.*;

@Entity
@Table(name = "mesas")
public class Mesa {

	

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	private String descricao;
	
	private String nome;

	private Long numero; // Campo 'numero'

	private boolean ocupada;

	@NotNull
	@Enumerated(EnumType.STRING)
	private StatusMesa status;

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
	public Long getNumero() { // Método para obter o número
		return numero;
	}

	public void setNumero(Long numero) { // Método para definir o número
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
}
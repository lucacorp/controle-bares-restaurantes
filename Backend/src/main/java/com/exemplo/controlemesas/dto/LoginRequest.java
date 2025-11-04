package com.exemplo.controlemesas.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public class LoginRequest {

	@NotBlank(message = "O e-mail é obrigatório")
	@Email(message = "Formato de e-mail inválido")
	private String email;

	@NotBlank(message = "A senha é obrigatória")
	private String senha;

	// Getters e setters
	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getSenha() {
		return senha;
	}

	public void setSenha(String senha) {
		this.senha = senha;
	}
}

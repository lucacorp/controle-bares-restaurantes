package com.exemplo.controlemesas.controller;

import com.exemplo.controlemesas.dto.LoginRequest;
import com.exemplo.controlemesas.dto.RegistroRequest;
import com.exemplo.controlemesas.model.Usuario;
import com.exemplo.controlemesas.repository.UsuarioRepository;
import com.exemplo.controlemesas.security.JwtUtil;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.AuthenticationException;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*")
public class AuthController {

	@Autowired
	private UsuarioRepository usuarioRepository;

	@Autowired
	private PasswordEncoder passwordEncoder;

	@Autowired
	private AuthenticationManager authenticationManager;

	@Autowired
	private JwtUtil jwtUtil;

	// Registro de novo usuário
	@PostMapping("/register")
	public ResponseEntity<Map<String, String>> register(@Valid @RequestBody RegistroRequest registroRequest) {
		Map<String, String> response = new HashMap<>();

		if (usuarioRepository.existsByEmail(registroRequest.getEmail())) {
			response.put("mensagem", "E-mail já está em uso.");
			return ResponseEntity.badRequest().body(response);
		}

		Usuario novoUsuario = new Usuario();
		novoUsuario.setNome(registroRequest.getNome());
		novoUsuario.setEmail(registroRequest.getEmail());
		novoUsuario.setSenha(passwordEncoder.encode(registroRequest.getSenha()));

		usuarioRepository.save(novoUsuario);

		response.put("mensagem", "Usuário registrado com sucesso.");
		return ResponseEntity.ok(response);
	}

	// Login + geração de token
	@PostMapping("/login")
	public ResponseEntity<?> login(@Valid @RequestBody LoginRequest loginRequest) {
		try {
			Authentication auth = authenticationManager.authenticate(
					new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getSenha()));

			if (auth.isAuthenticated()) {
				String token = jwtUtil.generateToken(loginRequest.getEmail());

				Map<String, String> response = new HashMap<>();
				response.put("token", token);
				return ResponseEntity.ok(response);
			}
		} catch (AuthenticationException e) {
			Map<String, String> response = new HashMap<>();
			response.put("mensagem", "Login ou senha inválidos.");
			return ResponseEntity.status(401).body(response);
		}

		Map<String, String> response = new HashMap<>();
		response.put("mensagem", "Erro desconhecido.");
		return ResponseEntity.status(500).body(response);
	}
}

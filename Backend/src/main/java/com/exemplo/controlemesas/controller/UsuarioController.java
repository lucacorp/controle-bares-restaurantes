package com.exemplo.controlemesas.controller;

import com.exemplo.controlemesas.dto.LoginRequest;
import com.exemplo.controlemesas.dto.RegistroRequest;
import com.exemplo.controlemesas.model.Usuario;
import com.exemplo.controlemesas.repository.UsuarioRepository;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/usuarios")
public class UsuarioController {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @PostMapping("/login")
    public ResponseEntity<Usuario> login(@Valid @RequestBody LoginRequest loginRequest) {
        Optional<Usuario> usuarioEncontrado = usuarioRepository.findByEmailAndSenha(loginRequest.getEmail(), loginRequest.getSenha());
        return usuarioEncontrado
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.status(401).build()); // 401 Unauthorized
    }

    @PostMapping
    public ResponseEntity<Usuario> cadastrar(@Valid @RequestBody RegistroRequest registroRequest) {
        Usuario novoUsuario = new Usuario();
        novoUsuario.setNome(registroRequest.getNome());
        novoUsuario.setEmail(registroRequest.getEmail());
        novoUsuario.setSenha(registroRequest.getSenha()); // adicionar hash futuramente
        Usuario salvo = usuarioRepository.save(novoUsuario);
        return ResponseEntity.status(201).body(salvo); // 201 Created
    }
}

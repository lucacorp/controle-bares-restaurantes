package com.exemplo.controlemesas.controller;

import com.exemplo.controlemesas.model.Usuario;
import com.exemplo.controlemesas.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/usuarios")
public class UsuarioController {

    @Autowired
    private UsuarioRepository usuarioRepository;

    // Endpoint para login
    @PostMapping("/login")
    public ResponseEntity<Usuario> login(@RequestBody Usuario usuario) {
        Optional<Usuario> usuarioEncontrado = usuarioRepository.findByNomeAndSenha(usuario.getNome(), usuario.getSenha());
        return usuarioEncontrado
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.status(401).build()); // 401 Unauthorized se não encontrar
    }

    // Endpoint para cadastrar novo usuário
    @PostMapping
    public ResponseEntity<Usuario> cadastrar(@RequestBody Usuario usuario) {
        Usuario novoUsuario = usuarioRepository.save(usuario);
        return ResponseEntity.status(201).body(novoUsuario); // 201 Created ao cadastrar
    }
}

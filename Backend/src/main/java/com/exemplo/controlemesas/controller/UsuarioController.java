package com.exemplo.controlemesas.controller;

import com.exemplo.controlemesas.model.Usuario;
import com.exemplo.controlemesas.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/usuarios")
@CrossOrigin(origins = "*") // libera o acesso do frontend
public class UsuarioController {

    @Autowired
    private UsuarioRepository usuarioRepository;

    // Endpoint para login
    @PostMapping("/login")
    public Optional<Usuario> login(@RequestBody Usuario usuario) {
        return usuarioRepository.findByNomeAndSenha(usuario.getNome(), usuario.getSenha());
    }

    // Endpoint para cadastrar novo usuário (opcional)
    @PostMapping
    public Usuario cadastrar(@RequestBody Usuario usuario) {
        return usuarioRepository.save(usuario);
    }
}

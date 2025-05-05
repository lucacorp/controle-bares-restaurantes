package com.exemplo.controlemesas.controller;

import com.exemplo.controlemesas.model.Usuario;
import com.exemplo.controlemesas.repository.UsuarioRepository;
import com.exemplo.controlemesas.security.JwtUtil;
import com.exemplo.controlemesas.dto.LoginDTO;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;

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
    public ResponseEntity<String> register(@RequestBody @Valid Usuario usuario) {
        if (usuarioRepository.existsByLogin(usuario.getLogin())) {
            return ResponseEntity.badRequest().body("Login já está em uso.");
        }

        usuario.setSenha(passwordEncoder.encode(usuario.getSenha()));
        usuarioRepository.save(usuario);

        return ResponseEntity.ok("Usuário registrado com sucesso.");
    }

    // Autenticação + geração de token
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody @Valid LoginDTO loginDTO) {
        Authentication auth = authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(loginDTO.getLogin(), loginDTO.getSenha())
        );

        if (auth.isAuthenticated()) {
            String token = jwtUtil.generateToken(loginDTO.getLogin());
            return ResponseEntity.ok(Collections.singletonMap("token", token));
        } else {
            return ResponseEntity.status(401).body("Login ou senha inválidos.");
        }
    }
}

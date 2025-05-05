package com.exemplo.controlemesas.service;

import com.exemplo.controlemesas.model.Usuario;
import com.exemplo.controlemesas.repository.UsuarioRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.*;
import org.springframework.stereotype.Service;

@Service
public class UsuarioService implements UserDetailsService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Usuario usuario = usuarioRepository.findByUsername(username)
            .orElseThrow(() -> new UsernameNotFoundException("Usuário não encontrado"));

        return User.builder()
                .username(usuario.getUsername())
                .password(usuario.getSenha()) // deve estar encriptada
                .roles("USER") // ou admin, etc — pode evoluir isso depois
                .build();
    }
}

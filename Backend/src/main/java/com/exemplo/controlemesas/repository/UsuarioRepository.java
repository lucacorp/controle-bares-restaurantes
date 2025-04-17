package com.exemplo.controlemesas.repository;

import com.exemplo.controlemesas.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
    Optional<Usuario> findByNomeAndSenha(String nome, String senha);
}

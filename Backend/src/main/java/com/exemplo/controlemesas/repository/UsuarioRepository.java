package com.exemplo.controlemesas.repository;

import com.exemplo.controlemesas.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

    Optional<Usuario> findByEmailAndSenha(String email, String senha);

    Optional<Usuario> findByEmail(String email);

    boolean existsByEmail(String email);

    // Se você estiver tratando o nome como username (login)
    Optional<Usuario> findByNome(String nome);
}

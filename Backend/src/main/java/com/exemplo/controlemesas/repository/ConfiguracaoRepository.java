// src/main/java/com/exemplo/controlemesas/repository/ConfiguracaoRepository.java
package com.exemplo.controlemesas.repository;

import com.exemplo.controlemesas.model.Configuracao;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.List;

public interface ConfiguracaoRepository extends JpaRepository<Configuracao, Long> {
  Optional<Configuracao> findByChave(String chave);
  List<Configuracao> findByChaveStartingWith(String prefixo);
}

// src/main/java/com/exemplo/controlemesas/model/Configuracao.java
package com.exemplo.controlemesas.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "configuracoes")
@Data @NoArgsConstructor @AllArgsConstructor
public class Configuracao {

  @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  /** CHAVE: “empresa.cnpj”, “sat.serie”, “printer.default”, etc. */
  @Column(nullable = false, length = 120, unique = true)
  private String chave;

  /** Valor em texto (até 4 KB). Pode ser JSON quando preciso. */
  @Column(nullable = false, columnDefinition = "TEXT")
  private String valor;
}

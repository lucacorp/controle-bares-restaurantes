package com.exemplo.controlemesas.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class ComandaResumoDTO {
    private Long id;
    private Long comandaId;
    private BigDecimal total;
    private LocalDateTime dataFechamento;
    private String nomeCliente;
    private String observacoes;

    // Getters e Setters
}

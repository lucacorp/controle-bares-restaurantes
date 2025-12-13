package com.exemplo.controlemesas.dce;

import lombok.Data;

import java.math.BigDecimal;

/**
 * Representa um item/produto na DC-e.
 */
@Data
public class ItemDCe {
    
    private String codigoProduto;
    private String descricao;
    private String ncm; // Nomenclatura Comum do Mercosul
    private BigDecimal quantidade;
    private BigDecimal valorUnitario;
    private BigDecimal valorTotal;
    
    // Dados adicionais
    private String unidade = "UN"; // Unidade (UN, KG, etc.)
    private BigDecimal peso; // Peso em kg
}

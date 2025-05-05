package com.exemplo.controlemesas.dto;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;

public class ReceitaItemDTO {
    
	@NotNull(message = "Produto é obrigatório.")
	private Long produtoId;
    
	@NotNull(message = "Quantidade é obrigatória.")
    @Positive(message = "A quantidade deve ser maior que 0.")
	private BigDecimal quantidade;

    public Long getProdutoId() {
        return produtoId;
    }

    public void setProdutoId(Long produtoId) {
        this.produtoId = produtoId;
    }

    public BigDecimal getQuantidade() {
        return quantidade;
    }

    public void setQuantidade(BigDecimal quantidade) {
        this.quantidade = quantidade;
    }
}

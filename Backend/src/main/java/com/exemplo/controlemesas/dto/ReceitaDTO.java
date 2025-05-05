package com.exemplo.controlemesas.dto;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.util.List;

public class ReceitaDTO {
    private Long id;
    @NotBlank(message = "O nome da receita é obrigatório.")
    private String nome;
    @NotNull(message = "O adicional é obrigatório.")
    @Min(value = 0, message = "O adicional deve ser no mínimo 0.")
    private BigDecimal adicional;
    
    @NotNull(message = "Produto final é obrigatório.")
    private Long produtoFinalId;
    
    
    @NotEmpty(message = "A receita deve conter pelo menos um item.")
    private List<ReceitaItemDTO> itens;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public BigDecimal getAdicional() {
        return adicional;
    }

    public void setAdicional(BigDecimal adicional) {
        this.adicional = adicional;
    }

    public Long getProdutoFinalId() {
        return produtoFinalId;
    }

    public void setProdutoFinalId(Long produtoFinalId) {
        this.produtoFinalId = produtoFinalId;
    }

    public List<ReceitaItemDTO> getItens() {
        return itens;
    }

    public void setItens(List<ReceitaItemDTO> itens) {
        this.itens = itens;
    }
}

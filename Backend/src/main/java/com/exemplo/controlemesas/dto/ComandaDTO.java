package com.exemplo.controlemesas.dto;

import com.exemplo.controlemesas.model.Comanda;
import com.exemplo.controlemesas.model.Mesa;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

public class ComandaDTO {
    private Long id;
    private Mesa mesa;
    private LocalDateTime dataAbertura;
    private boolean ativa;
    private BigDecimal valorTotal;
    private List<ItemComandaDTO> itens;

    public ComandaDTO() {}

    public ComandaDTO(Comanda comanda) {
        this.id = comanda.getId();
        this.mesa = comanda.getMesa();
        this.dataAbertura = comanda.getDataAbertura();
        this.ativa = comanda.isAtiva();
        this.itens = comanda.getItens().stream()
                .map(ItemComandaDTO::new)
                .collect(Collectors.toList());
        this.valorTotal = this.itens.stream()
                .map(ItemComandaDTO::getTotal) // <-- CORREÇÃO AQUI
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
    
    // Getters e Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Mesa getMesa() {
        return mesa;
    }

    public void setMesa(Mesa mesa) {
        this.mesa = mesa;
    }

    public LocalDateTime getDataAbertura() {
        return dataAbertura;
    }

    public void setDataAbertura(LocalDateTime dataAbertura) {
        this.dataAbertura = dataAbertura;
    }

    public boolean isAtiva() {
        return ativa;
    }

    public void setAtiva(boolean ativa) {
        this.ativa = ativa;
    }

    public BigDecimal getValorTotal() {
        return valorTotal;
    }

    public void setValorTotal(BigDecimal valorTotal) {
        this.valorTotal = valorTotal;
    }

    public List<ItemComandaDTO> getItens() {
        return itens;
    }

    public void setItens(List<ItemComandaDTO> itens) {
        this.itens = itens;
    }
}
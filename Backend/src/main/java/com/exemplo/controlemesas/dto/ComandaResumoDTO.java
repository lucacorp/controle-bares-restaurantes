package com.exemplo.controlemesas.dto;

import com.exemplo.controlemesas.model.Comanda;
import com.exemplo.controlemesas.model.ComandaResumo;
import java.math.BigDecimal;
import java.time.LocalDateTime;

public class ComandaResumoDTO {

    private Long id;
    private Long comandaId;
    private LocalDateTime dataFechamento;
    private BigDecimal total;
    private String statusSat; // compatibilidade com backend/antigo SAT
    // campo mais genérico para UI/uso com NFC-e
    private String statusFiscal;
    private String nomeCliente;
    private String observacoes;

    // Construtor para a entidade Comanda
    public ComandaResumoDTO(Comanda comanda) {
        this.id = comanda.getId();
        this.comandaId = comanda.getId();
        this.dataFechamento = comanda.getDataFechamento();
        this.total = BigDecimal.ZERO; // Ou o valor que você desejar
        // Outros campos podem ser nulos ou ter valores padrão, pois uma Comanda aberta não tem resumo
    }
    
    // Construtor para a entidade ComandaResumo
    public ComandaResumoDTO(ComandaResumo resumo) {
        this.id = resumo.getId();
        this.comandaId = resumo.getComanda().getId();
        this.dataFechamento = resumo.getDataFechamento();
        this.total = resumo.getTotal();
        this.nomeCliente = resumo.getNomeCliente();
        this.observacoes = resumo.getObservacoes();
        // preencher estado fiscal a partir do campo existente (compatibilidade)
        this.statusSat = resumo.getStatusSat();
        this.statusFiscal = resumo.getStatusFiscal();
    }
    
    // Getters e Setters (mantenha os seus)
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getComandaId() { return comandaId; }
    public void setComandaId(Long comandaId) { this.comandaId = comandaId; }
    public LocalDateTime getDataFechamento() { return dataFechamento; }
    public void setDataFechamento(LocalDateTime dataFechamento) { this.dataFechamento = dataFechamento; }
    public BigDecimal getTotal() { return total; }
    public void setTotal(BigDecimal total) { this.total = total; }
    public String getNomeCliente() { return nomeCliente; }
    public void setNomeCliente(String nomeCliente) { this.nomeCliente = nomeCliente; }
    public String getObservacoes() { return observacoes; }
    public void setObservacoes(String observacoes) { this.observacoes = observacoes; }
    public String getStatusSat() { return statusSat; }
    public void setStatusSat(String statusSat) { this.statusSat = statusSat; }
    public String getStatusFiscal() { return statusFiscal; }
    public void setStatusFiscal(String statusFiscal) { this.statusFiscal = statusFiscal; }
}

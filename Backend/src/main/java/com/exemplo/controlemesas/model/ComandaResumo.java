package com.exemplo.controlemesas.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "comanda_resumo")
public class ComandaResumo {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne
	@JoinColumn(name = "comanda_id")
	@com.fasterxml.jackson.annotation.JsonIgnore
	private Comanda comanda;

	private LocalDateTime dataFechamento;

	@Column(name = "pdf_path")
	private String pdfPath;
	
	private BigDecimal total; // mantém compatibilidade com seu front atual
	private BigDecimal totalBruto;
	private BigDecimal desconto;
	private BigDecimal acrescimo;

	private BigDecimal valorPorPessoa;
	private Integer qtdPessoas;

	private String nomeCliente;
	private String observacoes;

	private String numeroCupom; // ex: "000124"
	private String chaveSat; // chave de acesso da NFC-e/SAT
	private String statusSat = "PENDENTE";
	private String xmlPath;

	@OneToMany(mappedBy = "comandaResumo", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<ItemComandaResumo> itens = new ArrayList<>();

	// ====== Getters e Setters ======
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Comanda getComanda() {
		return comanda;
	}

	public void setComanda(Comanda comanda) {
		this.comanda = comanda;
	}

	public LocalDateTime getDataFechamento() {
		return dataFechamento;
	}

	public void setDataFechamento(LocalDateTime dataFechamento) {
		this.dataFechamento = dataFechamento;
	}

	public BigDecimal getTotal() {
		return total;
	}

	public void setTotal(BigDecimal total) {
		this.total = total;
	}

	public BigDecimal getTotalBruto() {
		return totalBruto;
	}

	public void setTotalBruto(BigDecimal totalBruto) {
		this.totalBruto = totalBruto;
	}

	public BigDecimal getDesconto() {
		return desconto;
	}

	public void setDesconto(BigDecimal desconto) {
		this.desconto = desconto;
	}

	public BigDecimal getAcrescimo() {
		return acrescimo;
	}

	public void setAcrescimo(BigDecimal acrescimo) {
		this.acrescimo = acrescimo;
	}

	public BigDecimal getValorPorPessoa() {
		return valorPorPessoa;
	}

	public void setValorPorPessoa(BigDecimal valorPorPessoa) {
		this.valorPorPessoa = valorPorPessoa;
	}

	public Integer getQtdPessoas() {
		return qtdPessoas;
	}

	public void setQtdPessoas(Integer qtdPessoas) {
		this.qtdPessoas = qtdPessoas;
	}

	public String getNomeCliente() {
		return nomeCliente;
	}

	public void setNomeCliente(String nomeCliente) {
		this.nomeCliente = nomeCliente;
	}

	public String getObservacoes() {
		return observacoes;
	}

	public void setObservacoes(String observacoes) {
		this.observacoes = observacoes;
	}

	public String getNumeroCupom() {
		return numeroCupom;
	}

	public void setNumeroCupom(String numeroCupom) {
		this.numeroCupom = numeroCupom;
	}

	public String getChaveSat() {
		return chaveSat;
	}

	public void setChaveSat(String chaveSat) {
		this.chaveSat = chaveSat;
	}

	public String getStatusSat() {
		return statusSat;
	}

	public void setStatusSat(String statusSat) {
		this.statusSat = statusSat;
	}

	public String getXmlPath() {
		return xmlPath;
	}

	public void setXmlPath(String xmlPath) {
		this.xmlPath = xmlPath;
	}

	public List<ItemComandaResumo> getItens() {
		return itens;
	}

	public void setItens(List<ItemComandaResumo> itens) {
		this.itens = itens;
	}
	
	public String getPdfPath() {
	    return pdfPath;
	}

	public void setPdfPath(String pdfPath) {
	    this.pdfPath = pdfPath;
	}
}

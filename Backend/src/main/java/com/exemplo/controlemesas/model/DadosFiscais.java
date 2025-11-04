package com.exemplo.controlemesas.model;

import jakarta.persistence.Embeddable;
import java.math.BigDecimal;

/**
 * Bloco fiscal incorporado na entidade Produto. Mantém todos os dados
 * necessários para emissão de SAT/NFC‑e sem JOIN extra.
 */
@Embeddable
public class DadosFiscais {

	private String cfop; // Código Fiscal de Operações e Prestações
	private String cst; // Código de Situação Tributária
	private String origem; // Origem da mercadoria (0‑8)
	private String ncm; // Nomenclatura Comum do Mercosul

	private BigDecimal aliqIcms;
	private BigDecimal aliqPis;
	private BigDecimal aliqCofins;

	// ===== Getters & Setters =====
	public String getCfop() {
		return cfop;
	}

	public void setCfop(String cfop) {
		this.cfop = cfop;
	}

	public String getCst() {
		return cst;
	}

	public void setCst(String cst) {
		this.cst = cst;
	}

	public String getOrigem() {
		return origem;
	}

	public void setOrigem(String origem) {
		this.origem = origem;
	}

	public String getNcm() {
		return ncm;
	}

	public void setNcm(String ncm) {
		this.ncm = ncm;
	}

	public BigDecimal getAliqIcms() {
		return aliqIcms;
	}

	public void setAliqIcms(BigDecimal aliqIcms) {
		this.aliqIcms = aliqIcms;
	}

	public BigDecimal getAliqPis() {
		return aliqPis;
	}

	public void setAliqPis(BigDecimal aliqPis) {
		this.aliqPis = aliqPis;
	}

	public BigDecimal getAliqCofins() {
		return aliqCofins;
	}

	public void setAliqCofins(BigDecimal aliqCofins) {
		this.aliqCofins = aliqCofins;
	}
}

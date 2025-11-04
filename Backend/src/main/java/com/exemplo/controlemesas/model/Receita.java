package com.exemplo.controlemesas.model;

import jakarta.persistence.*;
import java.util.List;
import java.math.BigDecimal; // Importação necessária para BigDecimal
import com.fasterxml.jackson.annotation.JsonManagedReference;

@Entity
@Table(name = "receita")
public class Receita {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	// Adicionados campos 'nome' e 'adicional' conforme os erros
	private String nome;
	private BigDecimal adicional;

	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "produto_final_id", unique = true, nullable = false)
	private Produto produtoFinal;

	@OneToMany(mappedBy = "receita", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
	@JsonManagedReference
	private List<ReceitaItem> itens;

	public Receita() {
	}

	public Receita(Produto produtoFinal) {
		this.produtoFinal = produtoFinal;
	}

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

	public Produto getProdutoFinal() {
		return produtoFinal;
	}

	public void setProdutoFinal(Produto produtoFinal) {
		this.produtoFinal = produtoFinal;
	}

	public List<ReceitaItem> getItens() {
		return itens;
	}

	public void setItens(List<ReceitaItem> itens) {
		this.itens = itens;
	}

	public void addItem(ReceitaItem item) {
		if (this.itens == null) {
			this.itens = new java.util.ArrayList<>();
		}
		this.itens.add(item);
		item.setReceita(this);
	}

	public void removeItem(ReceitaItem item) {
		if (this.itens != null) {
			this.itens.remove(item);
			item.setReceita(null);
		}
	}
}

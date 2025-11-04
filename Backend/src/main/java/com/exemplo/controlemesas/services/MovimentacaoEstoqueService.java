package com.exemplo.controlemesas.services;

import com.exemplo.controlemesas.model.*;
import com.exemplo.controlemesas.repository.MovimentacaoEstoqueRepository;
import com.exemplo.controlemesas.repository.ReceitaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List; // Adicione esta importação para 'List'

@Service
public class MovimentacaoEstoqueService {

	private final MovimentacaoEstoqueRepository movRepo;
	private final EstoqueService estoqueService;
	private final ReceitaRepository receitaRepo;

	public MovimentacaoEstoqueService(MovimentacaoEstoqueRepository movRepo, EstoqueService estoqueService,
			ReceitaRepository receitaRepo) {
		this.movRepo = movRepo;
		this.estoqueService = estoqueService;
		this.receitaRepo = receitaRepo;
	}

	/* ---------- CORE ---------- */
	@Transactional
	public MovimentacaoEstoque registrarMovimentacao(Produto produto, BigDecimal quantidade,
			MovimentacaoEstoque.TipoMovimentacao tipo, String obs) {

		MovimentacaoEstoque mov = new MovimentacaoEstoque();
		mov.setProduto(produto);
		mov.setQuantidade(quantidade);
		mov.setTipo(tipo);
		mov.setDataMovimentacao(LocalDateTime.now());
		mov.setObservacao(obs);

		MovimentacaoEstoque salvo = movRepo.save(mov);

		boolean entrada = (tipo == MovimentacaoEstoque.TipoMovimentacao.ENTRADA);
		estoqueService.ajustarEstoque(produto, quantidade, entrada);

		return salvo;
	}

	/* ---------- BAIXAS ---------- */
	public void baixarProdutoDireto(Produto p, BigDecimal qtd, String obs) {
		validarSaldo(p, qtd);
		registrarMovimentacao(p, qtd, MovimentacaoEstoque.TipoMovimentacao.SAIDA, obs);
	}

	public void baixarIngredientesReceita(Long receitaId, BigDecimal qtdReceitas, String obsBase) {
		Receita receita = receitaRepo.findById(receitaId)
				.orElseThrow(() -> new IllegalArgumentException("Receita não encontrada"));

		receita.getItens().forEach(it -> {
			Produto ing = it.getProduto();
			BigDecimal qtdTotal = it.getQuantidade().multiply(qtdReceitas);

			validarSaldo(ing, qtdTotal);
			registrarMovimentacao(ing, qtdTotal, MovimentacaoEstoque.TipoMovimentacao.SAIDA,
					obsBase + " – " + ing.getNome());
		});
	}

	/* ---------- ESTORNOS ---------- */
	public void estornarProdutoDireto(Produto p, BigDecimal qtd, String obs) {
		registrarMovimentacao(p, qtd, MovimentacaoEstoque.TipoMovimentacao.ENTRADA, obs);
	}

	public void estornarIngredientesReceita(Long receitaId, BigDecimal qtdReceitas, String obsBase) {
		Receita receita = receitaRepo.findById(receitaId)
				.orElseThrow(() -> new IllegalArgumentException("Receita não encontrada"));

		receita.getItens().forEach(it -> {
			Produto ing = it.getProduto();
			BigDecimal qtdTotal = it.getQuantidade().multiply(qtdReceitas);

			registrarMovimentacao(ing, qtdTotal, MovimentacaoEstoque.TipoMovimentacao.ENTRADA,
					obsBase + " – " + ing.getNome());
		});
		// O método 'listarPorProduto' foi movido para fora deste bloco.
	}

	// --- Este método deve ficar aqui, fora de qualquer outro método ---
	public List<MovimentacaoEstoque> listarPorProduto(Produto produto) {
		return movRepo.findByProduto(produto);
	}

	/* ---------- util ---------- */
	private void validarSaldo(Produto prod, BigDecimal qtdNec) {
		BigDecimal saldo = estoqueService.consultarSaldo(prod);
		if (saldo.compareTo(qtdNec) < 0) {
			throw new IllegalArgumentException(
					"Estoque insuficiente de " + prod.getNome() + " (Disp.: " + saldo + ", Nec.: " + qtdNec + ")");
		}
	}
}
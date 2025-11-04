package com.exemplo.controlemesas.controller;

import com.exemplo.controlemesas.dto.MovimentacaoEstoqueDTO;
import com.exemplo.controlemesas.dto.MovimentacaoEstoqueResponseDTO;
import com.exemplo.controlemesas.model.MovimentacaoEstoque;
import com.exemplo.controlemesas.model.Produto;
import com.exemplo.controlemesas.repository.ProdutoRepository;
import com.exemplo.controlemesas.services.MovimentacaoEstoqueService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus; // Importe HttpStatus
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/movimentacoes-estoque") // Certifique-se que este é o RequestMapping correto
@CrossOrigin(origins = "http://localhost:5173")
public class MovimentacaoEstoqueController {

	private final MovimentacaoEstoqueService movimentacaoService;
	private final ProdutoRepository produtoRepository;

	public MovimentacaoEstoqueController(MovimentacaoEstoqueService movimentacaoService,
			ProdutoRepository produtoRepository) {
		this.movimentacaoService = movimentacaoService;
		this.produtoRepository = produtoRepository;
	}

	@PostMapping // Endpoint para POST (Ajuste de Estoque)
	public ResponseEntity<String> registrarMovimentacao(@Valid @RequestBody MovimentacaoEstoqueDTO dto) {
		try {
			Produto produto = produtoRepository.findById(dto.getProdutoId())
					.orElseThrow(() -> new IllegalArgumentException("Produto não encontrado"));

			movimentacaoService.registrarMovimentacao(produto, dto.getQuantidade(),
					MovimentacaoEstoque.TipoMovimentacao.valueOf(dto.getTipo().toUpperCase().trim()),
					dto.getObservacao());

			return ResponseEntity.ok("Movimentação registrada com sucesso!");
		} catch (IllegalArgumentException e) {
			// Se o produto não for encontrado, retorne 400 Bad Request
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
		} catch (Exception e) {
			// Para outros erros inesperados, retorne 500 Internal Server Error
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body("Erro interno ao registrar movimentação: " + e.getMessage());
		}
	}

	@GetMapping("/{produtoId}") // Endpoint para GET (Listar Movimentações por Produto)
	public ResponseEntity<List<MovimentacaoEstoqueResponseDTO>> listarPorProduto(@PathVariable Long produtoId) {
		try {
			Produto produto = produtoRepository.findById(produtoId)
					.orElseThrow(() -> new IllegalArgumentException("Produto não encontrado"));

			List<MovimentacaoEstoqueResponseDTO> movimentacoes = movimentacaoService.listarPorProduto(produto).stream()
					.map(MovimentacaoEstoqueResponseDTO::new).toList();

			return ResponseEntity.ok(movimentacoes);
		} catch (IllegalArgumentException e) {
			// Se o produto não for encontrado, retorne 404 Not Found (ou 400 Bad Request,
			// dependendo da sua preferência)
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null); // Retorna 404 sem corpo, ou com uma mensagem
																			// de erro
		} catch (Exception e) {
			// Para outros erros inesperados, retorne 500 Internal Server Error
			e.printStackTrace(); // Imprima a stack trace para depuração!
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null); // Ou com uma mensagem de erro
		}
	}
}
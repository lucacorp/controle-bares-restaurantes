package com.exemplo.controlemesas.controller;

import com.exemplo.controlemesas.dto.EstoqueDTO;
import com.exemplo.controlemesas.model.Estoque;
import com.exemplo.controlemesas.model.Produto;
import com.exemplo.controlemesas.repository.EstoqueRepository;
import com.exemplo.controlemesas.repository.ProdutoRepository;
import com.exemplo.controlemesas.services.EstoqueService;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/estoques")
@CrossOrigin(origins = "http://localhost:5173")
public class EstoqueController {

	private final EstoqueService estoqueService;
	private final ProdutoRepository produtoRepository;
	private final EstoqueRepository estoqueRepository;

	public EstoqueController(EstoqueService estoqueService, ProdutoRepository produtoRepository,
			EstoqueRepository estoqueRepository) {
		this.estoqueService = estoqueService;
		this.produtoRepository = produtoRepository;
		this.estoqueRepository = estoqueRepository;
	}

	// GET /api/estoques - lista com DTO para o front
	@GetMapping
	public List<EstoqueDTO> listarTodos() {
		List<Estoque> estoques = estoqueRepository.findAll();

		return estoques.stream().map(
				e -> new EstoqueDTO(e.getId(), e.getProduto().getId(), e.getProduto().getNome(), e.getQuantidade()))
				.toList();
	}

	// GET /api/estoques/{produtoId} - consulta saldo de um produto
	@GetMapping("/{produtoId}")
	public BigDecimal consultarSaldo(@PathVariable Long produtoId) {
		Produto produto = produtoRepository.findById(produtoId)
				.orElseThrow(() -> new IllegalArgumentException("Produto não encontrado"));

		return estoqueService.consultarSaldo(produto);
	}

	// POST /api/estoques/ajuste - ajusta o saldo de um produto
	@PostMapping("/ajuste")
	public String ajustarEstoque(@RequestParam Long produtoId, @RequestParam BigDecimal quantidade,
			@RequestParam boolean entrada) {
		Produto produto = produtoRepository.findById(produtoId)
				.orElseThrow(() -> new IllegalArgumentException("Produto não encontrado"));

		estoqueService.ajustarEstoque(produto, quantidade, entrada);

		return "Estoque ajustado com sucesso!";
	}

	// --- Tratamento de erros ---

	// Produto não encontrado
	@ExceptionHandler(IllegalArgumentException.class)
	public ResponseEntity<String> handleIllegalArgumentException(IllegalArgumentException ex) {
		return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
	}

	// Violação de integridade de dados (ex: FK inválida, trigger)
	@ExceptionHandler(DataIntegrityViolationException.class)
	public ResponseEntity<String> handleDataIntegrityViolationException(DataIntegrityViolationException ex) {
		return ResponseEntity.status(HttpStatus.CONFLICT).body("Erro de integridade de dados: " + ex.getMessage());
	}

	// Erro genérico
	@ExceptionHandler(Exception.class)
	public ResponseEntity<String> handleGenericException(Exception ex) {
		return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erro interno: " + ex.getMessage());
	}
}

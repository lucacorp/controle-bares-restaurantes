package com.exemplo.controlemesas.controller;

import com.exemplo.controlemesas.dto.ReceitaDTO;
import com.exemplo.controlemesas.dto.ReceitaItemDTO;
import com.exemplo.controlemesas.model.Produto;
import com.exemplo.controlemesas.model.Receita;
import com.exemplo.controlemesas.model.ReceitaItem;
import com.exemplo.controlemesas.repository.ProdutoRepository;
import com.exemplo.controlemesas.repository.ReceitaItemRepository;
import com.exemplo.controlemesas.repository.ReceitaRepository;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Validated
@RestController
@RequestMapping("/api/receitas")
@CrossOrigin(origins = "http://localhost:5173")
public class ReceitaController {

	private static final Logger logger = LoggerFactory.getLogger(ReceitaController.class);

	@Autowired
	private ReceitaRepository receitaRepository;

	@Autowired
	private ProdutoRepository produtoRepository;

	@Autowired
	private ReceitaItemRepository receitaItemRepository;

	@GetMapping
	public ResponseEntity<List<ReceitaDTO>> listar() {
		List<Receita> receitas = receitaRepository.findAll();
		List<ReceitaDTO> receitasDTO = receitas.stream().map(this::toDTO).collect(Collectors.toList());
		return ResponseEntity.ok(receitasDTO);
	}

	@GetMapping("/{id}")
	public ResponseEntity<ReceitaDTO> buscarPorId(@PathVariable Long id) {
		Optional<Receita> receitaOpt = receitaRepository.findById(id);
		return receitaOpt.map(this::toDTO).map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
	}

	@PostMapping
	public ResponseEntity<ReceitaDTO> salvar(@RequestBody @Valid ReceitaDTO dto) {
		Receita receita = fromDTO(dto);
		receita = receitaRepository.save(receita);
		return ResponseEntity.status(201).body(toDTO(receita));
	}

	@PutMapping("/{id}")
	public ResponseEntity<ReceitaDTO> atualizar(@PathVariable Long id, @RequestBody @Valid ReceitaDTO dto) {
		Optional<Receita> receitaOpt = receitaRepository.findById(id);
		if (receitaOpt.isEmpty())
			return ResponseEntity.notFound().build();

		Receita receita = receitaOpt.get();
		receita.setNome(dto.getNome());
		// CORREÇÃO FINAL AQUI: Garante que ambos os lados do ternário são BigDecimal
		receita.setAdicional(dto.getAdicional() != null ? BigDecimal.valueOf(dto.getAdicional()) : BigDecimal.ZERO);

		Produto produtoFinal = produtoRepository.findById(dto.getProdutoFinalId())
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Produto final não encontrado"));
		receita.setProdutoFinal(produtoFinal);

		receita.getItens().clear();
		receitaRepository.save(receita);

		List<ReceitaItem> novosItens = new ArrayList<>();
		if (dto.getItens() != null) {
			for (ReceitaItemDTO itemDTO : dto.getItens()) {
				Produto produto = produtoRepository.findById(itemDTO.getProdutoId())
						.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Produto não encontrado"));

				ReceitaItem item = new ReceitaItem();
				item.setProduto(produto);
				// CORREÇÃO FINAL AQUI: Garante que ambos os lados do ternário são BigDecimal
				item.setQuantidade(itemDTO.getQuantidade() != null ? BigDecimal.valueOf(itemDTO.getQuantidade())
						: BigDecimal.ZERO);
				item.setReceita(receita);
				novosItens.add(item);
			}
		}
		receita.setItens(novosItens);

		receita = receitaRepository.save(receita);
		return ResponseEntity.ok(toDTO(receita));
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<Void> deletar(@PathVariable Long id) {
		if (!receitaRepository.existsById(id))
			return ResponseEntity.notFound().build();
		receitaRepository.deleteById(id);
		return ResponseEntity.noContent().build();
	}

	private ReceitaDTO toDTO(Receita receita) {
		ReceitaDTO dto = new ReceitaDTO();
		dto.setId(receita.getId());
		dto.setNome(receita.getNome());
		// CORREÇÃO FINAL AQUI: Garante que ambos os lados do ternário são Double
		dto.setAdicional(receita.getAdicional() != null ? receita.getAdicional().doubleValue() : 0.0);

		if (receita.getProdutoFinal() != null) {
			dto.setProdutoFinalId(receita.getProdutoFinal().getId());
		} else {
			dto.setProdutoFinalId(null);
			logger.warn("Receita ID {} tem produtoFinal nulo.", receita.getId());
		}

		List<ReceitaItemDTO> itensDTO = (receita.getItens() != null) ? receita.getItens().stream().map(item -> {
			ReceitaItemDTO itemDTO = new ReceitaItemDTO();
			if (item.getProduto() != null) {
				itemDTO.setProdutoId(item.getProduto().getId());
			} else {
				itemDTO.setProdutoId(null);
				logger.warn("ReceitaItem ID {} tem produto nulo na Receita ID {}.", item.getId(), receita.getId());
			}
			// CORREÇÃO FINAL AQUI: Garante que ambos os lados do ternário são Double
			itemDTO.setQuantidade(item.getQuantidade() != null ? item.getQuantidade().doubleValue() : 0.0);
			return itemDTO;
		}).collect(Collectors.toList()) : new ArrayList<>();

		dto.setItens(itensDTO);
		return dto;
	}

	private Receita fromDTO(ReceitaDTO dto) {
		Receita receita = new Receita();
		if (dto.getId() != null) {
			receita.setId(dto.getId());
		}
		receita.setNome(dto.getNome());
		// CORREÇÃO FINAL AQUI: Garante que ambos os lados do ternário são BigDecimal
		receita.setAdicional(dto.getAdicional() != null ? BigDecimal.valueOf(dto.getAdicional()) : BigDecimal.ZERO);

		Produto produtoFinal = produtoRepository.findById(dto.getProdutoFinalId())
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Produto final não encontrado"));
		receita.setProdutoFinal(produtoFinal);

		List<ReceitaItem> itens = new ArrayList<>();
		if (dto.getItens() != null) {
			for (ReceitaItemDTO itemDTO : dto.getItens()) {
				Produto produto = produtoRepository.findById(itemDTO.getProdutoId())
						.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Produto não encontrado"));
				ReceitaItem item = new ReceitaItem();
				item.setProduto(produto);
				// CORREÇÃO FINAL AQUI: Garante que ambos os lados do ternário são BigDecimal
				item.setQuantidade(itemDTO.getQuantidade() != null ? BigDecimal.valueOf(itemDTO.getQuantidade())
						: BigDecimal.ZERO);
				item.setReceita(receita);
				itens.add(item);
			}
		}
		receita.setItens(itens);
		return receita;
	}
}
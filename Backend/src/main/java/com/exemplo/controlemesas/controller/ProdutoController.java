package com.exemplo.controlemesas.controller;

import com.exemplo.controlemesas.dto.ProdutoDTO;

import com.exemplo.controlemesas.model.Produto;
import com.exemplo.controlemesas.repository.ProdutoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/produtos")
public class ProdutoController {

    @Autowired
    private ProdutoRepository produtoRepository;

    @GetMapping
    public List<ProdutoDTO> listarTodos() {
        return produtoRepository.findAll()
                .stream()
                .map(p -> new ProdutoDTO(p)) // usando construtor do DTO
                .collect(Collectors.toList());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProdutoDTO> buscarPorId(@PathVariable Long id) {
        return produtoRepository.findById(id)
                .map(p -> ResponseEntity.ok(new ProdutoDTO(p)))
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<ProdutoDTO> salvar(@RequestBody Produto produto) {
        Produto salvo = produtoRepository.save(produto);
        return ResponseEntity.ok(new ProdutoDTO(salvo));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProdutoDTO> atualizar(@PathVariable Long id, @RequestBody Produto produto) {
        return produtoRepository.findById(id).map(existing -> {
            // update allowed fields (keep id/version)
            existing.setCodigoBarras(produto.getCodigoBarras());
            existing.setNome(produto.getNome());
            existing.setGrupo(produto.getGrupo());
            existing.setUnidade(produto.getUnidade());
            existing.setPrecoVenda(produto.getPrecoVenda());
            existing.setCategoria(produto.getCategoria());
            existing.setDescricao(produto.getDescricao());
            existing.setPreco(produto.getPreco());
            existing.setFabricacaoPropria(produto.isFabricacaoPropria());
            existing.setCfop(produto.getCfop());
            existing.setCst(produto.getCst());
            existing.setOrigem(produto.getOrigem());
            existing.setAliquotaIcms(produto.getAliquotaIcms());
            existing.setAliquotaIpi(produto.getAliquotaIpi());
            Produto salvo = produtoRepository.save(existing);
            return ResponseEntity.ok(new ProdutoDTO(salvo));
        }).orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        if (!produtoRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        produtoRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
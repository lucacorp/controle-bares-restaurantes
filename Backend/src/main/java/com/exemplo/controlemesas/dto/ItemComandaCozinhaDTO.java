package com.exemplo.controlemesas.dto;

import com.exemplo.controlemesas.model.ItemComanda;
import com.exemplo.controlemesas.model.enums.StatusItemComanda;

public class ItemComandaCozinhaDTO {
    private Long id;
    private String produto;
    private Integer quantidade;
    private StatusItemComanda status;

    // getters e setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getProduto() { return produto; }
    public void setProduto(String produto) { this.produto = produto; }

    public Integer getQuantidade() { return quantidade; }
    public void setQuantidade(Integer quantidade) { this.quantidade = quantidade; }

    public StatusItemComanda getStatus() { return status; }
    public void setStatus(StatusItemComanda status) { this.status = status; }

    // Método estático para converter Entidade em DTO
    public static ItemComandaCozinhaDTO fromEntity(ItemComanda item) {
        ItemComandaCozinhaDTO dto = new ItemComandaCozinhaDTO();
        dto.setId(item.getId());
        dto.setProduto(item.getProduto().getNome()); // supondo que Produto tem um campo 'nome'
        dto.setQuantidade(item.getQuantidade());
        dto.setStatus(item.getStatus());
        return dto;
    }
}
package com.exemplo.controlemesas.dto;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ConfiguracaoEnderecoDTO {
    private String logradouro;
    private String numero;
    private String bairro;
    private String cidade;
    private String uf;
    private String cep;
}

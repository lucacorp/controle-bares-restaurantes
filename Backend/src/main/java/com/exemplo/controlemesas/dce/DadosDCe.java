package com.exemplo.controlemesas.dce;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Dados necessários para construção de uma DC-e.
 */
@Data
public class DadosDCe {
    
    // Identificação da DC-e
    private Integer numeroLote;
    private Integer codigoUF;
    private Integer codigoNumerico;
    private Integer modelo = 59; // Modelo 59 para DC-e
    private Integer serie;
    private Integer numero;
    private LocalDateTime dataEmissao;
    private Integer tipoEmissao = 1; // 1=Normal
    private Integer tipoAmbiente; // 1=Produção, 2=Homologação
    private Integer finalidade = 1; // 1=DC-e normal
    private Integer processoEmissao = 0; // 0=Aplicativo próprio
    private String versaoAplicativo;
    
    // Remetente (quem envia - geralmente Correios)
    private String remetenteCNPJ;
    private String remetenteNome;
    private String remetenteLogradouro;
    private String remetenteNumero;
    private String remetenteComplemento;
    private String remetenteBairro;
    private String remetenteCodigoMunicipio;
    private String remetenteMunicipio;
    private String remetenteUF;
    private String remetenteCEP;
    
    // Destinatário (quem recebe)
    private String destinatarioCNPJ;
    private String destinatarioCPF;
    private String destinatarioNome;
    private String destinatarioLogradouro;
    private String destinatarioNumero;
    private String destinatarioComplemento;
    private String destinatarioBairro;
    private String destinatarioCodigoMunicipio;
    private String destinatarioMunicipio;
    private String destinatarioUF;
    private String destinatarioCEP;
    
    // Itens/Produtos
    private List<ItemDCe> itens;
    
    // Totais
    private BigDecimal valorTotal;
    
    // Transporte
    private Integer modalidadeFrete = 9; // 9=Sem frete (padrão DC-e)
    
    // Informações adicionais (Correios)
    private String codigoRastreio;
    private String modalidadePostagem; // SEDEX, PAC, etc.
    private BigDecimal pesoTotal; // em kg
    private String objetoPostal; // Tipo de objeto (caixa, envelope, etc.)
}

# Diretório para armazenamento de XMLs DC-e

Este diretório armazena os XMLs gerados pela emissão de DC-e (Declaração de Conteúdo Eletrônica).

## Estrutura de Arquivos

Os XMLs são salvos com timestamp e tipo:

```
20251212_103045_DCe_1_nao_assinado.xml
20251212_103045_DCe_1_assinado.xml
20251212_103047_DCe_1_resposta_autorizacao.xml
20251212_103050_DCe_1_resposta_consulta.xml
```

## Tipos de Arquivo

- **nao_assinado.xml**: XML original antes da assinatura digital
- **assinado.xml**: XML após assinatura digital (enviado para SEFAZ)
- **resposta_autorizacao.xml**: Resposta da SEFAZ ao envio
- **resposta_consulta.xml**: Resposta da consulta de recibo

## Retenção

Recomenda-se manter os XMLs por pelo menos 5 anos para fins fiscais e auditoria.

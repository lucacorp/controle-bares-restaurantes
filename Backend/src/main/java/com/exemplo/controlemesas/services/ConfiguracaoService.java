package com.exemplo.controlemesas.services;

import com.exemplo.controlemesas.dto.ConfiguracaoDTO;
import com.exemplo.controlemesas.model.Configuracao;
import com.exemplo.controlemesas.repository.ConfiguracaoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.exemplo.controlemesas.dto.ConfiguracaoEnderecoDTO;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ConfiguracaoService {

    private final ConfiguracaoRepository repo;

    /* ------------------------- CRUD EXISTENTE ------------------------- */

    public List<ConfiguracaoDTO> listar(String prefix) {
        var lista = (prefix == null || prefix.isBlank())
                ? repo.findAll()
                : repo.findByChaveStartingWith(prefix);
        return lista.stream()
                .map(c -> new ConfiguracaoDTO(c.getChave(), c.getValor()))
                .toList();
    }

    public ConfiguracaoDTO salvar(ConfiguracaoDTO dto) {
        var cfg = repo.findByChave(dto.getChave())
                      .orElse(new Configuracao(null, dto.getChave(), dto.getValor()));
        cfg.setValor(dto.getValor());
        return toDto(repo.save(cfg));
    }

    private ConfiguracaoDTO toDto(Configuracao c) {
        return new ConfiguracaoDTO(c.getChave(), c.getValor());
    }
    
    private static final ObjectMapper json = new ObjectMapper();

    public ConfiguracaoEnderecoDTO getEnderecoEmpresa() {
        String raw = get("empresa.endereco", null);
        try {
            return raw != null
                ? json.readValue(raw, ConfiguracaoEnderecoDTO.class)
                : new ConfiguracaoEnderecoDTO(); // valores padrão se não houver
        } catch (Exception e) {
            throw new IllegalStateException("Erro ao ler endereço configurado", e);
        }
    }
    public void salvarEnderecoEmpresa(ConfiguracaoEnderecoDTO dto) {
        try {
            String jsonStr = json.writeValueAsString(dto);
            salvar(new ConfiguracaoDTO("empresa.endereco", jsonStr));
        } catch (Exception e) {
            throw new IllegalStateException("Erro ao salvar endereço configurado", e);
        }
    }

    /* ------------------------ UTILITÁRIOS ------------------------ */

    public String get(String chave, String def) {
        return repo.findByChave(chave)
                   .map(Configuracao::getValor)
                   .orElse(def);
    }

    public int getInt(String chave, int def) {
        try {
            return Integer.parseInt(get(chave, String.valueOf(def)));
        } catch (NumberFormatException e) {
            return def;
        }
    }

    /* ------------------------ NOVO: MODO FISCAL ------------------------ */

    public String getModoFiscal() {
        return get("modo.fiscal", "SAT");
    }

    public void setModoFiscal(String modo) {
        repo.save(new Configuracao(null, "modo.fiscal", modo.toUpperCase()));
    }
}

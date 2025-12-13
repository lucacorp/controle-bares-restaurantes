package com.exemplo.controlemesas.controller;

import com.exemplo.controlemesas.model.Mesa;
import com.exemplo.controlemesas.repository.MesaRepository;
import com.exemplo.controlemesas.dto.MesaDTO;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/mesas")
public class MesaController {

    @Autowired
    private MesaRepository mesaRepository;

    @GetMapping
    public ResponseEntity<List<MesaDTO>> listar() {
        List<MesaDTO> dtos = mesaRepository.findAll().stream().map(this::toDto).collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }

    @PostMapping
    public ResponseEntity<MesaDTO> criar(@Valid @RequestBody MesaDTO dto) {
        Mesa mesa = toEntity(dto);
        Mesa saved = mesaRepository.save(mesa);
        return ResponseEntity.ok(toDto(saved));
    }

    // Simple mapping helpers - keep lightweight to avoid introducing a dependency
    private MesaDTO toDto(Mesa m) {
        if (m == null) return null;
        MesaDTO d = new MesaDTO();
        d.setId(m.getId());
        d.setDescricao(m.getDescricao());
        d.setNome(m.getNome());
        d.setNumero(m.getNumero());
        d.setOcupada(m.isOcupada());
        d.setStatus(m.getStatus());
        return d;
    }

    private Mesa toEntity(MesaDTO d) {
        if (d == null) return null;
        Mesa m = new Mesa();
        m.setId(d.getId());
        m.setDescricao(d.getDescricao());
        m.setNome(d.getNome());
        m.setNumero(d.getNumero());
        m.setOcupada(d.isOcupada());
        m.setStatus(d.getStatus());
        return m;
    }
}
package com.exemplo.controlemesas.controller;

import com.exemplo.controlemesas.model.ComandaResumo;
import com.exemplo.controlemesas.repository.ComandaResumoRepository;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@RestController
@RequestMapping("/api/arquivos")
@CrossOrigin(origins = "http://localhost:5173")
@RequiredArgsConstructor
public class ArquivoController {

    private final ComandaResumoRepository resumoRepo;

    @GetMapping("/xml/{id}")
    public ResponseEntity<Resource> baixarXml(@PathVariable Long id) {
        ComandaResumo resumo = resumoRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Resumo não encontrado"));

        if (resumo.getChaveSat() == null || resumo.getChaveSat().isBlank()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }

        Path path = Paths.get("data/nfe/xml/NFe_" + resumo.getChaveSat() + ".xml");
        if (!Files.exists(path)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }

        Resource resource = new FileSystemResource(path);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + path.getFileName() + "\"")
                .contentType(MediaType.APPLICATION_XML)
                .body(resource);
    }

    @GetMapping("/pdf/{id}")
    public ResponseEntity<Resource> baixarPdf(@PathVariable Long id) {
        ComandaResumo resumo = resumoRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Resumo não encontrado"));

        if (resumo.getPdfPath() == null || resumo.getPdfPath().isBlank()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }

        Path path = Paths.get(resumo.getPdfPath());
        if (!Files.exists(path)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }

        Resource resource = new FileSystemResource(path);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + path.getFileName() + "\"")
                .contentType(MediaType.APPLICATION_PDF)
                .body(resource);
    }
}

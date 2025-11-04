package com.exemplo.controlemesas.util;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import lombok.extern.slf4j.Slf4j;

/**
 * Helper simples para gravar arquivos em disco criando pastas se preciso.
 */
@Slf4j
public final class FilesHelper {

    private FilesHelper() {}

    /**
     * Grava um ARQUIVO DE TEXTO (como XML) com charset específico.
     */
    public static Path writeFile(String dirBase, String nomeArquivo, String conteudo, Charset charset) throws IOException {
        return writeFile(dirBase, nomeArquivo, conteudo.getBytes(charset));
    }

    /**
     * Grava um ARQUIVO BINÁRIO (como PDF, XML em bytes, etc.).
     */
    public static Path writeFile(String dirBase, String nomeArquivo, byte[] conteudo) throws IOException {
        Path dir = Paths.get(dirBase);
        Files.createDirectories(dir);

        Path arq = dir.resolve(nomeArquivo);
        Path result = Files.write(arq, conteudo);

        log.debug("📄 Arquivo salvo em: {}", result.toAbsolutePath());
        return result;
    }
}

// src/main/java/com/exemplo/controlemesas/util/PdfUtils.java
package com.exemplo.controlemesas.util;

import com.openhtmltopdf.pdfboxout.PdfRendererBuilder;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

/** Converte HTML em PDF usando OpenHTMLToPdf (Flying Saucer + PDFBox). */
public final class PdfUtils {

    private PdfUtils() {}

    public static byte[] htmlToPdf(String html) throws IOException {
        try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            new PdfRendererBuilder()
                    .useFastMode()
                    .withHtmlContent(html, null)
                    .toStream(out)
                    .run();
            return out.toByteArray();
        } catch (Exception e) {
            throw new IOException("Falha ao gerar PDF", e);
        }
    }
}

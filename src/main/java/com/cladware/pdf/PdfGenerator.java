package com.cladware.pdf;

import com.lowagie.text.DocumentException;
import org.xhtmlrenderer.pdf.ITextRenderer;

import java.io.ByteArrayOutputStream;

public class PdfGenerator {
    public static byte[] generatePdf(String url) throws DocumentException {
        ITextRenderer renderer = new ITextRenderer();
        renderer.setDocument(url);
        renderer.layout();
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        renderer.createPDF(bos);
        return bos.toByteArray();
    }
}

package com.example.modificar.pdf.itext.controllers;

import com.example.modificar.pdf.itext.services.PdfService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class PdfController {

    @Autowired
    private PdfService pdfService;

    @GetMapping("/modificarDocumento/{nombreDocumento}")
    public ResponseEntity<String> modificarDocumento(@PathVariable String nombreDocumento){
        try {
            var rutaModificada = pdfService.modificarDocumento(nombreDocumento);
            return ResponseEntity.ok().body("Documento modificado con éxito y guardado en: " + rutaModificada);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error al modificar el documento: " + e.getMessage());
        }
    }

    @GetMapping("/modificarDocumentoPDFA/{nombreDocumento}")
    public ResponseEntity<String> modificarDocumentoPDFA(@PathVariable String nombreDocumento){
        try {
            var rutaModificada = pdfService.modificarDocumentoFormatoPdfA(nombreDocumento);
            return ResponseEntity.ok().body("Documento modificado en formato PDF/A con éxito y guardado en: " + rutaModificada);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error al modificar el documento, no se pudo convertir en formato PDF/A: " + e.getMessage());
        }
    }
}


package com.example.modificar.pdf.itext.services;

import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.kernel.pdf.*;
import com.itextpdf.kernel.utils.PdfMerger;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Image;
import com.itextpdf.pdfa.PdfADocument;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Random;

@Service
public class PdfService {

    @Autowired
    private ResourceLoader resourceLoader;

    public String modificarDocumento(String nombreDocumento) {
        try {
            // Rutas a los archivos
            Resource originalResource = resourceLoader.getResource("classpath:static/originales/" + nombreDocumento + ".pdf");
            Resource imageResource = resourceLoader.getResource("classpath:static/imagenDemo.png");
            // Crear la carpeta modificados si no existe
            Path pathModificados = Paths.get(resourceLoader.getResource("classpath:static/").getURI()).resolve("modificados");
            if (!Files.exists(pathModificados)) {
                System.out.println("Creando directorio para modificados porque no existe en " + pathModificados);
                Files.createDirectories(pathModificados);
            }

            System.out.println("Antes de crear pdfreader: " + String.valueOf(originalResource.getURI()));
            PdfReader pdfReader = new PdfReader(String.valueOf(originalResource.getURL()));


            String rutaModificada = pathModificados.resolve(nombreDocumento + new Random().nextInt(100) + ".pdf").toString();
            System.out.println("Antes de crear pdfwritter: " + rutaModificada);
            PdfWriter pdfWriter = new PdfWriter(rutaModificada);

            System.out.println("Antes de crear pdfdocument");
            // Abrir el documento original
            PdfDocument pdfDoc = new PdfDocument(pdfReader, pdfWriter);

            System.out.println("Antes de crear documento");
            // Crear un nuevo documento con iText
            Document document = new Document(pdfDoc);

            System.out.println("Antes de agregar una pagina");
            // Agregar una nueva página al final
            pdfDoc.addNewPage();

            System.out.println("Antes de cargar la imagen: " + imageResource.getURL());
            // Cargar la imagen
            Image image = new Image(ImageDataFactory.create(imageResource.getURL()));

            System.out.println("Antes de agregar la imagen");
            // Agregar la imagen a la última página
            document.add(image.setFixedPosition(pdfDoc.getNumberOfPages(), 50, 50));

            System.out.println("Antes de cerrar el document");
            // Cerrar el documento
            document.close();
            return rutaModificada;
        }
        catch (IOException e) {
            System.out.println(e.getMessage());
            throw new RuntimeException("Error al modificar el documento", e);
        }

    }

    public String modificarDocumentoFormatoPdfA(String nombreDocumento){
        try{
            // Rutas a los archivos
            Resource originalResource = resourceLoader.getResource("classpath:static/originales/" + nombreDocumento + ".pdf");
            Resource imageResource = resourceLoader.getResource("classpath:static/imagenDemo.png");
            // Crear la carpeta modificados si no existe
            Path pathModificados = Paths.get(resourceLoader.getResource("classpath:static/").getURI()).resolve("modificados");
            if (!Files.exists(pathModificados)) {
                System.out.println("Creando directorio para modificados porque no existe en " + pathModificados);
                Files.createDirectories(pathModificados);
            }

            // Crea un Pdf/A-3B vacio con el RGB del resources
            System.out.println("Antes de crear el PDF/A vacio");
            String rutaModificada = pathModificados.resolve(nombreDocumento + new Random().nextInt(100) + "FormatoPDFA.pdf").toString();
            PdfWriter pdfAWriter = new PdfWriter(rutaModificada);
            InputStream srgbIccProfileStream = getClass().getClassLoader().getResourceAsStream("sRGB-IEC61966-2.1.icc");
            PdfOutputIntent pdfOutputIntent = new PdfOutputIntent("Custom", "",null, "sRGB IEC61966-2.1", srgbIccProfileStream);
            PdfADocument pdfADocument = new PdfADocument(pdfAWriter, PdfAConformanceLevel.PDF_A_3B,pdfOutputIntent);

            // Crea el lector del pdf original
            System.out.println("Antes de crear pdfreader: " + String.valueOf(originalResource.getURI()));
            PdfReader pdfReader = new PdfReader(String.valueOf(originalResource.getURL()));
            PdfDocument pdfDocument = new PdfDocument(pdfReader);

            // Crear un nuevo documento con iText
            Document document = new Document(pdfADocument);

            System.out.println("Antes de copiar el contenido");
            // Copia el contenido a un PDF/A
            PdfMerger merger = new PdfMerger(pdfADocument);
            merger.merge(pdfDocument, 1, pdfDocument.getNumberOfPages());

            System.out.println("Antes de agregar una pagina");
            // Agregar una nueva página al final
            pdfADocument.addNewPage();

            System.out.println("Antes de cargar la imagen: " + imageResource.getURL());
            // Cargar la imagen
            Image image = new Image(ImageDataFactory.create(imageResource.getURL()));

            System.out.println("Antes de agregar la imagen");
            // Agregar la imagen a la última página
            document.add(image.setFixedPosition(pdfADocument.getNumberOfPages(), 50, 50));

            System.out.println("Antes de cerrar el document");
            // Cerrar el documento
            document.close();

            return rutaModificada;
        }catch (IOException e){
            System.out.println(e.getMessage());
            throw new RuntimeException("Error al modificar el documento", e);
        }
    }


}

package com.taller.services;

import com.itextpdf.io.image.ImageData;
import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.borders.Border;
import com.itextpdf.layout.borders.SolidBorder;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Image;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.UnitValue;
import com.taller.models.Configuracion;
import com.taller.models.Factura;
import com.taller.models.Repuesto;

import java.io.IOException;
import java.net.URL;

/**
 * Servicio de exportación de facturas a formato PDF.
 * <p>
 * Genera documentos PDF profesionales usando la librería iTextPdf con
 * logotipo del taller, tabla de repuestos, cálculo de totales y pie de página.
 * </p>
 *
 * @author BRANDON CAPACHO
 * @version 1.0
 * @see com.taller.models.Factura
 */
public class PdfExportService {

    public static void generarFacturaPdf(Factura factura, Configuracion config, String rutaDestino) {
        try {
            PdfWriter writer = new PdfWriter(rutaDestino);
            PdfDocument pdf = new PdfDocument(writer);
            Document document = new Document(pdf);

            // Cabecera con Logo y Título
            Table headerTable = new Table(UnitValue.createPercentArray(new float[]{30, 70})).useAllAvailableWidth();
            
            URL logoUrl = PdfExportService.class.getResource("/com/taller/assets/taller_logo.png");
            if (logoUrl != null) {
                ImageData data = ImageDataFactory.create(logoUrl);
                Image logo = new Image(data);
                logo.scaleToFit(120, 120);
                headerTable.addCell(new Cell().add(logo).setBorder(Border.NO_BORDER).setTextAlignment(TextAlignment.LEFT));
            } else {
                headerTable.addCell(new Cell().add(new Paragraph("")).setBorder(Border.NO_BORDER));
            }
            
            String nombreTaller = config != null ? config.nombreTaller() : "Taller Mecánico";
            Cell titleCell = new Cell().setBorder(Border.NO_BORDER).setTextAlignment(TextAlignment.RIGHT);
            titleCell.add(new Paragraph(nombreTaller)
                    .setFontSize(26)
                    .setFontColor(ColorConstants.DARK_GRAY)
                    .setBold());
            titleCell.add(new Paragraph("FACTURA #" + factura.id())
                    .setFontSize(14)
                    .setFontColor(ColorConstants.GRAY)
                    .setBold());
            headerTable.addCell(titleCell);
            document.add(headerTable);

            // Línea separadora
            document.add(new Paragraph("\n"));
            Table separator = new Table(UnitValue.createPercentArray(new float[]{100})).useAllAvailableWidth();
            separator.addCell(new Cell().setBorder(new SolidBorder(ColorConstants.LIGHT_GRAY, 1)));
            document.add(separator);
            document.add(new Paragraph("\n"));

            // Info de Factura y Vehículo
            Table infoTable = new Table(UnitValue.createPercentArray(new float[]{50, 50})).useAllAvailableWidth();
            infoTable.addCell(new Cell().add(new Paragraph("Detalles del Vehículo").setBold().setFontColor(ColorConstants.DARK_GRAY)).setBorder(Border.NO_BORDER));
            infoTable.addCell(new Cell().add(new Paragraph("Detalles de Emisión").setBold().setFontColor(ColorConstants.DARK_GRAY)).setBorder(Border.NO_BORDER).setTextAlignment(TextAlignment.RIGHT));
            infoTable.addCell(new Cell().add(new Paragraph("Placa: " + factura.placaVehiculo())).setBorder(Border.NO_BORDER));
            infoTable.addCell(new Cell().add(new Paragraph("Fecha: " + factura.fecha().toString())).setBorder(Border.NO_BORDER).setTextAlignment(TextAlignment.RIGHT));
            document.add(infoTable);
            document.add(new Paragraph("\n"));

            // Tabla de Repuestos y Conceptos
            Table table = new Table(UnitValue.createPercentArray(new float[]{75, 25})).useAllAvailableWidth();
            table.addHeaderCell(new Cell().add(new Paragraph("CONCEPTO / DESCRIPCIÓN").setBold().setFontColor(ColorConstants.WHITE)).setBackgroundColor(ColorConstants.DARK_GRAY).setPadding(8));
            table.addHeaderCell(new Cell().add(new Paragraph("PRECIO").setBold().setFontColor(ColorConstants.WHITE)).setBackgroundColor(ColorConstants.DARK_GRAY).setPadding(8).setTextAlignment(TextAlignment.RIGHT));

            // Mano de obra
            table.addCell(new Cell().add(new Paragraph("Servicio Mecánico (Mano de Obra)")).setPadding(8).setBorderBottom(new SolidBorder(ColorConstants.LIGHT_GRAY, 1)));
            table.addCell(new Cell().add(new Paragraph(String.format("$%,.2f", factura.manoObra()))).setPadding(8).setTextAlignment(TextAlignment.RIGHT).setBorderBottom(new SolidBorder(ColorConstants.LIGHT_GRAY, 1)));

            // Repuestos
            if (factura.repuestos() != null && !factura.repuestos().isEmpty()) {
                for (Repuesto r : factura.repuestos()) {
                    table.addCell(new Cell().add(new Paragraph("Repuesto: " + r.nombre())).setPadding(8).setBorderBottom(new SolidBorder(ColorConstants.LIGHT_GRAY, 1)));
                    table.addCell(new Cell().add(new Paragraph(String.format("$%,.2f", r.precio()))).setPadding(8).setTextAlignment(TextAlignment.RIGHT).setBorderBottom(new SolidBorder(ColorConstants.LIGHT_GRAY, 1)));
                }
            }
            document.add(table);
            document.add(new Paragraph("\n"));

            // Totales
            Table totalTable = new Table(UnitValue.createPercentArray(new float[]{75, 25})).useAllAvailableWidth();
            totalTable.addCell(new Cell().add(new Paragraph("TOTAL GENERAL:")).setBorder(Border.NO_BORDER).setTextAlignment(TextAlignment.RIGHT).setBold().setFontSize(14).setFontColor(ColorConstants.DARK_GRAY));
            totalTable.addCell(new Cell().add(new Paragraph(String.format("$%,.2f", factura.total()))).setBorder(Border.NO_BORDER).setTextAlignment(TextAlignment.RIGHT).setBold().setFontSize(16));
            document.add(totalTable);

            // Pie de página
            document.add(new Paragraph("\n\n"));
            document.add(new Paragraph("Gracias por preferir " + nombreTaller)
                    .setTextAlignment(TextAlignment.CENTER)
                    .setFontColor(ColorConstants.GRAY)
                    .setItalic()
                    .setFontSize(10));

            document.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

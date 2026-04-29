package com.taller.controllers;

import com.taller.models.Factura;
import com.taller.models.Repuesto;
import com.taller.repository.FacturaRepository;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;

import javafx.stage.FileChooser;
import javafx.stage.Window;
import javafx.scene.control.ButtonType;

import java.awt.Desktop;
import java.io.File;
import java.net.URL;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.ResourceBundle;
import java.util.Optional;

import com.taller.repository.ConfigRepository;
import com.taller.models.Configuracion;
import com.taller.services.PdfExportService;

/**
 * Controlador de la vista de creación de facturas ({@code facturacion.fxml}).
 * <p>
 * Permite agregar repuestos, calcular el total en tiempo real,
 * guardar la factura en la base de datos y exportarla como PDF.
 * </p>
 *
 * @author BRANDON CAPACHO
 * @version 1.0
 * @see com.taller.repository.FacturaRepository
 * @see com.taller.services.PdfExportService
 */
public class FacturacionController implements Initializable {

    @FXML private TextField txtPlaca;
    @FXML private TextField txtManoObra;
    
    @FXML private TextField txtDescRepuesto;
    @FXML private TextField txtPrecioRepuesto;

    @FXML private TableView<Repuesto> tablaRepuestos;
    @FXML private TableColumn<Repuesto, String> colDescripcion;
    @FXML private TableColumn<Repuesto, Number> colPrecio;
    
    @FXML private Label lblTotal;

    private FacturaRepository repository;
    private ObservableList<Repuesto> repuestosList;
    private double totalAcumulado = 0.0;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        repository = new FacturaRepository();
        repuestosList = FXCollections.observableArrayList();

        colDescripcion.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().nombre()));
        colPrecio.setCellValueFactory(cellData -> new SimpleDoubleProperty(cellData.getValue().precio()));
        
        tablaRepuestos.setItems(repuestosList);
        
        txtManoObra.textProperty().addListener((obs, oldV, newV) -> recalcularTotal());
    }

    @FXML
    public void agregarRepuestoAction() {
        String desc = txtDescRepuesto.getText().trim();
        String precioStr = txtPrecioRepuesto.getText().trim();

        if (desc.isEmpty() || precioStr.isEmpty()) {
            return;
        }

        try {
            double precio = Double.parseDouble(precioStr);
            Repuesto r = new Repuesto(0, 0, desc, precio);
            repuestosList.add(r);
            
            txtDescRepuesto.clear();
            txtPrecioRepuesto.clear();
            recalcularTotal();
        } catch (NumberFormatException e) {
            mostrarAlerta(Alert.AlertType.ERROR, "Error", "El precio del repuesto debe ser un número válido.");
        }
    }
    
    @FXML
    public void eliminarRepuestoAction() {
        Repuesto seleccionado = tablaRepuestos.getSelectionModel().getSelectedItem();
        if (seleccionado != null) {
            repuestosList.remove(seleccionado);
            recalcularTotal();
        }
    }

    private void recalcularTotal() {
        double manoObra = 0;
        try {
            manoObra = Double.parseDouble(txtManoObra.getText().trim());
        } catch (NumberFormatException ignored) {}

        double repuestosTotal = repuestosList.stream().mapToDouble(Repuesto::precio).sum();
        totalAcumulado = manoObra + repuestosTotal;
        lblTotal.setText(String.format("Total: $%,.2f", totalAcumulado));
    }

    @FXML
    public void generarFacturaAction() {
        String placa = txtPlaca.getText().trim();
        if (placa.isEmpty()) {
            mostrarAlerta(Alert.AlertType.WARNING, "Faltan datos", "Ingrese la placa del vehículo.");
            return;
        }

        double manoObra = 0;
        try {
            manoObra = Double.parseDouble(txtManoObra.getText().trim());
        } catch (NumberFormatException e) {
            mostrarAlerta(Alert.AlertType.ERROR, "Error", "El costo de mano de obra es inválido.");
            return;
        }

        Factura factura = new Factura(0, null, placa, manoObra, totalAcumulado, new ArrayList<>(repuestosList));
        
        try {
            int idGenerado = repository.guardarFacturaConRepuestos(factura);
            
            // Build the complete factura for PDF
            Factura facturaGuardada = new Factura(idGenerado, new java.sql.Timestamp(System.currentTimeMillis()), placa, manoObra, totalAcumulado, new ArrayList<>(repuestosList));
            
            // Generate PDF
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Guardar Factura PDF");
            fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("PDF Document", "*.pdf"));
            fileChooser.setInitialFileName("Factura_" + idGenerado + ".pdf");
            
            Window stage = txtPlaca.getScene().getWindow();
            File file = fileChooser.showSaveDialog(stage);
            
            if (file != null) {
                Configuracion config = new ConfigRepository().obtenerConfiguracion();
                PdfExportService.generarFacturaPdf(facturaGuardada, config, file.getAbsolutePath());
                
                Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
                confirm.setTitle("Factura y PDF Generados");
                confirm.setHeaderText("Factura guardada con ID: " + idGenerado);
                confirm.setContentText("¿Desea abrir el archivo PDF ahora?");

                Optional<ButtonType> result = confirm.showAndWait();
                if (result.isPresent() && result.get() == ButtonType.OK) {
                    if (Desktop.isDesktopSupported() && Desktop.getDesktop().isSupported(Desktop.Action.OPEN)) {
                        Desktop.getDesktop().open(file);
                    } else {
                        mostrarAlerta(Alert.AlertType.WARNING, "No Soportado", "El sistema no permite abrir archivos automáticamente.");
                    }
                }
            } else {
                mostrarAlerta(Alert.AlertType.INFORMATION, "Éxito", "Factura guardada con ID: " + idGenerado + " (sin exportar PDF).");
            }
            
            limpiarTodo();
        } catch (SQLException e) {
            mostrarAlerta(Alert.AlertType.ERROR, "Error BD", "No se pudo guardar la factura: " + e.getMessage());
        } catch (Exception e) {
            mostrarAlerta(Alert.AlertType.ERROR, "Error PDF", "Ocurrió un error al generar PDF: " + e.getMessage());
        }
    }

    private void limpiarTodo() {
        txtPlaca.clear();
        txtManoObra.clear();
        txtManoObra.setText("0");
        txtDescRepuesto.clear();
        txtPrecioRepuesto.clear();
        repuestosList.clear();
        recalcularTotal();
    }

    private void mostrarAlerta(Alert.AlertType type, String titulo, String mensaje) {
        Alert alert = new Alert(type);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
}

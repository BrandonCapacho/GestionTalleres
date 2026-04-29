package com.taller.controllers;

import com.taller.models.Configuracion;
import com.taller.models.Factura;
import com.taller.repository.ConfigRepository;
import com.taller.repository.FacturaRepository;
import com.taller.services.PdfExportService;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.stage.FileChooser;
import javafx.stage.Window;

import java.awt.Desktop;
import java.io.File;
import java.net.URL;
import java.sql.SQLException;
import java.util.List;
import java.util.ResourceBundle;

/**
 * Controlador de la vista de búsqueda de facturas ({@code buscador_facturas.fxml}).
 * <p>
 * Permite buscar facturas por placa de vehículo o por cédula del cliente,
 * visualizarlas en una tabla y exportar la seleccionada como PDF.
 * </p>
 *
 * @author BRANDON CAPACHO
 * @version 1.0
 * @see com.taller.repository.FacturaRepository
 * @see com.taller.services.PdfExportService
 */
public class BuscadorFacturasController implements Initializable {

    @FXML private TextField txtBusqueda;

    @FXML private TableView<Factura> tablaFacturas;
    @FXML private TableColumn<Factura, Number> colId;
    @FXML private TableColumn<Factura, String> colPlaca;
    @FXML private TableColumn<Factura, String> colFecha;
    @FXML private TableColumn<Factura, Number> colTotal;

    private FacturaRepository repository;
    private ConfigRepository configRepository;
    private ObservableList<Factura> facturasList;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        repository = new FacturaRepository();
        configRepository = new ConfigRepository();
        facturasList = FXCollections.observableArrayList();

        colId.setCellValueFactory(cellData -> new SimpleIntegerProperty(cellData.getValue().id()));
        colPlaca.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().placaVehiculo()));
        colFecha.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().fecha().toString()));
        colTotal.setCellValueFactory(cellData -> new SimpleDoubleProperty(cellData.getValue().total()));

        tablaFacturas.setItems(facturasList);
    }

    @FXML
    public void buscarPorPlacaAction() {
        String placa = txtBusqueda.getText().trim();
        if (placa.isEmpty()) return;
        try {
            List<Factura> encontradas = repository.buscarFacturasPorPlaca(placa);
            facturasList.setAll(encontradas);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void buscarPorCedulaAction() {
        String cedula = txtBusqueda.getText().trim();
        if (cedula.isEmpty()) return;
        try {
            List<Factura> encontradas = repository.buscarFacturasPorCedula(cedula);
            facturasList.setAll(encontradas);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void exportarPdfAction() {
        Factura seleccionada = tablaFacturas.getSelectionModel().getSelectedItem();
        if (seleccionada == null) {
            mostrarAlerta(Alert.AlertType.WARNING, "Aviso", "Seleccione una factura de la tabla para exportar.");
            return;
        }

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Guardar Factura PDF");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("PDF Document", "*.pdf"));
        fileChooser.setInitialFileName("Factura_" + seleccionada.id() + ".pdf");
        
        Window stage = tablaFacturas.getScene().getWindow();
        File file = fileChooser.showSaveDialog(stage);

        if (file != null) {
            try {
                Configuracion config = configRepository.obtenerConfiguracion();
                PdfExportService.generarFacturaPdf(seleccionada, config, file.getAbsolutePath());
                
                Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
                confirm.setTitle("PDF Generado");
                confirm.setHeaderText("La factura se guardó correctamente.");
                confirm.setContentText("¿Desea abrir el archivo PDF ahora?");

                java.util.Optional<ButtonType> result = confirm.showAndWait();
                if (result.isPresent() && result.get() == ButtonType.OK) {
                    if (Desktop.isDesktopSupported() && Desktop.getDesktop().isSupported(Desktop.Action.OPEN)) {
                        Desktop.getDesktop().open(file);
                    } else {
                        mostrarAlerta(Alert.AlertType.WARNING, "No Soportado", "El sistema no permite abrir archivos automáticamente.");
                    }
                }
            } catch (SQLException e) {
                mostrarAlerta(Alert.AlertType.ERROR, "Error", "No se pudo cargar la configuración: " + e.getMessage());
            } catch (Exception e) {
                mostrarAlerta(Alert.AlertType.ERROR, "Error", "Ocurrió un error al generar PDF: " + e.getMessage());
            }
        }
    }

    private void mostrarAlerta(Alert.AlertType type, String titulo, String mensaje) {
        Alert alert = new Alert(type);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
}

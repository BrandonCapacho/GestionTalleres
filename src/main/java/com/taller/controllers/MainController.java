package com.taller.controllers;

import com.taller.models.Configuracion;
import com.taller.repository.ConfigRepository;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.control.TextInputDialog;
import javafx.scene.layout.StackPane;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.Optional;
import java.util.ResourceBundle;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;

/**
 * Controlador principal de la aplicación ({@code main.fxml}).
 * <p>
 * Gestiona la barra superior de navegación, carga dinámica de vistas
 * en el área de contenido central, atajos de teclado y acceso protegido
 * a la configuración del taller.
 * </p>
 *
 * <b>Atajos de teclado:</b>
 * <ul>
 *   <li>{@code Ctrl+1} → Clientes</li>
 *   <li>{@code Ctrl+2} → Vehículos</li>
 *   <li>{@code Ctrl+3} → Nueva Factura</li>
 *   <li>{@code Ctrl+4} → Historial de Facturas</li>
 *   <li>{@code Ctrl+H} → Inicio (Dashboard)</li>
 * </ul>
 *
 * @author BRANDON CAPACHO
 * @version 2.0
 */
public class MainController implements Initializable {

    @FXML private Label lblTallerNombre;
    @FXML private StackPane contentArea;
    
    private ConfigRepository configRepo = new ConfigRepository();
    private String adminPassword = "admin";

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        cargarConfiguracion();
        navInicio(); // Load the new dashboard initially

        contentArea.sceneProperty().addListener((obs, oldScene, newScene) -> {
            if (newScene != null) {
                newScene.getAccelerators().put(new KeyCodeCombination(KeyCode.DIGIT1, KeyCombination.CONTROL_DOWN), () -> navClientes());
                newScene.getAccelerators().put(new KeyCodeCombination(KeyCode.DIGIT2, KeyCombination.CONTROL_DOWN), () -> navVehiculos());
                newScene.getAccelerators().put(new KeyCodeCombination(KeyCode.DIGIT3, KeyCombination.CONTROL_DOWN), () -> navNuevaFactura());
                newScene.getAccelerators().put(new KeyCodeCombination(KeyCode.DIGIT4, KeyCombination.CONTROL_DOWN), () -> navHistorialFacturas());
                newScene.getAccelerators().put(new KeyCodeCombination(KeyCode.H, KeyCombination.CONTROL_DOWN), () -> navInicio());
            }
        });
    }

    public void cargarConfiguracion() {
        try {
            Configuracion config = configRepo.obtenerConfiguracion();
            if (config != null) {
                lblTallerNombre.setText(config.nombreTaller());
                adminPassword = config.claveAcceso();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void navInicio() { mostrarVista("/com/taller/views/dashboard.fxml"); }

    @FXML
    public void navClientes() { mostrarVista("/com/taller/views/clientes.fxml"); }

    @FXML
    public void navVehiculos() { mostrarVista("/com/taller/views/vehiculos.fxml"); }

    @FXML
    public void navNuevaFactura() { mostrarVista("/com/taller/views/facturacion.fxml"); }

    @FXML
    public void navHistorialFacturas() { mostrarVista("/com/taller/views/buscador_facturas.fxml"); }

    @FXML
    public void navConfiguracion() {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Acceso Restringido");
        dialog.setHeaderText("Configuración del Taller");
        dialog.setContentText("Ingrese la contraseña de administrador:");

        Optional<String> result = dialog.showAndWait();
        if (result.isPresent() && result.get().equals(adminPassword)) {
            mostrarVista("/com/taller/views/config.fxml");
        }
    }

    private void mostrarVista(String fxmlPath) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Parent view = loader.load();
            
            Object controller = loader.getController();
            if (controller instanceof ConfigController) {
                ((ConfigController) controller).setMainController(this);
            } else if (controller instanceof DashboardController) {
                ((DashboardController) controller).setMainController(this);
            }
            
            contentArea.getChildren().clear();
            contentArea.getChildren().add(view);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

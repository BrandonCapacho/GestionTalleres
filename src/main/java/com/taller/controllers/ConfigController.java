package com.taller.controllers;

import com.taller.models.Configuracion;
import com.taller.repository.ConfigRepository;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

import java.net.URL;
import java.sql.SQLException;
import java.util.ResourceBundle;

/**
 * Controlador de la vista de configuración ({@code config.fxml}).
 * <p>
 * Permite modificar el nombre del taller, la ruta del logotipo
 * y la clave de acceso del sistema. El acceso está protegido por contraseña.
 * </p>
 *
 * @author BRANDON CAPACHO
 * @version 1.0
 * @see com.taller.repository.ConfigRepository
 */
public class ConfigController implements Initializable {

    @FXML private TextField txtNombreTaller;
    @FXML private TextField txtLogoPath;
    @FXML private PasswordField txtNuevaClave;

    private ConfigRepository repository;
    private MainController mainController;

    public void setMainController(MainController mc) {
        this.mainController = mc;
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        repository = new ConfigRepository();
        cargarDatos();
    }

    private void cargarDatos() {
        try {
            Configuracion config = repository.obtenerConfiguracion();
            if (config != null) {
                txtNombreTaller.setText(config.nombreTaller());
                txtLogoPath.setText(config.logoPath());
                txtNuevaClave.setText(config.claveAcceso());
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void guardarConfiguracionAction() {
        String nombre = txtNombreTaller.getText().trim();
        String logo = txtLogoPath.getText().trim();
        String clave = txtNuevaClave.getText().trim();

        if (nombre.isEmpty() || clave.isEmpty()) {
            mostrarAlerta(Alert.AlertType.WARNING, "Advertencia", "El nombre y la clave no pueden estar vacíos.");
            return;
        }

        try {
            // Preserve the current theme when updating other settings
            Configuracion existing = repository.obtenerConfiguracion();
            String currentTheme = (existing != null && existing.tema() != null) ? existing.tema() : "dark";
            Configuracion config = new Configuracion(1, nombre, logo, clave, currentTheme);
            repository.actualizarConfiguracion(config);
            if (mainController != null) {
                mainController.cargarConfiguracion();
            }
            mostrarAlerta(Alert.AlertType.INFORMATION, "Éxito", "Configuración actualizada correctamente.");
        } catch (SQLException e) {
            mostrarAlerta(Alert.AlertType.ERROR, "Error", "Fallo al guardar: " + e.getMessage());
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

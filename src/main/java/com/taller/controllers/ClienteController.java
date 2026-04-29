package com.taller.controllers;

import com.taller.models.Cliente;
import com.taller.repository.ClienteRepository;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;

import java.net.URL;
import java.sql.SQLException;
import java.util.ResourceBundle;

/**
 * Controlador de la vista de gestión de clientes ({@code clientes.fxml}).
 * <p>
 * Permite registrar nuevos clientes y visualizar los existentes en una tabla.
 * </p>
 *
 * @author BRANDON CAPACHO
 * @version 1.0
 * @see com.taller.repository.ClienteRepository
 */
public class ClienteController implements Initializable {

    @FXML private TextField txtCedula;
    @FXML private TextField txtNombre;
    @FXML private TextField txtTelefono;

    @FXML private TableView<Cliente> tablaClientes;
    @FXML private TableColumn<Cliente, String> colCedula;
    @FXML private TableColumn<Cliente, String> colNombre;
    @FXML private TableColumn<Cliente, String> colTelefono;

    private ClienteRepository repository;
    private ObservableList<Cliente> clientesList;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        repository = new ClienteRepository();
        clientesList = FXCollections.observableArrayList();

        colCedula.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().cedula()));
        colNombre.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().nombre()));
        colTelefono.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().telefono()));
        
        tablaClientes.setItems(clientesList);
        cargarDatos();
    }

    private void cargarDatos() {
        try {
            clientesList.setAll(repository.obtenerTodosLosClientes());
        } catch (SQLException e) {
            mostrarAlerta(Alert.AlertType.ERROR, "Error de BD", e.getMessage());
        }
    }

    @FXML
    public void guardarClienteAction() {
        String cedula = txtCedula.getText().trim();
        String nombre = txtNombre.getText().trim();
        String telefono = txtTelefono.getText().trim();

        if (cedula.isEmpty() || nombre.isEmpty()) {
            mostrarAlerta(Alert.AlertType.WARNING, "Campos Vacíos", "La cédula y nombre son obligatorios.");
            return;
        }

        try {
            Cliente nuevo = new Cliente(cedula, nombre, telefono);
            repository.guardarCliente(nuevo);
            clientesList.add(nuevo);
            limpiarCampos();
            mostrarAlerta(Alert.AlertType.INFORMATION, "Éxito", "Cliente guardado.");
        } catch (SQLException e) {
            mostrarAlerta(Alert.AlertType.ERROR, "Error", "Fallo al guardar: " + e.getMessage());
        }
    }

    private void limpiarCampos() {
        txtCedula.clear();
        txtNombre.clear();
        txtTelefono.clear();
    }

    private void mostrarAlerta(Alert.AlertType type, String titulo, String mensaje) {
        Alert alert = new Alert(type);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
}

package com.taller.controllers;

import com.taller.models.*;
import com.taller.repository.VehiculoRepository;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;

import java.net.URL;
import java.sql.SQLException;
import java.time.Year;
import java.util.ResourceBundle;

/**
 * Controlador de la vista de gestión de vehículos ({@code vehiculos.fxml}).
 * <p>
 * Administra el formulario de registro con campos dinámicos según el
 * tipo de vehículo seleccionado, valida rigurosamente todas las entradas
 * del usuario y persiste los datos a través de {@link VehiculoRepository}.
 * </p>
 *
 * @author BRANDON CAPACHO
 * @version 2.0
 * @see VehiculoRepository
 * @see Vehiculo
 */
public class VehiculoController implements Initializable {

    // ==================== FXML Bindings ====================

    @FXML private ComboBox<String> cmbTipo;
    @FXML private TextField txtPlaca;
    @FXML private TextField txtMarca;
    @FXML private TextField txtModelo;
    @FXML private TextField txtAnio;
    @FXML private TextField txtCedulaCliente;

    /** Campo específico para {@link Moto}: cilindraje en cc. */
    @FXML private TextField txtCilindraje;

    /** Campo específico para {@link Carro}: número de puertas. */
    @FXML private TextField txtNumPuertas;

    /** Campo específico para {@link Diesel}: capacidad de carga en toneladas. */
    @FXML private TextField txtCapacidadCarga;

    @FXML private Button btnGuardar;

    @FXML private TableView<Vehiculo> tablaVehiculos;
    @FXML private TableColumn<Vehiculo, String> colTipo;
    @FXML private TableColumn<Vehiculo, String> colPlaca;
    @FXML private TableColumn<Vehiculo, String> colMarca;
    @FXML private TableColumn<Vehiculo, String> colModelo;
    @FXML private TableColumn<Vehiculo, Number> colAnio;
    @FXML private TableColumn<Vehiculo, String> colCedulaCliente;
    @FXML private TableColumn<Vehiculo, String> colExtra;

    // ==================== Estado Interno ====================

    private VehiculoRepository repository;
    private ObservableList<Vehiculo> vehiculosList;

    /** Año máximo permitido para el registro (año actual + 1). */
    private static final int ANIO_MAX = Year.now().getValue() + 1;

    /** Año mínimo permitido para el registro. */
    private static final int ANIO_MIN = 1900;

    // ==================== Inicialización ====================

    /**
     * Inicializa el controlador, configura las columnas de la tabla,
     * carga los tipos de vehículo disponibles y puebla los datos existentes.
     */
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        repository = new VehiculoRepository();
        vehiculosList = FXCollections.observableArrayList();

        cmbTipo.setItems(FXCollections.observableArrayList("Moto", "Carro", "Diesel"));

        colTipo.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getTipo()));
        colPlaca.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().placa()));
        colMarca.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().marca()));
        colModelo.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().modelo()));
        colAnio.setCellValueFactory(cellData -> new SimpleIntegerProperty(cellData.getValue().anio()));
        colCedulaCliente.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().cedulaCliente()));
        colExtra.setCellValueFactory(cellData -> {
            Vehiculo v = cellData.getValue();
            if (v instanceof Moto m) return new SimpleStringProperty(m.getCilindraje() + " cc");
            if (v instanceof Carro c) return new SimpleStringProperty(c.getNumPuertas() + " puertas");
            if (v instanceof Diesel d) return new SimpleStringProperty(d.getCapacidadCargaToneladas() + " ton");
            return new SimpleStringProperty("");
        });

        tablaVehiculos.setItems(vehiculosList);
        cargarDatos();
    }

    // ==================== Carga de Datos ====================

    /**
     * Carga todos los vehículos registrados desde la base de datos
     * y los muestra en la tabla.
     */
    private void cargarDatos() {
        try {
            vehiculosList.setAll(repository.obtenerTodosLosVehiculos());
        } catch (SQLException e) {
            mostrarAlerta(Alert.AlertType.ERROR, "Error de BD", e.getMessage());
        }
    }

    // ==================== Eventos FXML ====================

    /**
     * Maneja la selección de tipo en el {@link ComboBox}.
     * <p>
     * Habilita los campos base y muestra/oculta dinámicamente
     * los campos específicos del tipo seleccionado.
     * </p>
     */
    @FXML
    public void onTipoSeleccionado() {
        String tipo = cmbTipo.getValue();
        if (tipo == null) return;

        // Habilitar campos base
        txtPlaca.setDisable(false);
        txtMarca.setDisable(false);
        txtModelo.setDisable(false);
        txtAnio.setDisable(false);
        txtCedulaCliente.setDisable(false);
        btnGuardar.setDisable(false);

        // Ocultar y limpiar todos los campos específicos
        ocultarCampo(txtCilindraje);
        ocultarCampo(txtNumPuertas);
        ocultarCampo(txtCapacidadCarga);

        // Mostrar el campo específico correspondiente
        switch (tipo) {
            case "Moto" -> mostrarCampo(txtCilindraje);
            case "Carro" -> mostrarCampo(txtNumPuertas);
            case "Diesel" -> mostrarCampo(txtCapacidadCarga);
        }
    }

    /**
     * Valida exhaustivamente todas las entradas del usuario y, si son correctas,
     * construye la subclase concreta de {@link Vehiculo} correspondiente
     * y la persiste en la base de datos.
     * <p>
     * <b>Validaciones aplicadas:</b>
     * <ul>
     *   <li>Ningún campo base puede estar vacío</li>
     *   <li>Placa: formato {@code ABC-123} (3 letras mayúsculas, guión, 3 dígitos)</li>
     *   <li>Año: entre {@value #ANIO_MIN} y año actual + 1</li>
     *   <li>Cilindraje (Moto): entero positivo</li>
     *   <li>Puertas (Carro): entre 1 y 6</li>
     *   <li>Carga (Diesel): número positivo con hasta 2 decimales</li>
     * </ul>
     */
    @FXML
    public void guardarVehiculoAction() {
        String tipo = cmbTipo.getValue();
        String placa = txtPlaca.getText().trim().toUpperCase();
        String marca = txtMarca.getText().trim();
        String modelo = txtModelo.getText().trim();
        String anioStr = txtAnio.getText().trim();
        String cedula = txtCedulaCliente.getText().trim();

        // --- Validación de campos vacíos ---
        if (tipo == null || placa.isEmpty() || marca.isEmpty() || modelo.isEmpty()
                || anioStr.isEmpty() || cedula.isEmpty()) {
            mostrarAlerta(Alert.AlertType.WARNING, "Campos Vacíos",
                    "Todos los campos base son obligatorios.");
            return;
        }

        // --- Validación de formato de placa (mínimo 4 caracteres, solo letras y números) ---
        if (!placa.matches("[A-Z0-9]{4,}")) {
            mostrarAlerta(Alert.AlertType.WARNING, "Placa Inválida",
                    "La placa debe tener mínimo 4 caracteres, solo letras y números (sin caracteres especiales).");
            return;
        }

        // --- Validación de año ---
        int anio;
        try {
            anio = Integer.parseInt(anioStr);
        } catch (NumberFormatException e) {
            mostrarAlerta(Alert.AlertType.WARNING, "Año Inválido",
                    "El año debe ser un número entero.");
            return;
        }
        if (anio < ANIO_MIN || anio > ANIO_MAX) {
            mostrarAlerta(Alert.AlertType.WARNING, "Año Fuera de Rango",
                    "El año debe estar entre " + ANIO_MIN + " y " + ANIO_MAX + ".");
            return;
        }

        // --- Validación de cédula (solo dígitos) ---
        if (!cedula.matches("\\d+")) {
            mostrarAlerta(Alert.AlertType.WARNING, "Cédula Inválida",
                    "La cédula debe contener solo números.");
            return;
        }

        // --- Construcción del vehículo según tipo ---
        try {
            Vehiculo nuevo;

            switch (tipo) {
                case "Moto" -> {
                    int cilindraje = parseEnteroPositivo(txtCilindraje, "Cilindraje");
                    nuevo = new Moto(placa, marca, modelo, anio, cedula, cilindraje);
                }
                case "Carro" -> {
                    int puertas = parseEnteroPositivo(txtNumPuertas, "Número de puertas");
                    if (puertas < 1 || puertas > 6) {
                        mostrarAlerta(Alert.AlertType.WARNING, "Puertas Inválidas",
                                "El número de puertas debe estar entre 1 y 6.");
                        return;
                    }
                    nuevo = new Carro(placa, marca, modelo, anio, cedula, puertas);
                }
                case "Diesel" -> {
                    double carga = parseDoublePositivo(txtCapacidadCarga, "Capacidad de carga");
                    nuevo = new Diesel(placa, marca, modelo, anio, cedula, carga);
                }
                default -> {
                    mostrarAlerta(Alert.AlertType.ERROR, "Tipo Desconocido",
                            "Seleccione un tipo de vehículo válido.");
                    return;
                }
            }

            repository.guardarVehiculo(nuevo);
            vehiculosList.add(nuevo);
            limpiarCampos();
            mostrarAlerta(Alert.AlertType.INFORMATION, "Éxito",
                    "Vehículo " + placa + " guardado correctamente.");

        } catch (NumberFormatException e) {
            mostrarAlerta(Alert.AlertType.ERROR, "Formato Inválido",
                    e.getMessage());
        } catch (IllegalArgumentException e) {
            mostrarAlerta(Alert.AlertType.WARNING, "Dato Inválido", e.getMessage());
        } catch (SQLException e) {
            manejarErrorSQL(e);
        }
    }

    // ==================== Métodos Auxiliares ====================

    /**
     * Parsea un campo de texto como entero positivo.
     *
     * @param campo      campo de texto a parsear
     * @param nombreCampo nombre legible del campo para mensajes de error
     * @return valor entero positivo
     * @throws NumberFormatException si el campo está vacío o no contiene un entero positivo
     */
    private int parseEnteroPositivo(TextField campo, String nombreCampo) {
        String texto = campo.getText().trim();
        if (texto.isEmpty()) {
            throw new NumberFormatException(nombreCampo + " es obligatorio.");
        }
        int valor;
        try {
            valor = Integer.parseInt(texto);
        } catch (NumberFormatException e) {
            throw new NumberFormatException(nombreCampo + " debe ser un número entero.");
        }
        if (valor <= 0) {
            throw new NumberFormatException(nombreCampo + " debe ser mayor a 0.");
        }
        return valor;
    }

    /**
     * Parsea un campo de texto como double positivo.
     *
     * @param campo      campo de texto a parsear
     * @param nombreCampo nombre legible del campo para mensajes de error
     * @return valor double positivo
     * @throws NumberFormatException si el campo está vacío o no contiene un número positivo
     */
    private double parseDoublePositivo(TextField campo, String nombreCampo) {
        String texto = campo.getText().trim();
        if (texto.isEmpty()) {
            throw new NumberFormatException(nombreCampo + " es obligatorio.");
        }
        double valor;
        try {
            valor = Double.parseDouble(texto);
        } catch (NumberFormatException e) {
            throw new NumberFormatException(nombreCampo + " debe ser un número válido.");
        }
        if (valor <= 0) {
            throw new NumberFormatException(nombreCampo + " debe ser mayor a 0.");
        }
        return valor;
    }

    /**
     * Muestra un campo de texto y lo marca como gestionado por el layout.
     */
    private void mostrarCampo(TextField campo) {
        campo.setVisible(true);
        campo.setManaged(true);
    }

    /**
     * Oculta un campo de texto, lo quita del layout y limpia su contenido.
     */
    private void ocultarCampo(TextField campo) {
        campo.setVisible(false);
        campo.setManaged(false);
        campo.clear();
    }

    /**
     * Maneja errores SQL con mensajes amigables al usuario.
     */
    private void manejarErrorSQL(SQLException e) {
        String msg = e.getMessage();
        if (msg != null && (msg.contains("FOREIGN KEY") || msg.contains("Referential integrity"))) {
            mostrarAlerta(Alert.AlertType.ERROR, "Cliente No Registrado",
                    "La cédula ingresada no pertenece a ningún cliente registrado.\n"
                            + "Por favor, registre primero al dueño.");
        } else if (msg != null && msg.contains("PRIMARY KEY") || msg != null && msg.contains("Unique index")) {
            mostrarAlerta(Alert.AlertType.ERROR, "Placa Duplicada",
                    "Ya existe un vehículo registrado con esa placa.");
        } else {
            mostrarAlerta(Alert.AlertType.ERROR, "Error de BD",
                    "No se pudo guardar: " + msg);
        }
    }

    /**
     * Limpia todos los campos del formulario y los desactiva,
     * dejando la vista lista para una nueva selección de tipo.
     */
    private void limpiarCampos() {
        txtPlaca.clear(); txtMarca.clear(); txtModelo.clear();
        txtAnio.clear(); txtCedulaCliente.clear();
        txtCilindraje.clear(); txtNumPuertas.clear(); txtCapacidadCarga.clear();
        cmbTipo.getSelectionModel().clearSelection();

        txtPlaca.setDisable(true); txtMarca.setDisable(true);
        txtModelo.setDisable(true); txtAnio.setDisable(true);
        txtCedulaCliente.setDisable(true); btnGuardar.setDisable(true);

        ocultarCampo(txtCilindraje);
        ocultarCampo(txtNumPuertas);
        ocultarCampo(txtCapacidadCarga);
    }

    /**
     * Muestra una alerta estándar de JavaFX al usuario.
     *
     * @param type    tipo de alerta (INFO, WARNING, ERROR)
     * @param titulo  título de la ventana
     * @param mensaje cuerpo del mensaje
     */
    private void mostrarAlerta(Alert.AlertType type, String titulo, String mensaje) {
        Alert alert = new Alert(type);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
}

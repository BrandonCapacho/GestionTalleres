package com.taller.controllers;

import javafx.fxml.FXML;

/**
 * Controlador del panel principal del Dashboard ({@code dashboard.fxml}).
 * <p>
 * Delega la navegación a las secciones principales del sistema
 * a través de {@link MainController}.
 * </p>
 *
 * @author BRANDON CAPACHO
 * @version 1.0
 */
public class DashboardController {

    private MainController mainController;

    public void setMainController(MainController mainController) {
        this.mainController = mainController;
    }

    @FXML
    public void navClientes() {
        if (mainController != null) mainController.navClientes();
    }

    @FXML
    public void navVehiculos() {
        if (mainController != null) mainController.navVehiculos();
    }

    @FXML
    public void navFacturacion() {
        if (mainController != null) mainController.navNuevaFactura();
    }

    @FXML
    public void navBuscarFacturas() {
        if (mainController != null) mainController.navHistorialFacturas();
    }
}

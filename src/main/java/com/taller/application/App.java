package com.taller.application;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * Clase principal de la aplicación JavaFX.
 * <p>
 * Carga la vista principal ({@code main.fxml}), aplica el tema oscuro
 * global ({@code dark-theme.css}) y lanza la ventana del sistema.
 * </p>
 *
 * @author BRANDON CAPACHO
 * @version 2.0
 */
public class App extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/taller/views/main.fxml"));
        Parent root = loader.load();
        
        primaryStage.setTitle("Sistema de Gestión - Taller Mecánico");
        Scene scene = new Scene(root, 1100, 700);
        scene.getStylesheets().add(getClass().getResource("/com/taller/styles/dark-theme.css").toExternalForm());
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}

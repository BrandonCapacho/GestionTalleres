package com.taller.application;

/**
 * Punto de entrada alternativo para la aplicación.
 * <p>
 * Necesario para ejecutar la aplicación JavaFX desde un JAR empaquetado
 * sin depender de la clase {@link App} directamente, evitando el error
 * {@code JavaFX runtime components are missing}.
 * </p>
 *
 * @author BRANDON CAPACHO
 * @version 1.0
 */
public class Launcher {
    public static void main(String[] args) {
        App.main(args);
    }
}

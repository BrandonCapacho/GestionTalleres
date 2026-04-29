package com.taller.models;

/**
 * Clase base abstracta para todos los tipos de vehículo del taller.
 * <p>
 * Implementa el patrón <b>Single Table Inheritance</b> donde cada subclase
 * concreta ({@link Moto}, {@link Carro}, {@link Diesel}) define su propio
 * tipo y campos adicionales específicos.
 * </p>
 *
 * @author BRANDON CAPACHO
 * @version 2.0
 */
public abstract class Vehiculo {

    /** Placa única del vehículo (formato ABC-123). */
    private final String placa;

    /** Marca del fabricante (ej. Toyota, Chevrolet). */
    private final String marca;

    /** Modelo comercial del vehículo (ej. Corolla, Spark). */
    private final String modelo;

    /** Año de fabricación del vehículo. */
    private final int anio;

    /** Cédula del cliente propietario del vehículo. */
    private final String cedulaCliente;

    /**
     * Constructor base para cualquier tipo de vehículo.
     *
     * @param placa         placa única del vehículo
     * @param marca         marca del fabricante
     * @param modelo        modelo comercial
     * @param anio          año de fabricación
     * @param cedulaCliente cédula del dueño registrado en el sistema
     * @throws IllegalArgumentException si placa, marca, modelo o cédula son nulos/vacíos
     */
    public Vehiculo(String placa, String marca, String modelo, int anio, String cedulaCliente) {
        if (placa == null || placa.isBlank()) throw new IllegalArgumentException("La placa no puede estar vacía.");
        if (marca == null || marca.isBlank()) throw new IllegalArgumentException("La marca no puede estar vacía.");
        if (modelo == null || modelo.isBlank()) throw new IllegalArgumentException("El modelo no puede estar vacío.");
        if (cedulaCliente == null || cedulaCliente.isBlank()) throw new IllegalArgumentException("La cédula del cliente no puede estar vacía.");
        this.placa = placa;
        this.marca = marca;
        this.modelo = modelo;
        this.anio = anio;
        this.cedulaCliente = cedulaCliente;
    }

    /**
     * Retorna el tipo concreto de vehículo como cadena de texto.
     * Cada subclase debe implementar este método.
     *
     * @return nombre del tipo de vehículo (ej. "Moto", "Carro", "Diesel")
     */
    public abstract String getTipo();

    /** @return placa del vehículo */
    public String placa() { return placa; }

    /** @return marca del fabricante */
    public String marca() { return marca; }

    /** @return modelo comercial */
    public String modelo() { return modelo; }

    /** @return año de fabricación */
    public int anio() { return anio; }

    /** @return cédula del dueño */
    public String cedulaCliente() { return cedulaCliente; }
}

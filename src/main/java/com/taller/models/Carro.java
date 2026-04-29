package com.taller.models;

/**
 * Representa un vehículo de tipo Automóvil (Carro).
 * <p>
 * Extiende {@link Vehiculo} añadiendo el número de puertas
 * como atributo específico de este tipo de vehículo.
 * </p>
 *
 * @author BRANDON CAPACHO
 * @version 1.1
 * @see Vehiculo
 */
public class Carro extends Vehiculo {

    /** Número de puertas del automóvil (típicamente 2-6). */
    private final int numPuertas;

    /**
     * Construye una nueva instancia de Automóvil.
     *
     * @param placa         placa única del vehículo
     * @param marca         marca del fabricante
     * @param modelo        modelo comercial
     * @param anio          año de fabricación
     * @param cedulaCliente cédula del dueño registrado
     * @param numPuertas    número de puertas (1-6)
     * @throws IllegalArgumentException si el número de puertas es &lt; 1 o &gt; 6
     */
    public Carro(String placa, String marca, String modelo, int anio,
                 String cedulaCliente, int numPuertas) {
        super(placa, marca, modelo, anio, cedulaCliente);
        if (numPuertas < 1 || numPuertas > 6) {
            throw new IllegalArgumentException("El número de puertas debe estar entre 1 y 6.");
        }
        this.numPuertas = numPuertas;
    }

    /** {@inheritDoc} */
    @Override
    public String getTipo() {
        return "Carro";
    }

    /**
     * Obtiene el número de puertas del automóvil.
     *
     * @return cantidad de puertas
     */
    public int getNumPuertas() {
        return numPuertas;
    }
}

package com.taller.models;

/**
 * Representa un vehículo de tipo Motocicleta.
 * <p>
 * Extiende {@link Vehiculo} añadiendo el cilindraje del motor (en cc)
 * como atributo específico de este tipo de vehículo ligero.
 * </p>
 *
 * @author BRANDON CAPACHO
 * @version 1.1
 * @see Vehiculo
 */
public class Moto extends Vehiculo {

    /** Cilindraje del motor medido en centímetros cúbicos (cc). */
    private final int cilindraje;

    /**
     * Construye una nueva instancia de Motocicleta.
     *
     * @param placa         placa única del vehículo
     * @param marca         marca del fabricante
     * @param modelo        modelo comercial
     * @param anio          año de fabricación
     * @param cedulaCliente cédula del dueño registrado
     * @param cilindraje    cilindraje del motor en cc (&gt; 0)
     * @throws IllegalArgumentException si el cilindraje es &le; 0
     */
    public Moto(String placa, String marca, String modelo, int anio,
                String cedulaCliente, int cilindraje) {
        super(placa, marca, modelo, anio, cedulaCliente);
        if (cilindraje <= 0) {
            throw new IllegalArgumentException("El cilindraje debe ser mayor a 0.");
        }
        this.cilindraje = cilindraje;
    }

    /** {@inheritDoc} */
    @Override
    public String getTipo() {
        return "Moto";
    }

    /**
     * Obtiene el cilindraje del motor.
     *
     * @return cilindraje en centímetros cúbicos
     */
    public int getCilindraje() {
        return cilindraje;
    }
}

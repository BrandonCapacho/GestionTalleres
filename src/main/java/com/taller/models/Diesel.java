package com.taller.models;

/**
 * Representa un vehículo de tipo Diésel (camión, tractomula, etc.).
 * <p>
 * Extiende {@link Vehiculo} añadiendo la capacidad de carga en toneladas
 * como atributo específico de este tipo de vehículo pesado.
 * </p>
 *
 * @author BRANDON CAPACHO
 * @version 1.0
 * @see Vehiculo
 */
public class Diesel extends Vehiculo {

    /** Capacidad máxima de carga del vehículo medida en toneladas. */
    private final double capacidadCargaToneladas;

    /**
     * Construye una nueva instancia de vehículo Diésel.
     *
     * @param placa                   placa única del vehículo (formato ABC-123)
     * @param marca                   marca del fabricante
     * @param modelo                  modelo comercial
     * @param anio                    año de fabricación
     * @param cedulaCliente           cédula del dueño registrado
     * @param capacidadCargaToneladas capacidad de carga en toneladas (&gt; 0)
     * @throws IllegalArgumentException si la capacidad de carga es &le; 0
     */
    public Diesel(String placa, String marca, String modelo, int anio,
                  String cedulaCliente, double capacidadCargaToneladas) {
        super(placa, marca, modelo, anio, cedulaCliente);
        if (capacidadCargaToneladas <= 0) {
            throw new IllegalArgumentException("La capacidad de carga debe ser mayor a 0.");
        }
        this.capacidadCargaToneladas = capacidadCargaToneladas;
    }

    /**
     * {@inheritDoc}
     * @return la cadena {@code "Diesel"}
     */
    @Override
    public String getTipo() {
        return "Diesel";
    }

    /**
     * Obtiene la capacidad de carga del vehículo en toneladas.
     *
     * @return capacidad de carga en toneladas
     */
    public double getCapacidadCargaToneladas() {
        return capacidadCargaToneladas;
    }
}

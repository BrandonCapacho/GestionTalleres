package com.taller.models;

/**
 * Registro inmutable que representa un Repuesto utilizado en un servicio.
 * <p>
 * Cada repuesto está vinculado a una {@link Factura} mediante su {@code facturaId}.
 * </p>
 *
 * @param id        identificador único auto-generado
 * @param facturaId identificador de la factura a la que pertenece
 * @param nombre    descripción o nombre del repuesto
 * @param precio    precio unitario del repuesto en pesos
 *
 * @author BRANDON CAPACHO
 * @version 1.0
 * @see Factura
 */
public record Repuesto(int id, int facturaId, String nombre, double precio) {
}

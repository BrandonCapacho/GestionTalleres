package com.taller.models;

import java.sql.Timestamp;
import java.util.List;

/**
 * Registro inmutable que representa una Factura de servicio del taller.
 * <p>
 * Contiene la información completa de un servicio realizado: vehículo atendido,
 * costo de mano de obra, lista de repuestos utilizados y total facturado.
 * </p>
 *
 * @param id             identificador único auto-generado
 * @param fecha          fecha y hora de emisión de la factura
 * @param placaVehiculo  placa del vehículo atendido
 * @param manoObra       costo de la mano de obra en pesos
 * @param total          monto total de la factura (mano de obra + repuestos)
 * @param repuestos      lista de repuestos utilizados en el servicio
 *
 * @author BRANDON CAPACHO
 * @version 1.0
 * @see Repuesto
 */
public record Factura(int id, Timestamp fecha, String placaVehiculo,
                      double manoObra, double total, List<Repuesto> repuestos) {
}

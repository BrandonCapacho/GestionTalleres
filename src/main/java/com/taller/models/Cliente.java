package com.taller.models;

/**
 * Registro inmutable que representa un Cliente del taller.
 * <p>
 * Almacena los datos personales del propietario de uno o más vehículos.
 * Utiliza {@code record} de Java para garantizar inmutabilidad y generar
 * automáticamente {@code equals()}, {@code hashCode()} y {@code toString()}.
 * </p>
 *
 * @param cedula   cédula de identidad del cliente (clave primaria)
 * @param nombre   nombre completo del cliente
 * @param telefono número de teléfono de contacto
 *
 * @author BRANDON CAPACHO
 * @version 1.0
 */
public record Cliente(String cedula, String nombre, String telefono) {

}

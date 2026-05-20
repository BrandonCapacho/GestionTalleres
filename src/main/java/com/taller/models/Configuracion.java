package com.taller.models;

/**
 * Registro inmutable que almacena la configuración global del taller.
 * <p>
 * Permite personalizar el nombre del taller, la ruta del logotipo
 * y la clave de acceso desde la pantalla de Configuración.
 * </p>
 *
 * @param id            identificador fijo (siempre 1, registro único)
 * @param nombreTaller  nombre comercial del taller
 * @param logoPath      ruta absoluta o URL al logotipo del taller
 * @param claveAcceso   contraseña de acceso al sistema
 *
 * @author BRANDON CAPACHO
 * @version 1.0
 */
public record Configuracion(int id, String nombreTaller, String logoPath, String claveAcceso, String tema) {

}

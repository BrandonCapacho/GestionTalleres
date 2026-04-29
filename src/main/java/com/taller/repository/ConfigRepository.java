package com.taller.repository;

import com.taller.models.Configuracion;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Repositorio de acceso a datos para la tabla {@code configuracion}.
 * <p>
 * Administra la lectura y actualización de los ajustes globales del taller
 * (nombre, logotipo, clave de acceso).
 * </p>
 *
 * @author BRANDON CAPACHO
 * @version 1.0
 * @see com.taller.models.Configuracion
 */
public class ConfigRepository {
    public Configuracion obtenerConfiguracion() throws SQLException {
        String query = "SELECT id, nombre_taller, logo_path, clave_acceso FROM configuracion WHERE id = 1";
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query);
             ResultSet rs = pstmt.executeQuery()) {
            if (rs.next()) {
                return new Configuracion(
                        rs.getInt("id"),
                        rs.getString("nombre_taller"),
                        rs.getString("logo_path"),
                        rs.getString("clave_acceso")
                );
            }
        }
        return null;
    }

    public void actualizarConfiguracion(Configuracion config) throws SQLException {
        String query = "UPDATE configuracion SET nombre_taller = ?, logo_path = ?, clave_acceso = ? WHERE id = 1";
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setString(1, config.nombreTaller());
            pstmt.setString(2, config.logoPath());
            pstmt.setString(3, config.claveAcceso());
            pstmt.executeUpdate();
        }
    }
}

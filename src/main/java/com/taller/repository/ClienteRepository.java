package com.taller.repository;

import com.taller.models.Cliente;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Repositorio de acceso a datos para la tabla {@code clientes}.
 * <p>
 * Proporciona operaciones para registrar y consultar clientes del taller.
 * </p>
 *
 * @author BRANDON CAPACHO
 * @version 1.0
 * @see com.taller.models.Cliente
 */
public class ClienteRepository {
    public void guardarCliente(Cliente c) throws SQLException {
        String query = "INSERT INTO clientes (cedula, nombre, telefono) VALUES (?, ?, ?)";
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setString(1, c.cedula());
            pstmt.setString(2, c.nombre());
            pstmt.setString(3, c.telefono());
            pstmt.executeUpdate();
        }
    }

    public List<Cliente> obtenerTodosLosClientes() throws SQLException {
        List<Cliente> clientes = new ArrayList<>();
        String query = "SELECT cedula, nombre, telefono FROM clientes";
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query);
             ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                clientes.add(new Cliente(
                        rs.getString("cedula"),
                        rs.getString("nombre"),
                        rs.getString("telefono")
                ));
            }
        }
        return clientes;
    }
}

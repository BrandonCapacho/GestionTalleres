package com.taller.repository;

import com.taller.models.Factura;
import com.taller.models.Repuesto;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 * Repositorio de acceso a datos para las tablas {@code facturas} y {@code repuestos}.
 * <p>
 * Gestiona la persistencia transaccional de facturas con sus repuestos asociados,
 * así como las consultas de búsqueda por placa y por cédula del cliente.
 * </p>
 *
 * @author BRANDON CAPACHO
 * @version 1.0
 * @see com.taller.models.Factura
 * @see com.taller.models.Repuesto
 */
public class FacturaRepository {
    public int guardarFacturaConRepuestos(Factura f) throws SQLException {
        String insertFactura = "INSERT INTO facturas (placa_vehiculo, mano_obra, total) VALUES (?, ?, ?)";
        String insertRepuesto = "INSERT INTO repuestos (factura_id, nombre, precio) VALUES (?, ?, ?)";
        
        Connection conn = null;
        try {
            conn = DatabaseConnection.getInstance().getConnection();
            conn.setAutoCommit(false); // Transacción para asegurar repuestos y facturas
            
            int facturaId = -1;
            try (PreparedStatement pstmtFactura = conn.prepareStatement(insertFactura, Statement.RETURN_GENERATED_KEYS)) {
                pstmtFactura.setString(1, f.placaVehiculo());
                pstmtFactura.setDouble(2, f.manoObra());
                pstmtFactura.setDouble(3, f.total());
                pstmtFactura.executeUpdate();
                
                try (ResultSet generatedKeys = pstmtFactura.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        facturaId = generatedKeys.getInt(1);
                    }
                }
            }
            
            if (facturaId != -1 && f.repuestos() != null && !f.repuestos().isEmpty()) {
                try (PreparedStatement pstmtRepuesto = conn.prepareStatement(insertRepuesto)) {
                    for (Repuesto r : f.repuestos()) {
                        pstmtRepuesto.setInt(1, facturaId);
                        pstmtRepuesto.setString(2, r.nombre());
                        pstmtRepuesto.setDouble(3, r.precio());
                        pstmtRepuesto.addBatch();
                    }
                    pstmtRepuesto.executeBatch();
                }
            }
            
            conn.commit();
            return facturaId;
        } catch (SQLException e) {
            if (conn != null) {
                conn.rollback();
            }
            throw e;
        } finally {
            if (conn != null) {
                conn.setAutoCommit(true);
                conn.close();
            }
        }
    }

    public List<Factura> buscarFacturasPorCedula(String cedula) throws SQLException {
        String query = "SELECT f.id, f.fecha, f.placa_vehiculo, f.mano_obra, f.total " +
                       "FROM facturas f JOIN vehiculos v ON f.placa_vehiculo = v.placa " +
                       "WHERE v.cedula_cliente = ?";
        return ejecutarBusquedaFacturas(query, cedula);
    }
    
    public List<Factura> buscarFacturasPorPlaca(String placa) throws SQLException {
        String query = "SELECT id, fecha, placa_vehiculo, mano_obra, total FROM facturas WHERE placa_vehiculo = ?";
        return ejecutarBusquedaFacturas(query, placa);
    }

    private List<Factura> ejecutarBusquedaFacturas(String query, String param) throws SQLException {
        List<Factura> facturas = new ArrayList<>();
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setString(1, param);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    int id = rs.getInt("id");
                    facturas.add(new Factura(
                        id,
                        rs.getTimestamp("fecha"),
                        rs.getString("placa_vehiculo"),
                        rs.getDouble("mano_obra"),
                        rs.getDouble("total"),
                        obtenerRepuestosPorFactura(id, conn)
                    ));
                }
            }
        }
        return facturas;
    }

    private List<Repuesto> obtenerRepuestosPorFactura(int facturaId, Connection conn) throws SQLException {
        List<Repuesto> repuestos = new ArrayList<>();
        String query = "SELECT id, nombre, precio FROM repuestos WHERE factura_id = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setInt(1, facturaId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    repuestos.add(new Repuesto(
                        rs.getInt("id"),
                        facturaId,
                        rs.getString("nombre"),
                        rs.getDouble("precio")
                    ));
                }
            }
        }
        return repuestos;
    }
}

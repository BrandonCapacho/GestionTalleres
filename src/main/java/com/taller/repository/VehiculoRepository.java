package com.taller.repository;

import com.taller.models.Vehiculo;
import com.taller.models.Moto;
import com.taller.models.Carro;
import com.taller.models.Diesel;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Repositorio de acceso a datos para la tabla {@code vehiculos}.
 * <p>
 * Implementa operaciones CRUD utilizando el patrón <b>Single Table Inheritance</b>
 * para persistir los distintos tipos de vehículo ({@link Moto}, {@link Carro},
 * {@link Diesel}) en una única tabla con columnas opcionales.
 * </p>
 *
 * @author BRANDON CAPACHO
 * @version 2.0
 * @see Vehiculo
 * @see DatabaseConnection
 */
public class VehiculoRepository {

    /**
     * Persiste un vehículo en la base de datos.
     * <p>
     * Detecta el tipo concreto mediante {@code instanceof} para extraer los
     * campos específicos de cada subclase.
     * </p>
     *9
     * @param v instancia concreta de {@link Vehiculo} a guardar
     * @throws SQLException si ocurre un error de conexión o restricción de integridad
     */
    public void guardarVehiculo(Vehiculo v) throws SQLException {
        String query = "INSERT INTO vehiculos (placa, marca, modelo, anio, cedula_cliente, tipo, cilindraje, num_puertas, capacidad_carga_ton) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setString(1, v.placa());
            pstmt.setString(2, v.marca());
            pstmt.setString(3, v.modelo());
            pstmt.setInt(4, v.anio());
            pstmt.setString(5, v.cedulaCliente());
            pstmt.setString(6, v.getTipo());

            int cilindraje = 0, numPuertas = 0;
            double cargaTon = 0;

            if (v instanceof Moto m) {
                cilindraje = m.getCilindraje();
            } else if (v instanceof Carro c) {
                numPuertas = c.getNumPuertas();
            } else if (v instanceof Diesel d) {
                cargaTon = d.getCapacidadCargaToneladas();
            }

            pstmt.setInt(7, cilindraje);
            pstmt.setInt(8, numPuertas);
            pstmt.setDouble(9, cargaTon);

            pstmt.executeUpdate();
        }
    }

    /**
     * Obtiene todos los vehículos registrados en la base de datos.
     * <p>
     * Reconstruye la instancia concreta correcta según la columna {@code tipo}
     * almacenada en cada registro.
     * </p>
     *
     * @return lista de vehículos (puede estar vacía, nunca {@code null})
     * @throws SQLException si ocurre un error de conexión
     */
    public List<Vehiculo> obtenerTodosLosVehiculos() throws SQLException {
        List<Vehiculo> vehiculos = new ArrayList<>();
        String query = "SELECT placa, marca, modelo, anio, cedula_cliente, tipo, cilindraje, num_puertas, capacidad_carga_ton FROM vehiculos";
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query);
             ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                String placa = rs.getString("placa");
                String marca = rs.getString("marca");
                String modelo = rs.getString("modelo");
                int anio = rs.getInt("anio");
                String cedula = rs.getString("cedula_cliente");
                String tipo = rs.getString("tipo");

                Vehiculo v;
                switch (tipo != null ? tipo : "") {
                    case "Moto":
                        v = new Moto(placa, marca, modelo, anio, cedula, rs.getInt("cilindraje"));
                        break;
                    case "Carro":
                        v = new Carro(placa, marca, modelo, anio, cedula, rs.getInt("num_puertas"));
                        break;
                    case "Diesel":
                        v = new Diesel(placa, marca, modelo, anio, cedula, rs.getDouble("capacidad_carga_ton"));
                        break;
                    default:
                        // Fallback seguro para registros antiguos o desconocidos
                        v = new Carro(placa, marca, modelo, anio, cedula, 4);
                        break;
                }
                vehiculos.add(v);
            }
        }
        return vehiculos;
    }
}

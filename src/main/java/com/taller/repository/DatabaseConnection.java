package com.taller.repository;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Conexión Singleton thread-safe a la base de datos H2 del taller.
 * <p>
 * Administra la creación de tablas, migraciones de esquema y la provisión
 * de conexiones JDBC a los repositorios del sistema.
 * </p>
 *
 * @author BRANDON CAPACHO
 * @version 2.0
 */
public class DatabaseConnection {

    private static DatabaseConnection instance;
    private final String url = "jdbc:h2:./taller_db;DB_CLOSE_DELAY=-1;AUTO_SERVER=TRUE";
    private final String user;
    private final String password;

    private DatabaseConnection() {
        String envUser = System.getenv("DB_USER");
        String envPassword = System.getenv("DB_PASSWORD");
        
        this.user = (envUser != null && !envUser.isBlank()) ? envUser : "sa";
        this.password = (envPassword != null) ? envPassword : "";
        
        inicializarBaseDatos();
    }

    /**
     * Obtiene la instancia única de la conexión (patrón Singleton thread-safe).
     *
     * @return instancia de {@code DatabaseConnection}
     */
    public static synchronized DatabaseConnection getInstance() {
        if (instance == null) {
            instance = new DatabaseConnection();
        }
        return instance;
    }

    /**
     * Crea y retorna una nueva conexión JDBC a la base de datos.
     *
     * @return conexión activa al motor H2
     * @throws SQLException si no se puede establecer la conexión
     */
    public Connection getConnection() throws SQLException {
        return DriverManager.getConnection(url, user, password);
    }
    
    /**
     * Inicializa el esquema de la base de datos creando las tablas necesarias
     * y aplicando migraciones de columnas si es necesario.
     */
    private void inicializarBaseDatos() {
        String createConfig = "CREATE TABLE IF NOT EXISTS configuracion ("
                + "id INT PRIMARY KEY,"
                + "nombre_taller VARCHAR(100),"
                + "logo_path VARCHAR(255),"
                + "clave_acceso VARCHAR(255)"
                + ");";
        String createClientes = "CREATE TABLE IF NOT EXISTS clientes ("
                + "cedula VARCHAR(20) PRIMARY KEY,"
                + "nombre VARCHAR(100) NOT NULL,"
                + "telefono VARCHAR(20)"
                + ");";
        String createVehiculos = "CREATE TABLE IF NOT EXISTS vehiculos ("
                + "placa VARCHAR(20) PRIMARY KEY,"
                + "marca VARCHAR(50) NOT NULL,"
                + "modelo VARCHAR(50) NOT NULL,"
                + "anio INT NOT NULL"
                + ");";
        String createFacturas = "CREATE TABLE IF NOT EXISTS facturas ("
                + "id INT AUTO_INCREMENT PRIMARY KEY,"
                + "fecha TIMESTAMP DEFAULT CURRENT_TIMESTAMP,"
                + "placa_vehiculo VARCHAR(20),"
                + "mano_obra DOUBLE,"
                + "total DOUBLE,"
                + "FOREIGN KEY (placa_vehiculo) REFERENCES vehiculos(placa) ON DELETE CASCADE"
                + ");";
        String createRepuestos = "CREATE TABLE IF NOT EXISTS repuestos ("
                + "id INT AUTO_INCREMENT PRIMARY KEY,"
                + "factura_id INT,"
                + "nombre VARCHAR(100),"
                + "precio DOUBLE,"
                + "FOREIGN KEY (factura_id) REFERENCES facturas(id) ON DELETE CASCADE"
                + ");";
        
        try (Connection conn = getConnection();
             java.sql.Statement stmt = conn.createStatement()) {
            
            stmt.execute(createConfig);
            // Configurar default
            try (ResultSet rs = stmt.executeQuery("SELECT count(*) FROM configuracion")) {
                if (rs.next() && rs.getInt(1) == 0) {
                    stmt.execute("INSERT INTO configuracion (id, nombre_taller, logo_path, clave_acceso) VALUES (1, 'Mi Taller Mecánico', '', 'admin')");
                }
            }

            stmt.execute(createClientes);
            stmt.execute(createVehiculos);
            
            try {
                // Compatibilidad si la tabla vehículos no tenía la foreign key cedula_cliente u otras columnas nuevas
                stmt.execute("ALTER TABLE vehiculos ADD COLUMN IF NOT EXISTS cedula_cliente VARCHAR(20)");
                stmt.execute("ALTER TABLE vehiculos ADD FOREIGN KEY (cedula_cliente) REFERENCES clientes(cedula) ON DELETE SET NULL");
                
                stmt.execute("ALTER TABLE vehiculos ADD COLUMN IF NOT EXISTS tipo VARCHAR(20) DEFAULT 'Desconocido'");
                stmt.execute("ALTER TABLE vehiculos ADD COLUMN IF NOT EXISTS cilindraje INT DEFAULT 0");
                stmt.execute("ALTER TABLE vehiculos ADD COLUMN IF NOT EXISTS num_puertas INT DEFAULT 0");
                stmt.execute("ALTER TABLE vehiculos ADD COLUMN IF NOT EXISTS capacidad_carga_ton DOUBLE DEFAULT 0");
            } catch (SQLException ignored) {
                // Silencioso. Ya puede estar creada la columna de un build anterior o no importaba.
            }
            
            stmt.execute(createFacturas);
            stmt.execute(createRepuestos);
            
        } catch (SQLException e) {
            System.err.println("Error al inicializar la base de datos: " + e.getMessage());
        }
    }
}

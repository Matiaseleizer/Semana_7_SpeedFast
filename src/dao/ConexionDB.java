package dao; // O package conexion; según tu estructura de carpetas

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConexionDB {

    // Configuración de la conexión a MySQL
    private static final String URL = "jdbc:mysql://localhost:3306/speedfast_db?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC";
    private static final String USER = "root";
    private static final String PASSWORD = "Mokoloko21.";

    /**
     * Establece y retorna una conexión activa a la base de datos MySQL.
     * @return Connection objeto de conexión
     * @throws SQLException si ocurre un error al conectar con la base de datos
     */
    public static Connection conectar() throws SQLException {
        try {
            // Cargar explícitamente el driver de MySQL
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            throw new SQLException("Error: Driver JDBC de MySQL no encontrado. Verifica que el archivo .jar esté en las librerías del proyecto.", e);
        }
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }
}



package dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConexionDB {

    // Configuración de la conexión a MySQL

    private static final String URL = "jdbc:mysql://localhost:3306/speedfast_db?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC";
    private static final String USER = "root";
    private static final String PASSWORD = "Mokoloko21.";

    public static Connection conectar() throws SQLException {
        try {

            // Cargar explícitamente el driver de MySQL

            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            System.err.println("Driver JDBC de MySQL no encontrado. Revisa la carpeta lib.");
        }
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }

}



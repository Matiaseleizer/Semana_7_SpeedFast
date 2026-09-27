package dao;

import modelo.Repartidor;
import modelo.ZonaDeCarga;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class RepartidorDAO {

    // Guarda un nuevo repartidor en la base de datos MySQL
    public boolean guardar(Repartidor repartidor) {
        String sql = "INSERT INTO repartidor (nombre) VALUES (?)";

        try (Connection conn = ConexionDB.conectar();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, repartidor.getNombre());

            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error al guardar repartidor: " + e.getMessage());
            return false;
        }
    }

    // Lista todos los repartidores desde la BD utilizando ResultSet
    public List<Repartidor> listarTodos(ZonaDeCarga zonaDeCarga) {
        List<Repartidor> lista = new ArrayList<>();
        String sql = "SELECT * FROM repartidor";

        try (Connection conn = ConexionDB.conectar();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                int id = rs.getInt("id");
                String nombre = rs.getString("nombre");

                // Creamos la instancia con el constructor existente
                Repartidor r = new Repartidor(nombre, zonaDeCarga);
                r.setId(id);
                lista.add(r);
            }
        } catch (SQLException e) {
            System.err.println("Error al listar repartidores: " + e.getMessage());
        }
        return lista;
    }
}

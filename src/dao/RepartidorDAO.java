package dao;


import modelo.Repartidor;
import modelo.ZonaDeCarga;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class RepartidorDAO {

    /**
     * Guarda un nuevo repartidor en la base de datos MySQL.
     */
    public boolean guardar(Repartidor repartidor) throws SQLException {
        String sql = "INSERT INTO repartidores (nombre) VALUES (?)";

        try (Connection conn = ConexionDB.conectar();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, repartidor.getNombre());
            return pstmt.executeUpdate() > 0;
        }
    }

    /**
     * Lista todos los repartidores
     */
    public List<Repartidor> listarTodos() throws SQLException {
        return listarTodos(null);
    }

    /**
     * Sobrecarga de listarTodos
     */
    public List<Repartidor> listarTodos(ZonaDeCarga zonaDeCarga) throws SQLException {
        List<Repartidor> lista = new ArrayList<>();
        String sql = "SELECT * FROM repartidores";

        try (Connection conn = ConexionDB.conectar();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                int id = rs.getInt("id");
                String nombre = rs.getString("nombre");

                Repartidor r;
                if (zonaDeCarga != null) {
                    r = new Repartidor(nombre, zonaDeCarga);
                } else {
                    r = new Repartidor(nombre);
                }
                r.setId(id);
                lista.add(r);
            }
        }
        return lista;
    }

    /**
     * Actualiza el nombre de un repartidor existente.
     */
    public boolean actualizar(Repartidor repartidor) throws SQLException {
        String sql = "UPDATE repartidores SET nombre = ? WHERE id = ?";

        try (Connection conn = ConexionDB.conectar();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, repartidor.getNombre());
            pstmt.setInt(2, repartidor.getId());

            return pstmt.executeUpdate() > 0;
        }
    }

    /**
     * Elimina un repartidor por su ID.
     */
    public boolean eliminar(int id) throws SQLException {
        String sql = "DELETE FROM repartidores WHERE id = ?";

        try (Connection conn = ConexionDB.conectar();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, id);
            return pstmt.executeUpdate() > 0;
        }
    }
}

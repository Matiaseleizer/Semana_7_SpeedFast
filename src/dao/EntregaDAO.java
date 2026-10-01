package dao;


import modelo.Entrega;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class EntregaDAO {

    /**
     * Guarda el registro de la entrega asociando el pedido con el repartidor.
     */
    public boolean guardar(Entrega entrega) throws SQLException {
        String sql = "INSERT INTO entregas (id_pedido, id_repartidor, fecha, hora) VALUES (?, ?, ?, ?)";

        try (Connection conn = ConexionDB.conectar();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, entrega.getIdPedido());
            pstmt.setInt(2, entrega.getIdRepartidor());
            pstmt.setDate(3, entrega.getFecha());
            pstmt.setTime(4, entrega.getHora());

            return pstmt.executeUpdate() > 0;
        }
    }

    /**
     * Lista todas las entregas registradas en la base de datos.
     */
    public List<Entrega> listar() throws SQLException {
        List<Entrega> lista = new ArrayList<>();
        String sql = "SELECT * FROM entregas";

        try (Connection conn = ConexionDB.conectar();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                Entrega entrega = new Entrega(
                        rs.getInt("id"),
                        rs.getInt("id_pedido"),
                        rs.getInt("id_repartidor"),
                        rs.getDate("fecha"),
                        rs.getTime("hora")
                );
                lista.add(entrega);
            }
        }
        return lista;
    }

    /**
     * Actualiza una entrega existente.
     */
    public boolean actualizar(Entrega entrega) throws SQLException {
        String sql = "UPDATE entregas SET id_pedido = ?, id_repartidor = ?, fecha = ?, hora = ? WHERE id = ?";

        try (Connection conn = ConexionDB.conectar();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, entrega.getIdPedido());
            pstmt.setInt(2, entrega.getIdRepartidor());
            pstmt.setDate(3, entrega.getFecha());
            pstmt.setTime(4, entrega.getHora());
            pstmt.setInt(5, entrega.getId());

            return pstmt.executeUpdate() > 0;
        }
    }

    /**
     * Elimina una entrega por su ID.
     */
    public boolean eliminar(int id) throws SQLException {
        String sql = "DELETE FROM entregas WHERE id = ?";

        try (Connection conn = ConexionDB.conectar();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, id);
            return pstmt.executeUpdate() > 0;
        }
    }
}

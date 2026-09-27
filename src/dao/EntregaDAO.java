package dao;

import modelo.Entrega;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class EntregaDAO {

    // Guarda el registro de la entrega asociando el pedido con el repartidor
    public boolean guardar(Entrega entrega) {
        String sql = "INSERT INTO entrega (id_pedido, id_repartidor, fecha, hora) VALUES (?, ?, ?, ?)";

        try (Connection conn = ConexionDB.conectar();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, entrega.getIdPedido());
            pstmt.setInt(2, entrega.getIdRepartidor());
            pstmt.setDate(3, entrega.getFecha());
            pstmt.setTime(4, entrega.getHora());

            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error al guardar la entrega: " + e.getMessage());
            return false;
        }
    }
}

package dao;

import modelo.Pedido;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PedidoDAO {

    // Guarda un pedido en la base de datos MySQL
    public boolean guardar(Pedido pedido) {
        String sql = "INSERT INTO pedido (direccion, tipo, estado) VALUES (?, ?, ?)";

        try (Connection conn = ConexionDB.conectar();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, pedido.getDireccionEntrega());
            pstmt.setString(2, pedido.getTipo() != null ? pedido.getTipo() : "COMIDA");
            pstmt.setString(3, (pedido.getEstado() != null) ? pedido.getEstado().name() : "PENDIENTE");

            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error al guardar pedido: " + e.getMessage());
            return false;
        }
    }

    // Lista todos los pedidos desde MySQL para mostrarlos en la JTable
    public List<Pedido> listarTodos() {
        List<Pedido> lista = new ArrayList<>();
        String sql = "SELECT * FROM pedido";

        try (Connection conn = ConexionDB.conectar();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                int id = rs.getInt("id");
                String direccion = rs.getString("direccion");
                String tipo = rs.getString("tipo");

                Pedido p = crearPedidoConcreto(id, direccion, tipo);
                lista.add(p);
            }
        } catch (SQLException e) {
            System.err.println("Error al listar pedidos: " + e.getMessage());
        }
        return lista;
    }

    private Pedido crearPedidoConcreto(int id, String direccion, String tipo) {
        return new Pedido(id, direccion, 5) {
            @Override
            protected int calcularTiempoEntrega() {
                return getDistanciaKm() * 3;
            }
        };
    }
}

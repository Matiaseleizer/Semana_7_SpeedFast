package dao;

import modelo.EstadoPedido;
import modelo.Pedido;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PedidoDAO {

    /**
     * Guarda un nuevo pedido incluyendo la distancia en la base de datos MySQL.
     */
    public boolean guardar(Pedido pedido) throws SQLException {
        String sql = "INSERT INTO pedidos (direccion, tipo, estado, distancia) VALUES (?, ?, ?, ?)";

        try (Connection conn = ConexionDB.conectar();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, pedido.getDireccionEntrega());
            pstmt.setString(2, pedido.getTipo() != null ? pedido.getTipo() : "COMIDA");
            pstmt.setString(3, (pedido.getEstado() != null) ? pedido.getEstado().name() : "PENDIENTE");
            pstmt.setInt(4, pedido.getDistanciaKm());

            return pstmt.executeUpdate() > 0;
        }
    }

    /**
     * Lista todos los pedidos leyendo la distancia real almacenada en MySQL.
     */
    public List<Pedido> listarTodos() throws SQLException {
        List<Pedido> lista = new ArrayList<>();
        String sql = "SELECT * FROM pedidos";

        try (Connection conn = ConexionDB.conectar();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                int id = rs.getInt("id");
                String direccion = rs.getString("direccion");
                String tipo = rs.getString("tipo");
                String estadoStr = rs.getString("estado");
                int distancia = rs.getInt("distancia"); // Lee la distancia guardada

                Pedido p = crearPedidoConcreto(id, direccion, tipo, distancia);
                if (estadoStr != null) {
                    try {
                        p.setEstado(EstadoPedido.valueOf(estadoStr.toUpperCase()));
                    } catch (IllegalArgumentException e) {
                        p.setEstado(EstadoPedido.PENDIENTE);
                    }
                }
                lista.add(p);
            }
        }
        return lista;
    }

    /**
     * Actualiza la dirección, tipo, estado y distancia de un pedido.
     */
    public boolean actualizar(Pedido pedido) throws SQLException {
        String sql = "UPDATE pedidos SET direccion = ?, tipo = ?, estado = ?, distancia = ? WHERE id = ?";

        try (Connection conn = ConexionDB.conectar();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, pedido.getDireccionEntrega());
            pstmt.setString(2, pedido.getTipo() != null ? pedido.getTipo() : "COMIDA");
            pstmt.setString(3, (pedido.getEstado() != null) ? pedido.getEstado().name() : "PENDIENTE");
            pstmt.setInt(4, pedido.getDistanciaKm());
            pstmt.setInt(5, pedido.getIdPedido());

            return pstmt.executeUpdate() > 0;
        }
    }

    /**
     * Elimina un pedido por su ID.
     */
    public boolean eliminar(int id) throws SQLException {
        String sql = "DELETE FROM pedidos WHERE id = ?";

        try (Connection conn = ConexionDB.conectar();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, id);
            return pstmt.executeUpdate() > 0;
        }
    }

    /**
     * Instancia dinámicamente un Pedido pasando la distancia recuperada de la BD.
     */
    private Pedido crearPedidoConcreto(int id, String direccion, String tipo, int distancia) {
        Pedido p = new Pedido(id, direccion, distancia) {
            @Override
            protected int calcularTiempoEntrega() {
                return getDistanciaKm() * 3;
            }
        };
        p.setTipo(tipo);
        return p;
    }
}

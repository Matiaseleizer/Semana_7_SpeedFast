package vista;

import controlador.ControladorPedidos;
import dao.PedidoDAO;
import modelo.*;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class VentanaGestionPedidos extends JFrame {

    private Usuario usuarioActual;
    private ControladorPedidos controladorPedidos;
    private PedidoDAO pedidoDAO; // Instancia del DAO para interactuar con MySQL

    // Componentes del Formulario
    private JComboBox<String> cbTipoPedido;
    private JTextField txtDireccion;
    private JTextField txtDistancia;
    private JButton btnAgregarPedido;
    private JButton btnIniciarReparto;

    // Componentes de la Tabla
    private JTable tablaPedidos;
    private DefaultTableModel modelTabla;

    // Contador secuencial para asignación automática de ID
    private int contadorId = 1;

    public VentanaGestionPedidos(Usuario usuarioActual, ControladorPedidos controladorPedidos) {
        this.usuarioActual = usuarioActual;
        this.controladorPedidos = controladorPedidos;
        this.pedidoDAO = new PedidoDAO(); // Inicialización del DAO

        // Configuración de la ventana principal
        setTitle("Verdulería al Paso - Gestión de Envíos | Usuario: " + usuarioActual.getNombreUsuario());
        setSize(750, 500);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));

        // Inicializar interfaz por componentes
        inicializarEncabezado();
        inicializarFormulario();
        inicializarTabla();
        inicializarBotoneraAccion();

        // Aplicar restricciones según el Rol
        aplicarPermisosPorRol();

        // Cargar los pedidos existentes desde la base de datos MySQL
        cargarPedidosDesdeBD();
    }

    private void inicializarEncabezado() {
        JPanel panelHeader = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panelHeader.setBackground(new Color(230, 240, 250));

        JLabel lblInfoUser = new JLabel("  Conectado como: " + usuarioActual.getNombreUsuario()
                + " | Rol: " + usuarioActual.getRol().toUpperCase());
        lblInfoUser.setFont(new Font("SansSerif", Font.BOLD, 12));
        panelHeader.add(lblInfoUser);

        add(panelHeader, BorderLayout.NORTH);
    }

    private void inicializarFormulario() {
        JPanel panelForm = new JPanel(new GridLayout(4, 2, 8, 8));
        panelForm.setBorder(BorderFactory.createTitledBorder("Registrar Nuevo Pedido"));

        panelForm.add(new JLabel("Tipo de Pedido:"));
        cbTipoPedido = new JComboBox<>(new String[]{"Comida", "Encomienda", "Express", "Estandar"});
        panelForm.add(cbTipoPedido);

        panelForm.add(new JLabel("Dirección de Entrega:"));
        txtDireccion = new JTextField();
        panelForm.add(txtDireccion);

        panelForm.add(new JLabel("Distancia (KM):"));
        txtDistancia = new JTextField();
        panelForm.add(txtDistancia);

        btnAgregarPedido = new JButton("Agregar a Zona de Carga");
        panelForm.add(new JLabel("")); // Espacio vacío para alinear el botón
        panelForm.add(btnAgregarPedido);

        add(panelForm, BorderLayout.WEST);

        // Evento para agregar pedido
        btnAgregarPedido.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                agregarPedido();
            }
        });
    }

    private void inicializarTabla() {
        String[] columnas = {"ID", "Tipo", "Dirección", "Distancia", "Estado"};
        modelTabla = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Hace que la tabla no sea editable directamente
            }
        };

        tablaPedidos = new JTable(modelTabla);
        JScrollPane scrollPane = new JScrollPane(tablaPedidos);
        scrollPane.setBorder(BorderFactory.createTitledBorder("Pedidos en Zona de Carga"));

        add(scrollPane, BorderLayout.CENTER);
    }

    private void inicializarBotoneraAccion() {
        JPanel panelSouth = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        btnIniciarReparto = new JButton("Iniciar Simulación de Reparto");
        btnIniciarReparto.setFont(new Font("SansSerif", Font.BOLD, 12));
        btnIniciarReparto.setBackground(new Color(40, 167, 69));
        btnIniciarReparto.setForeground(Color.WHITE);

        panelSouth.add(btnIniciarReparto);
        add(panelSouth, BorderLayout.SOUTH);

        // Evento para iniciar la simulación multihilo
        btnIniciarReparto.addActionListener(e -> ejecutarSimulacionMultihilo());
    }

    private void aplicarPermisosPorRol() {
        if (usuarioActual.getRol().equalsIgnoreCase("Operador")) {
            btnIniciarReparto.setEnabled(false);
            btnIniciarReparto.setToolTipText("Requiere rol de Administrador para ejecutar el reparto.");
        }
    }

    /**
     * Consulta los registros almacenados en MySQL a través de PedidoDAO
     * y llena la JTable con los resultados.
     */
    private void cargarPedidosDesdeBD() {
        modelTabla.setRowCount(0); // Limpia la tabla actual
        List<Pedido> pedidosBD = pedidoDAO.listarTodos();

        for (Pedido p : pedidosBD) {
            modelTabla.addRow(new Object[]{
                    p.getIdPedido(),
                    p.getTipo() != null ? p.getTipo() : "Estándar",
                    p.getDireccionEntrega(),
                    p.getDistanciaKm(),
                    p.getEstado()
            });
        }
    }

    private void agregarPedido() {
        String direccion = txtDireccion.getText().trim();
        String distanciaTexto = txtDistancia.getText().trim();
        String tipoSeleccionado = (String) cbTipoPedido.getSelectedItem();

        if (direccion.isEmpty() || distanciaTexto.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Complete la dirección y distancia.", "Atención", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            int distancia = Integer.parseInt(distanciaTexto);
            Pedido nuevoPedido = crearInstanciaPedido(tipoSeleccionado, contadorId++, direccion, distancia);

            // 1. Guardar el pedido en la base de datos MySQL mediante JDBC
            boolean guardadoExitoso = pedidoDAO.guardar(nuevoPedido);

            if (guardadoExitoso) {
                // 2. Agregar al controlador en memoria
                controladorPedidos.agregarPedidoATabla(nuevoPedido, modelTabla);

                // 3. Recargar la JTable desde la BD para reflejar los datos actualizados
                cargarPedidosDesdeBD();

                // Limpiar formulario
                txtDireccion.setText("");
                txtDistancia.setText("");
                JOptionPane.showMessageDialog(this, "Pedido agregado y guardado con éxito.");
            } else {
                JOptionPane.showMessageDialog(this, "Error al guardar el pedido en la base de datos.", "Error BD", JOptionPane.ERROR_MESSAGE);
            }

        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "La distancia debe ser un número entero válido.", "Error de formato", JOptionPane.ERROR_MESSAGE);
        }
    }

    private Pedido crearInstanciaPedido(String tipo, int id, String direccion, int distancia) {
        switch (tipo) {
            case "Comida":
                return new PedidoComida(id, direccion, distancia);
            case "Encomienda":
                return new PedidoEncomienda(id, direccion, distancia);
            case "Express":
                return new PedidoExpress(id, direccion, distancia);
            default:
                return new PedidoEstandar(id, direccion, distancia);
        }
    }

    private void ejecutarSimulacionMultihilo() {
        if (controladorPedidos.getZonaDeCarga().estaVacia()) {
            JOptionPane.showMessageDialog(this, "No hay pedidos en la zona de carga para repartir.", "Zona Vacía", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        btnIniciarReparto.setEnabled(false);

        new Thread(() -> {
            ExecutorService executor = Executors.newFixedThreadPool(3);

            executor.execute(new Repartidor("Carlos", controladorPedidos.getZonaDeCarga()));
            executor.execute(new Repartidor("María", controladorPedidos.getZonaDeCarga()));
            executor.execute(new Repartidor("Pedro", controladorPedidos.getZonaDeCarga()));

            executor.shutdown();

            try {
                if (executor.awaitTermination(1, TimeUnit.MINUTES)) {
                    SwingUtilities.invokeLater(() -> {
                        JOptionPane.showMessageDialog(this, "¡Todos los pedidos han sido entregados por los repartidores!", "Simulación Finalizada", JOptionPane.INFORMATION_MESSAGE);
                        actualizarEstadosEnTabla();
                        btnIniciarReparto.setEnabled(true);
                    });
                }
            } catch (InterruptedException ex) {
                Thread.currentThread().interrupt();
            }
        }).start();
    }

    private void actualizarEstadosEnTabla() {
        for (int i = 0; i < modelTabla.getRowCount(); i++) {
            modelTabla.setValueAt(EstadoPedido.ENTREGADO, i, 4);
        }
    }
}
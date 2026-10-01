package vista;

import controlador.ControladorPedidos;
import dao.EntregaDAO;
import dao.PedidoDAO;
import dao.RepartidorDAO;
import modelo.*;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Date;
import java.sql.SQLException;
import java.sql.Time;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class VentanaGestionPedidos extends JFrame {

    private Usuario usuarioActual;
    private ControladorPedidos controladorPedidos;

    // DAOs
    private PedidoDAO pedidoDAO;
    private RepartidorDAO repartidorDAO;
    private EntregaDAO entregaDAO;

    // Pestaña Pedidos y Simulación
    private JComboBox<String> cbTipoPedido;
    private JTextField txtDireccion;
    private JTextField txtDistancia;
    private JButton btnAgregarPedido;
    private JButton btnIniciarReparto;
    private JTable tablaPedidos;
    private DefaultTableModel modelTabla;

    // Pestaña Repartidores
    private JTextField txtRepartidorNombre;
    private JTable tablaRepartidores;
    private DefaultTableModel modelRepartidores;

    // Pestaña Entregas
    private JComboBox<Pedido> cbEntregaPedido;
    private JComboBox<Repartidor> cbEntregaRepartidor;
    private JTextField txtFecha;
    private JTextField txtHora;
    private JTable tablaEntregas;
    private DefaultTableModel modelEntregas;

    private int contadorId = 1;

    public VentanaGestionPedidos(Usuario usuarioActual, ControladorPedidos controladorPedidos) {
        this.usuarioActual = usuarioActual;
        this.controladorPedidos = controladorPedidos;

        // Inicializar DAOs
        this.pedidoDAO = new PedidoDAO();
        this.repartidorDAO = new RepartidorDAO();
        this.entregaDAO = new EntregaDAO();

        setTitle("SpeedFast - Gestión de Envíos | Usuario: " + usuarioActual.getNombreUsuario());
        setSize(850, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));

        inicializarEncabezado();

        // Contenedor principal con pestañas
        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.addTab("Pedidos & Simulación", crearPanelPedidosYSimulacion());
        tabbedPane.addTab("Gestión Repartidores", crearPanelRepartidores());
        tabbedPane.addTab("Registro Entregas", crearPanelEntregas());

        add(tabbedPane, BorderLayout.CENTER);

        aplicarPermisosPorRol();
        cargarDatosGenerales();
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

    // Panel de Pedidos y Simulación
    private JPanel crearPanelPedidosYSimulacion() {
        JPanel panelPrincipal = new JPanel(new BorderLayout(10, 10));

        // Formulario
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
        panelForm.add(new JLabel(""));
        panelForm.add(btnAgregarPedido);

        btnAgregarPedido.addActionListener(e -> agregarPedido());
        panelPrincipal.add(panelForm, BorderLayout.WEST);

        // Tabla Pedidos
        String[] columnas = {"ID", "Tipo", "Dirección", "Distancia", "Estado"};
        modelTabla = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        tablaPedidos = new JTable(modelTabla);
        JScrollPane scrollPane = new JScrollPane(tablaPedidos);
        scrollPane.setBorder(BorderFactory.createTitledBorder("Pedidos en Zona de Carga / BD"));
        panelPrincipal.add(scrollPane, BorderLayout.CENTER);

        // Botón de Simulación
        JPanel panelSouth = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        btnIniciarReparto = new JButton("Iniciar Simulación de Reparto");
        btnIniciarReparto.setFont(new Font("SansSerif", Font.BOLD, 12));
        btnIniciarReparto.setBackground(new Color(40, 167, 69));
        btnIniciarReparto.setForeground(Color.WHITE);

        panelSouth.add(btnIniciarReparto);
        panelPrincipal.add(panelSouth, BorderLayout.SOUTH);

        btnIniciarReparto.addActionListener(e -> ejecutarSimulacionMultihilo());

        return panelPrincipal;
    }

    // Panel de Repartidores
    private JPanel crearPanelRepartidores() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        JPanel form = new JPanel(new FlowLayout(FlowLayout.LEFT));
        form.setBorder(BorderFactory.createTitledBorder("Registrar Repartidor"));

        txtRepartidorNombre = new JTextField(15);
        JButton btnGuardar = new JButton("Guardar");
        JButton btnEliminar = new JButton("Eliminar");

        form.add(new JLabel("Nombre:"));
        form.add(txtRepartidorNombre);
        form.add(btnGuardar);
        form.add(btnEliminar);

        modelRepartidores = new DefaultTableModel(new String[]{"ID", "Nombre"}, 0);
        tablaRepartidores = new JTable(modelRepartidores);

        btnGuardar.addActionListener(e -> {
            String nombre = txtRepartidorNombre.getText().trim();
            if (nombre.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Ingrese el nombre del repartidor.", "Validación", JOptionPane.WARNING_MESSAGE);
                return;
            }
            try {
                repartidorDAO.guardar(new Repartidor(nombre));
                JOptionPane.showMessageDialog(this, "Repartidor guardado correctamente.");
                txtRepartidorNombre.setText("");
                cargarDatosGenerales();
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this, "Error en BD: " + ex.getMessage(), "Error SQL", JOptionPane.ERROR_MESSAGE);
            }
        });

        btnEliminar.addActionListener(e -> {
            int row = tablaRepartidores.getSelectedRow();
            if (row == -1) {
                JOptionPane.showMessageDialog(this, "Seleccione un repartidor para eliminar.", "Validación", JOptionPane.WARNING_MESSAGE);
                return;
            }
            int id = (int) modelRepartidores.getValueAt(row, 0);
            try {
                repartidorDAO.eliminar(id);
                JOptionPane.showMessageDialog(this, "Repartidor eliminado.");
                cargarDatosGenerales();
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this, "Error al eliminar: " + ex.getMessage(), "Error SQL", JOptionPane.ERROR_MESSAGE);
            }
        });

        panel.add(form, BorderLayout.NORTH);
        panel.add(new JScrollPane(tablaRepartidores), BorderLayout.CENTER);
        return panel;
    }

    // Panel de Entregas (CRUD)
    private JPanel crearPanelEntregas() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        JPanel form = new JPanel(new GridLayout(3, 2, 5, 5));
        form.setBorder(BorderFactory.createTitledBorder("Registrar Entrega"));

        cbEntregaPedido = new JComboBox<>();
        cbEntregaRepartidor = new JComboBox<>();
        txtFecha = new JTextField(10);
        txtHora = new JTextField(8);

        form.add(new JLabel("Pedido:"));
        form.add(cbEntregaPedido);
        form.add(new JLabel("Repartidor:"));
        form.add(cbEntregaRepartidor);
        form.add(new JLabel("Fecha (AAAA-MM-DD):"));
        form.add(txtFecha);
        form.add(new JLabel("Hora (HH:MM:SS):"));
        form.add(txtHora);

        JPanel panelAcciones = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton btnGuardarEntrega = new JButton("Guardar Entrega");
        JButton btnEliminarEntrega = new JButton("Eliminar Entrega");
        panelAcciones.add(btnGuardarEntrega);
        panelAcciones.add(btnEliminarEntrega);

        modelEntregas = new DefaultTableModel(new String[]{"ID", "ID Pedido", "ID Repartidor", "Fecha", "Hora"}, 0);
        tablaEntregas = new JTable(modelEntregas);

        btnGuardarEntrega.addActionListener(e -> {
            Pedido p = (Pedido) cbEntregaPedido.getSelectedItem();
            Repartidor r = (Repartidor) cbEntregaRepartidor.getSelectedItem();

            if (p == null || r == null || txtFecha.getText().trim().isEmpty() || txtHora.getText().trim().isEmpty()) {
                JOptionPane.showMessageDialog(this, "Todos los campos son requeridos.", "Validación", JOptionPane.WARNING_MESSAGE);
                return;
            }

            try {
                Date fecha = Date.valueOf(txtFecha.getText().trim());
                Time hora = Time.valueOf(txtHora.getText().trim());

                Entrega entrega = new Entrega(p.getIdPedido(), r.getId(), fecha, hora);
                entregaDAO.guardar(entrega);

                JOptionPane.showMessageDialog(this, "Entrega registrada correctamente.");
                txtFecha.setText("");
                txtHora.setText("");
                cargarDatosGenerales();
            } catch (IllegalArgumentException ex) {
                JOptionPane.showMessageDialog(this, "Formato fecha/hora inválido (Use AAAA-MM-DD y HH:MM:SS).", "Formato Incorrecto", JOptionPane.ERROR_MESSAGE);
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this, "Error en BD: " + ex.getMessage(), "Error SQL", JOptionPane.ERROR_MESSAGE);
            }
        });

        btnEliminarEntrega.addActionListener(e -> {
            int row = tablaEntregas.getSelectedRow();
            if (row == -1) {
                JOptionPane.showMessageDialog(this, "Seleccione una entrega para eliminar.", "Validación", JOptionPane.WARNING_MESSAGE);
                return;
            }
            int id = (int) modelEntregas.getValueAt(row, 0);
            try {
                entregaDAO.eliminar(id);
                JOptionPane.showMessageDialog(this, "Entrega eliminada.");
                cargarDatosGenerales();
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this, "Error al eliminar: " + ex.getMessage(), "Error SQL", JOptionPane.ERROR_MESSAGE);
            }
        });

        JPanel panelSuperior = new JPanel(new BorderLayout());
        panelSuperior.add(form, BorderLayout.CENTER);
        panelSuperior.add(panelAcciones, BorderLayout.SOUTH);

        panel.add(panelSuperior, BorderLayout.NORTH);
        panel.add(new JScrollPane(tablaEntregas), BorderLayout.CENTER);
        return panel;
    }

    private void aplicarPermisosPorRol() {
        if (usuarioActual.getRol().equalsIgnoreCase("Operador")) {
            btnIniciarReparto.setEnabled(false);
            btnIniciarReparto.setToolTipText("Requiere rol de Administrador para ejecutar el reparto.");
        }
    }

    // Carga unificada de datos desde MySQL
    private void cargarDatosGenerales() {
        try {
            // 1. Cargar Pedidos
            modelTabla.setRowCount(0);
            if (cbEntregaPedido != null) cbEntregaPedido.removeAllItems();
            List<Pedido> pedidosBD = pedidoDAO.listarTodos();

            for (Pedido p : pedidosBD) {
                modelTabla.addRow(new Object[]{
                        p.getIdPedido(),
                        p.getTipo() != null ? p.getTipo() : "Estándar",
                        p.getDireccionEntrega(),
                        p.getDistanciaKm(),
                        p.getEstado()
                });
                if (cbEntregaPedido != null) cbEntregaPedido.addItem(p);
            }

            // 2. Cargar Repartidores
            if (modelRepartidores != null) {
                modelRepartidores.setRowCount(0);
                cbEntregaRepartidor.removeAllItems();
                List<Repartidor> repartidoresBD = repartidorDAO.listarTodos();
                for (Repartidor r : repartidoresBD) {
                    modelRepartidores.addRow(new Object[]{r.getId(), r.getNombre()});
                    cbEntregaRepartidor.addItem(r);
                }
            }

            // 3. Cargar Entregas
            if (modelEntregas != null) {
                modelEntregas.setRowCount(0);
                List<Entrega> entregasBD = entregaDAO.listar();
                for (Entrega e : entregasBD) {
                    modelEntregas.addRow(new Object[]{e.getId(), e.getIdPedido(), e.getIdRepartidor(), e.getFecha(), e.getHora()});
                }
            }

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error al cargar datos desde MySQL: " + ex.getMessage(), "Error BD", JOptionPane.ERROR_MESSAGE);
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

            // Guardar en MySQL
            boolean guardadoExitoso = pedidoDAO.guardar(nuevoPedido);

            if (guardadoExitoso) {
                controladorPedidos.agregarPedidoATabla(nuevoPedido, modelTabla);
                cargarDatosGenerales();

                txtDireccion.setText("");
                txtDistancia.setText("");
                JOptionPane.showMessageDialog(this, "Pedido agregado y guardado con éxito.");
            } else {
                JOptionPane.showMessageDialog(this, "Error al guardar el pedido en la base de datos.", "Error BD", JOptionPane.ERROR_MESSAGE);
            }

        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "La distancia debe ser un número entero válido.", "Error de formato", JOptionPane.ERROR_MESSAGE);
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error en la base de datos: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private Pedido crearInstanciaPedido(String tipo, int id, String direccion, int distancia) {
        Pedido p;
        switch (tipo) {
            case "Comida":
                p = new PedidoComida(id, direccion, distancia);
                break;
            case "Encomienda":
                p = new PedidoEncomienda(id, direccion, distancia);
                break;
            case "Express":
                p = new PedidoExpress(id, direccion, distancia);
                break;
            default:
                p = new PedidoEstandar(id, direccion, distancia);
                break;
        }

        p.setTipo(tipo.toUpperCase());

        return p;
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
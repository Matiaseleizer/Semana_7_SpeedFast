package modelo;

public abstract class Pedido {

    // Atributos
    private int idPedido;
    private String direccionEntrega;
    private int distanciaKm;
    protected String repartidor;
    private EstadoPedido estado;
    private String tipo;

    // Constructor
    public Pedido(int idPedido, String direccionEntrega, int distanciaKm) {
        this.idPedido = idPedido;
        this.direccionEntrega = direccionEntrega;
        this.distanciaKm = distanciaKm;
        this.estado = EstadoPedido.PENDIENTE;
    }

    // Métodos de consola
    public void mostrarResumen() {
        System.out.println("---N° pedido #: " + idPedido);
        System.out.println("---Dirección de entrega: " + direccionEntrega);
        System.out.println("---Distancia (KM): " + distanciaKm);
        System.out.println("---Tiempo de entrega : " + calcularTiempoEntrega() + " minutos.");
        System.out.println("---Estado: " + estado);
    }

    public void asignarRepartidor() {
        this.repartidor = "modelo.Repartidor estándar";
    }

    protected abstract int calcularTiempoEntrega(); // Método Abstracto

    private void tipoDeEntrega() {}

    private void factoresQueAfectanDuracion() {}

    // Getters & Setters
    public int getIdPedido() {
        return idPedido;
    }

    public void setIdPedido(int idPedido) {
        this.idPedido = idPedido;
    }

    public String getDireccionEntrega() {
        return direccionEntrega;
    }

    public void setDireccionEntrega(String direccionEntrega) {
        this.direccionEntrega = direccionEntrega;
    }

    public int getDistanciaKm() {
        return distanciaKm;
    }

    public void setDistanciaKm(int distanciaKm) {
        this.distanciaKm = distanciaKm;
    }

    public String getRepartidor() {
        return repartidor;
    }

    public void setRepartidor(String repartidor) {
        this.repartidor = repartidor;
    }

    public EstadoPedido getEstado() {
        return estado;
    }

    public void setEstado(EstadoPedido estado) {
        this.estado = estado;
    }

    public String getTipo() {
        return tipo;
    }

    // AGREGADO: Necesario para que PedidoDAO y la vista puedan asignar el tipo de pedido
    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    // AGREGADO: Necesario para que el JComboBox en Swing muestre el pedido de forma entendible
    @Override
    public String toString() {
        return idPedido + " - " + (direccionEntrega != null ? direccionEntrega : "Sin dirección");
    }
}

package modelo;

public class Repartidor implements Runnable {

    private String nombre;
    private ZonaDeCarga zonaDeCarga;
    private int id;

    // AGREGADO: Constructor sencillo sin ZonaDeCarga (necesario para DAO y Swing)
    public Repartidor(String nombre) {
        this.nombre = nombre;
    }

    // Constructor completo con ZonaDeCarga (para la simulación con hilos)
    public Repartidor(String nombre, ZonaDeCarga zonaDeCarga) {
        this.nombre = nombre;
        this.zonaDeCarga = zonaDeCarga;
    }

    // Getters y Setters
    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public ZonaDeCarga getZonaDeCarga() {
        return zonaDeCarga;
    }

    public void setZonaDeCarga(ZonaDeCarga zonaDeCarga) {
        this.zonaDeCarga = zonaDeCarga;
    }

    // AGREGADO: Formato legible para desplegables JComboBox en la interfaz Swing
    @Override
    public String toString() {
        return (id > 0 ? id + " - " : "") + nombre;
    }

    @Override
    public void run() {
        // Bucle dinámico: atiende la cola compartida mientras haya elementos
        while (true) {
            if (zonaDeCarga == null) {
                break;
            }

            // Extracción sincronizada desde la modelo.ZonaDeCarga
            Pedido pedido = zonaDeCarga.retirarPedido();

            // Si la cola está vacía, el hilo concluye su jornada
            if (pedido == null) {
                break;
            }

            // Asignación de datos y cambio a EN_REPARTO
            pedido.setRepartidor(this.nombre);
            pedido.setEstado(EstadoPedido.EN_REPARTO);

            System.out.println("[modelo.Repartidor: " + nombre + "] Retirando "
                    + pedido.getClass().getSimpleName() + " #" + pedido.getIdPedido()
                    + " (Estado: " + pedido.getEstado() + ")...");

            try {
                // Generar tiempo aleatorio de simulación entre 1000 ms y 3000 ms
                long tiempoEntrega = (long) (Math.random() * 2000 + 1000);
                Thread.sleep(tiempoEntrega);

            } catch (InterruptedException e) {
                System.out.println("La entrega del repartidor " + nombre + " fue interrumpida.");
                Thread.currentThread().interrupt();
                break;
            }

            // Transición final a ENTREGADO
            pedido.setEstado(EstadoPedido.ENTREGADO);
            System.out.println("[modelo.Repartidor: " + nombre + "] modelo.Pedido #" + pedido.getIdPedido()
                    + " entregado con éxito (Estado: " + pedido.getEstado() + ").");
        }
    }
}
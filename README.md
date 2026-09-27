# 🚀 SpeedFast - Sistema Multihilo y Persistencia con JDBC + MySQL (MVC + GUI Swing)

![Java](https://img.shields.io/badge/Java-17%2B-orange)
![MySQL](https://img.shields.io/badge/MySQL-8.0%2B-blue)
![IDE](https://img.shields.io/badge/IDE-IntelliJ%20IDEA-blue)
![DuocUC](https://img.shields.io/badge/Evaluaci%C3%B3n-Sumativa%20Semana%207-003366)

Proyecto desarrollado para la asignatura **Desarrollo Orientado a Objetos II** de **Duoc UC Online** (Semana 7: *"Persistencia de datos con JDBC y bases de datos relacionales"*).

La aplicación evoluciona el sistema de logística de **SpeedFast**, incorporando una capa de persistencia mediante **JDBC (Java Database Connectivity)** y **MySQL**, integrando el patrón **DAO (Data Access Object)** con el modelo concurrente multihilo y la interfaz gráfica en **Java Swing**.

---

## 📋 Descripción del Caso (Semana 7)

**SpeedFast** consolida su arquitectura integrando persistencia relacional para garantizar que los pedidos, repartidores y entregas no se pierdan al cerrar la aplicación:

* 🗄️ **Persistencia en Base de Datos MySQL:** Creación y gestión de la base de datos `speedfast_db` con las tablas `pedido`, `repartidor` y `entrega`.
* 🔌 **Conexión Robusta vía JDBC:** Implementación de la clase `ConexionDB` utilizando el driver de MySQL cargado desde la carpeta del proyecto (`lib/`).
* 🧱 **Patrón DAO (Data Access Object):** Separación clara entre la lógica de acceso a datos y la interfaz mediante `PedidoDAO`, `RepartidorDAO` y `EntregaDAO`, haciendo uso de `PreparedStatement` y `ResultSet`.
* 📊 **Sincronización con GUI (`JTable`):** Carga dinámica de los datos almacenados en MySQL al iniciar la `VentanaGestionPedidos` y almacenamiento automático de los nuevos registros creados desde el formulario.
* ⚡ **Ejecución Concurrente Multihilo:** Mantenimiento de la simulación de reparto en segundo plano vía `ExecutorService` sin bloquear el hilo principal de eventos de la interfaz (*Event Dispatch Thread*).

---

## 🛠️ Conceptos y Tecnologías Aplicadas

1. **Persistencia de Datos y JDBC:**
   * Conexión a servidor MySQL local mediante `DriverManager` y cadenas de conexión preparadas.
   * Inclusión del driver `mysql-connector-j-*.jar` dentro de la carpeta `lib/` del proyecto.
   * Ejecución segura de consultas SQL (`INSERT`, `SELECT`) utilizando `PreparedStatement` para prevenir inyecciones SQL y `ResultSet` para la lectura de registros.
2. **Patrón de Diseño DAO (Data Access Object):**
   * Encapsulamiento de las operaciones de lectura y escritura en la base de datos.
   * Manejo de clases abstractas (como `Pedido`) instanciando subclases concretas dinámicamente al mapear los resultados SQL.
3. **Patrón de Arquitectura MVC (Modelo-Vista-Controlador):**
   * **Modelo:** Clases de dominio (`Pedido`, `Entrega`, `Repartidor`, `Usuario`, `ZonaDeCarga`, `EstadoPedido`, interfaces).
   * **Acceso a Datos (DAO):** `ConexionDB`, `PedidoDAO`, `RepartidorDAO`, `EntregaDAO`.
   * **Controlador:** `ControladorUsuarios` y `ControladorPedidos`.
   * **Vista:** Formulario de inicio de sesión (`VentanaLogin`) y panel principal (`VentanaGestionPedidos`).
4. **Interfaz Gráfica (Java Swing) y Concurrencia:**
   * Uso de `JTable`, `DefaultTableModel`, `JComboBox`, `JTextField` y layouts.
   * Coordinación de hilos `Runnable` con `ExecutorService` para la simulación sincronizada de entregas.

---

## 📁 Estructura del Proyecto

```text
semana 7/
 ├── lib/
 │    └── mysql-connector-j-8.x.x.jar # Driver JDBC de MySQL
 │
 ├── src/
 │    ├── dao/
 │    │    ├── ConexionDB.java        # Gestión de conexión JDBC a speedfast_db
 │    │    ├── PedidoDAO.java         # Operaciones CRUD para la tabla 'pedido'
 │    │    ├── RepartidorDAO.java     # Operaciones CRUD para la tabla 'repartidor'
 │    │    └── EntregaDAO.java        # Registro de entregas en la tabla 'entrega'
 │    │
 │    ├── modelo/
 │    │    ├── Cancelable.java        # Interfaz para cancelación
 │    │    ├── Despachable.java       # Interfaz para despacho
 │    │    ├── Rastreable.java        # Interfaz para historial y rastreo
 │    │    ├── EstadoPedido.java      # Enum de estados (PENDIENTE, EN_REPARTO, ENTREGADO)
 │    │    ├── Pedido.java            # Clase base abstracta
 │    │    ├── PedidoComida.java      # Subclase especializada
 │    │    ├── PedidoEncomienda.java  # Subclase especializada
 │    │    ├── PedidoExpress.java     # Subclase especializada
 │    │    ├── PedidoEstandar.java    # Subclase concreta estándar
 │    │    ├── Entrega.java           # Entidad del registro de entrega
 │    │    ├── Repartidor.java        # Tarea ejecutable (Runnable)
 │    │    ├── Usuario.java           # Entidad de usuario y permisos
 │    │    └── ZonaDeCarga.java       # Recurso compartido sincronizado
 │    │
 │    ├── controlador/
 │    │    ├── ControladorUsuarios.java # Lógica de autenticación
 │    │    └── ControladorPedidos.java  # Intermediario de datos y zona de carga
 │    │
 │    ├── vista/
 │    │    ├── VentanaLogin.java           # Interfaz de inicio de sesión
 │    │    └── VentanaGestionPedidos.java  # Vista principal con JTable y formulario JDBC
 │    │
 │    └── Main.java                   # Punto de entrada de la aplicación
 └── README.md                        # Documentación del proyecto

## ⚙️ Requisitos y Entorno de Ejecución

* **JDK:** Java SE 17 o superior.
* **IDE:** IntelliJ IDEA.
* **Control de Versiones:** Git & GitHub.

---

## 📄 Licencia y Créditos

Desarrollado como actividad formativa para **Duoc UC Online** - Carrera de Analista programador computacional. Reservados todos los derechos institucionales.

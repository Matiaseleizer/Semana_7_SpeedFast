# 🚀 SpeedFast - Sistema Multihilo y Persistencia con JDBC + MySQL (MVC + GUI Swing)

![Java](https://img.shields.io/badge/Java-17%2B-orange)
![MySQL](https://img.shields.io/badge/MySQL-8.0%2B-blue)
![IDE](https://img.shields.io/badge/IDE-IntelliJ%20IDEA-blue)
![DuocUC](https://img.shields.io/badge/Evaluaci%C3%B3n-Sumativa%20Semana%208-003366)

Proyecto desarrollado para la asignatura **Desarrollo Orientado a Objetos II** de **Duoc UC Online** (Semana 8: *"Integración Final del Sistema, Persistencia Relacional y Concurrencia"*).

La aplicación consolida la arquitectura del sistema de logística **SpeedFast**, logrando una integración total entre una interfaz gráfica en **Java Swing**, un backend relacional en **MySQL** a través de **JDBC** con el patrón **DAO**, un modelo de concurrencia **Multihilo** y un esquema de **Control de Acceso Basado en Roles (RBAC)**.

---

## 📋 Descripción del Caso (Semana 8)

**SpeedFast** culmina su plataforma de gestión garantizando que los pedidos, repartidores y registros de entrega mantengan sincronización continua y persistencia relacional completa:

* 🗄️ **Persistencia en Base de Datos MySQL:** Gestión de la base de datos `speedfast_db` mediante las tablas en plural `repartidores`, `pedidos` (incluyendo soporte para tipo, estado y columna `distancia`) y `entregas`.
* 🔌 **Conexión Robusta vía JDBC:** Implementación centralizada en `ConexionDB` utilizando el driver oficial `com.mysql.cj.jdbc.Driver` con parámetros de conexión seguros.
* 🧱 **Patrón DAO (Data Access Object):** Operaciones CRUD completas (`guardar`, `listarTodos`, `actualizar`, `eliminar`) a través de `PedidoDAO`, `RepartidorDAO` y `EntregaDAO`, con propagación de `SQLException` mediante `JOptionPane`.
* 📊 **Interfaz Gráfica Integrada (`JTabbedPane`):** Visualización y gestión en tiempo real con pestañas dedicadas para **Pedidos & Simulación**, **Repartidores** y **Entregas**.
* 🔐 **Autenticación y Roles (RBAC):** Inicio de sesión interactivo (`VentanaLogin`) diferenciando accesos entre perfil `Administrador` y `Operador`.
* ⚡ **Ejecución Concurrente Multihilo:** Simulación de reparto en segundo plano mediante `ExecutorService` (Thread Pool) procesando la cola sincronizada `ZonaDeCarga` sin congelar el hilo principal de eventos de Swing (*Event Dispatch Thread*).

---

## 🛠️ Conceptos y Tecnologías Aplicadas

1. **Persistencia de Datos y JDBC:**
   * Conexión a servidor MySQL local mediante `DriverManager`.
   * Consultas SQL preparadas (`PreparedStatement`) para prevenir inyecciones SQL y mapeo dinámico de datos a través de `ResultSet`.
2. **Patrón de Diseño DAO (Data Access Object):**
   * Desacoplamiento de la lógica de persistencia respecto a la interfaz de usuario.
   * Instanciación polimórfica de pedidos (`PedidoComida`, `PedidoEncomienda`, `PedidoExpress`, `PedidoEstandar`) al recuperar registros de MySQL.
3. **Patrón de Arquitectura MVC (Modelo-Vista-Controlador):**
   * **Modelo:** Clases de dominio (`Pedido`, `Entrega`, `Repartidor`, `Usuario`, `ZonaDeCarga`, `EstadoPedido`).
   * **Acceso a Datos (DAO):** `ConexionDB`, `PedidoDAO`, `RepartidorDAO`, `EntregaDAO`.
   * **Controlador:** `ControladorUsuarios` y `ControladorPedidos`.
   * **Vista:** `VentanaLogin` y `VentanaGestionPedidos` con pestañas interactivas.
4. **Interfaz Gráfica (Java Swing) y Concurrencia:**
   * Componentes `JTabbedPane`, `JTable`, `DefaultTableModel`, `JComboBox`, `JTextField` y `JOptionPane`.
   * Asignación de tareas ejecutables (`Runnable`) con `ExecutorService` para la simulación sincronizada de entregas.

---

## 📁 Estructura del Proyecto

```text
semana 8/
 ├── lib/
 │    └── mysql-connector-j-8.x.x.jar # Driver JDBC de MySQL
 │
 ├── src/
 │    ├── dao/
 │    │    ├── ConexionDB.java        # Gestión de conexión JDBC a speedfast_db
 │    │    ├── PedidoDAO.java         # Operaciones CRUD para la tabla 'pedidos'
 │    │    ├── RepartidorDAO.java     # Operaciones CRUD para la tabla 'repartidores'
 │    │    └── EntregaDAO.java        # Operaciones CRUD para la tabla 'entregas'
 │    │
 │    ├── modelo/
 │    │    ├── Cancelable.java        # Interfaz para cancelación
 │    │    ├── Despachable.java       # Interfaz para despacho
 │    │    ├── Rastreable.java        # Interfaz para historial y rastreo
 │    │    ├── EstadoPedido.java      # Enum de estados (PENDIENTE, EN_REPARTO, ENTREGADO)
 │    │    ├── Pedido.java            # Clase base abstracta
 │    │    ├── PedidoComida.java      # Subclase concreta
 │    │    ├── PedidoEncomienda.java  # Subclase concreta
 │    │    ├── PedidoExpress.java     # Subclase concreta
 │    │    ├── PedidoEstandar.java    # Subclase concreta
 │    │    ├── Entrega.java           # Modelo de la entidad Entrega
 │    │    ├── Repartidor.java        # Tarea ejecutable (Runnable)
 │    │    ├── Usuario.java           # Modelo de usuario con rol
 │    │    └── ZonaDeCarga.java       # Recurso compartido sincronizado (Buffer)
 │    │
 │    ├── controlador/
 │    │    ├── ControladorUsuarios.java # Lógica de autenticación y sesiones
 │    │    └── ControladorPedidos.java  # Intermediario de datos y zona de carga
 │    │
 │    ├── vista/
 │    │    ├── VentanaLogin.java           # Interfaz de inicio de sesión
 │    │    └── VentanaGestionPedidos.java  # Vista principal con JTabbedPane, CRUD y simulación
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

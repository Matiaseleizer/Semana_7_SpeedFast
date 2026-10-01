import controlador.ControladorUsuarios;
import modelo.ZonaDeCarga;
import vista.VentanaLogin;

import javax.swing.*;

public class Main {

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
        }

        // Ejecutar la interfaz gráfica dentro del Event Dispatch Thread (EDT)
        SwingUtilities.invokeLater(() -> {
            // 1. Instanciar controladores y modelos
            ControladorUsuarios controladorUsuarios = new ControladorUsuarios();
            ZonaDeCarga zonaDeCarga = new ZonaDeCarga();

            // 2. Instanciar y desplegar la ventana de Login
            VentanaLogin ventanaLogin = new VentanaLogin(controladorUsuarios, zonaDeCarga);
            ventanaLogin.setVisible(true);
        });
    }
}
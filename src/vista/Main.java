package vista;

import javax.swing.SwingUtilities;

/**
 * Clase principal que inicia la ejecución de la aplicación de Ajedrez.
 */
public class Main {

    /**
     * Método principal de arranque en Java.
     * Qué hace: Lanza la interfaz gráfica de Swing en el hilo de despacho de eventos.
     * Qué recibe: args (String[]) argumentos de línea de comandos.
     * Qué devuelve: void (no retorna valor).
     */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            VentanaAjedrez ventana = new VentanaAjedrez();
            ventana.setVisible(true);
        });
    }
}


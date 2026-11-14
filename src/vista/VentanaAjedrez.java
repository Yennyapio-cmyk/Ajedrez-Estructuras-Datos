package vista;

import java.awt.*;
import javax.swing.*;

/**
 * Clase encargada de construir y mostrar la interfaz gráfica principal del juego de Ajedrez
 * utilizando Java Swing. Incluye el menú principal, el tablero de 8x8 y los controles de partida.
 */
public class VentanaAjedrez extends JFrame {

    private JButton[][] casillas = new JButton[8][8];
    private JLabel lblTurno;
    private JTextArea areaHistorial;

    /**
     * Constructor de la ventana principal.
     * Qué hace: Configura las propiedades de la ventana, el menú superior, el tablero y el panel lateral.
     * Qué recibe: No recibe parámetros.
     * Qué devuelve: Una instancia de VentanaAjedrez lista para visualizarse.
     */
    public VentanaAjedrez() {
        setTitle("Ajedrez - Proyecto Estructuras de Datos");
        setSize(900, 680);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));

        inicializarMenu();
        inicializarTablero();
        inicializarPanelLateral();
    }

    /**
     * Configura la barra de menú principal requerida por el proyecto.
     * Qué hace: Crea las opciones para iniciar partida (2 jugadores o vs IA), rendirse o salir.
     * Qué recibe: No recibe parámetros.
     * Qué devuelve: void (no retorna valor).
     */
    private void inicializarMenu() {
        JMenuBar barraMenu = new JMenuBar();
        JMenu menuJuego = new JMenu("Juego");

        JMenuItem itemNuevo2P = new JMenuItem("Nueva Partida (2 Jugadores)");
        JMenuItem itemNuevoIA = new JMenuItem("Nueva Partida (vs IA Árbol)");
        JMenuItem itemRendirse = new JMenuItem("Rendirse / Finalizar Partida");
        JMenuItem itemSalir = new JMenuItem("Salir");

        itemSalir.addActionListener(e -> System.exit(0));
        itemRendirse.addActionListener(e -> JOptionPane.showMessageDialog(this, 
                "Partida finalizada. ¡Gracias por jugar!"));

        menuJuego.add(itemNuevo2P);
        menuJuego.add(itemNuevoIA);
        menuJuego.addSeparator();
        menuJuego.add(itemRendirse);
        menuJuego.add(itemSalir);

        barraMenu.add(menuJuego);
        setJMenuBar(barraMenu);
    }

    /**
     * Dibuja la cuadrícula de 8x8 del tablero de ajedrez con colores alternados y piezas iniciales.
     * Qué hace: Instancia 64 botones, asigna colores claros/oscuros y coloca los símbolos iniciales.
     * Qué recibe: No recibe parámetros.
     * Qué devuelve: void (no retorna valor).
     */
    private void inicializarTablero() {
        JPanel panelTablero = new JPanel(new GridLayout(8, 8));
        panelTablero.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        Color colorClaro = new Color(240, 217, 181);
        Color colorOscuro = new Color(181, 136, 99);

        String[][] disposicionInicial = {
            {"♜", "♞", "♝", "♛", "♚", "♝", "♞", "♜"},
            {"♟", "♟", "♟", "♟", "♟", "♟", "♟", "♟"},
            {"", "", "", "", "", "", "", ""},
            {"", "", "", "", "", "", "", ""},
            {"", "", "", "", "", "", "", ""},
            {"", "", "", "", "", "", "", ""},
            {"♙", "♙", "♙", "♙", "♙", "♙", "♙", "♙"},
            {"♖", "♘", "♗", "♕", "♔", "♗", "♘", "♖"}
        };

        for (int fila = 0; fila < 8; fila++) {
            for (int col = 0; col < 8; col++) {
                JButton boton = new JButton(disposicionInicial[fila][col]);
                boton.setFont(new Font("Serif", Font.PLAIN, 42));
                boton.setFocusPainted(false);
                boton.setOpaque(true);
                boton.setBorderPainted(false);

                if ((fila + col) % 2 == 0) {
                    boton.setBackground(colorClaro);
                } else {
                    boton.setBackground(colorOscuro);
                }

                casillas[fila][col] = boton;
                panelTablero.add(boton);
            }
        }
        add(panelTablero, BorderLayout.CENTER);
    }

    /**
     * Construye el panel derecho con información de turnos, historial y botones de estructuras.
     * Qué hace: Agrega controles para Deshacer/Rehacer (Pila), Repetir (Cola) y Ver Piezas (Lista Doble).
     * Qué recibe: No recibe parámetros.
     * Qué devuelve: void (no retorna valor).
     */
    private void inicializarPanelLateral() {
        JPanel panelDerecho = new JPanel(new BorderLayout(8, 8));
        panelDerecho.setPreferredSize(new Dimension(260, 0));
        panelDerecho.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 10));

        lblTurno = new JLabel("Turno: Blancas (Lista Circular)", SwingConstants.CENTER);
        lblTurno.setFont(new Font("SansSerif", Font.BOLD, 14));
        panelDerecho.add(lblTurno, BorderLayout.NORTH);

        areaHistorial = new JTextArea("Historial de jugadas (Cola):\n");
        areaHistorial.setEditable(false);
        panelDerecho.add(new JScrollPane(areaHistorial), BorderLayout.CENTER);

        JPanel panelBotones = new JPanel(new GridLayout(3, 1, 5, 5));
        panelBotones.add(new JButton("Deshacer Jugada (Pila)"));
        panelBotones.add(new JButton("Rehacer Jugada (Pila)"));
        panelBotones.add(new JButton("Repetir Partida (Cola)"));
        panelDerecho.add(panelBotones, BorderLayout.SOUTH);

        add(panelDerecho, BorderLayout.EAST);
    }
}

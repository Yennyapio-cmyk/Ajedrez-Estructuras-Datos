package vista;

import estructuras.ColaHistorial;
import java.awt.*;
import javax.swing.*;
import modelo.JuegoAjedrez;
import modelo.Movimiento;
import modelo.Pieza;

/**
 * Interfaz gráfica completa del juego de Ajedrez en Java Swing.
 * Conecta el tablero interactivo con el motor JuegoAjedrez y permite visualizar
 * el funcionamiento de las 5 estructuras de datos implementadas desde cero.
 */
public class VentanaAjedrez extends JFrame {

    private JuegoAjedrez juego;
    private JButton[][] casillas = new JButton[8][8];
    private JLabel lblTurno;
    private JLabel lblEstado;
    private JTextArea areaHistorial;

    private int filaSeleccionada = -1;
    private int colSeleccionada = -1;
    private boolean bloqueadoPorRepeticion = false;

    private final Color colorClaro = new Color(240, 217, 181);
    private final Color colorOscuro = new Color(181, 136, 99);
    private final Color colorSeleccion = new Color(130, 151, 105);
    private final Color colorDestinoValido = new Color(170, 210, 130);

    /**
     * Constructor de la ventana principal del juego.
     * Qué hace: Inicia el motor en modo 2 jugadores, construye la interfaz y dibuja el tablero.
     * Qué recibe: No recibe parámetros.
     * Qué devuelve: Una instancia de VentanaAjedrez lista para jugar.
     */
    public VentanaAjedrez() {
        this.juego = new JuegoAjedrez(false);

        setTitle("Ajedrez - Proyecto Final Estructuras de Datos");
        setSize(980, 720);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));

        inicializarMenu();
        inicializarTablero();
        inicializarPanelLateral();
        actualizarVistaCompleta();
    }

    /**
     * Configura la barra de menú principal con modos de juego, estructuras y reglas.
     * Qué hace: Crea las opciones para iniciar partida (2P o vs IA), consultar estructuras y salir.
     * Qué recibe: No recibe parámetros.
     * Qué devuelve: void (no retorna valor).
     */
    private void inicializarMenu() {
        JMenuBar barraMenu = new JMenuBar();

        JMenu menuJuego = new JMenu("Juego");
        JMenuItem itemNuevo2P = new JMenuItem("Nueva Partida (2 Jugadores)");
        JMenuItem itemNuevoIA = new JMenuItem("Nueva Partida (vs IA - Árbol BST)");
        JMenuItem itemRendirse = new JMenuItem("Rendirse / Terminar Partida");
        JMenuItem itemSalir = new JMenuItem("Salir");

        itemNuevo2P.addActionListener(e -> reiniciarJuego(false));
        itemNuevoIA.addActionListener(e -> reiniciarJuego(true));
        itemRendirse.addActionListener(e -> {
            juego.rendirse();
            actualizarVistaCompleta();
            JOptionPane.showMessageDialog(this, juego.getMensajeEstado(), "Fin de la partida", JOptionPane.INFORMATION_MESSAGE);
        });
        itemSalir.addActionListener(e -> System.exit(0));

        menuJuego.add(itemNuevo2P);
        menuJuego.add(itemNuevoIA);
        menuJuego.addSeparator();
        menuJuego.add(itemRendirse);
        menuJuego.add(itemSalir);

        JMenu menuEstructuras = new JMenu("Inspeccionar Estructuras");
        JMenuItem itemListaDoble = new JMenuItem("Ver Piezas Vivas (Lista Doble: Adelante y Atrás)");
        JMenuItem itemListaCircular = new JMenuItem("Ver Ciclo de Turnos (Lista Circular)");
        JMenuItem itemArbolIA = new JMenuItem("Ver Recorridos del Árbol IA (Inorden/Preorden/Posorden)");

        itemListaDoble.addActionListener(e -> mostrarInfoListaDoble());
        itemListaCircular.addActionListener(e -> JOptionPane.showMessageDialog(this,
                "Estructura Lista Circular de Turnos:\n" + juego.getTurnos().recorrer()
                + "\nTurno activo: " + juego.getTurnoActual(),
                "Lista Circular", JOptionPane.INFORMATION_MESSAGE));
        itemArbolIA.addActionListener(e -> mostrarInfoArbolIA());

        menuEstructuras.add(itemListaDoble);
        menuEstructuras.add(itemListaCircular);
        menuEstructuras.add(itemArbolIA);

        JMenu menuAyuda = new JMenu("Ayuda");
        JMenuItem itemReglas = new JMenuItem("Reglas y Condiciones de Fin");
        itemReglas.addActionListener(e -> JOptionPane.showMessageDialog(this,
                "Controles:\n"
                + "- Haz clic en una pieza de tu turno para ver sus movimientos válidos en verde.\n"
                + "- Haz clic en una casilla verde para mover.\n\n"
                + "Condiciones de fin de partida (no infinito):\n"
                + "- Jaque Mate al Rey contrario.\n"
                + "- Empate por Rey Ahogado o Material Insuficiente.\n"
                + "- Empate automático por regla de 50 movimientos sin capturas ni avance de peón.\n"
                + "- Rendición desde el menú Juego.",
                "Reglas del Ajedrez", JOptionPane.INFORMATION_MESSAGE));
        menuAyuda.add(itemReglas);

        barraMenu.add(menuJuego);
        barraMenu.add(menuEstructuras);
        barraMenu.add(menuAyuda);
        setJMenuBar(barraMenu);
    }

    /**
     * Construye la cuadrícula interactiva de 8x8 del tablero de ajedrez.
     * Qué hace: Crea los 64 botones y les asigna el evento de clic para seleccionar y mover piezas.
     * Qué recibe: No recibe parámetros.
     * Qué devuelve: void (no retorna valor).
     */
    private void inicializarTablero() {
        JPanel panelTablero = new JPanel(new GridLayout(8, 8));
        panelTablero.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        for (int fila = 0; fila < 8; fila++) {
            for (int col = 0; col < 8; col++) {
                final int f = fila;
                final int c = col;
                JButton boton = new JButton();
                boton.setFont(new Font("Serif", Font.PLAIN, 42));
                boton.setFocusPainted(false);
                boton.setOpaque(true);
                boton.setBorderPainted(false);
                boton.addActionListener(e -> manejarClicCasilla(f, c));

                casillas[fila][col] = boton;
                panelTablero.add(boton);
            }
        }
        add(panelTablero, BorderLayout.CENTER);
    }

    /**
     * Construye el panel derecho con estado del turno, historial de jugadas y botones de control.
     * Qué hace: Añade etiquetas de estado, área de historial y botones para Pila, Cola y Lista Doble.
     * Qué recibe: No recibe parámetros.
     * Qué devuelve: void (no retorna valor).
     */
    private void inicializarPanelLateral() {
        JPanel panelDerecho = new JPanel(new BorderLayout(8, 8));
        panelDerecho.setPreferredSize(new Dimension(290, 0));
        panelDerecho.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 10));

        JPanel panelSuperior = new JPanel(new GridLayout(2, 1, 4, 4));
        lblTurno = new JLabel("Turno: BLANCAS", SwingConstants.CENTER);
        lblTurno.setFont(new Font("SansSerif", Font.BOLD, 15));

        lblEstado = new JLabel("Partida en curso", SwingConstants.CENTER);
        lblEstado.setFont(new Font("SansSerif", Font.PLAIN, 13));
        lblEstado.setForeground(new Color(30, 90, 160));

        panelSuperior.add(lblTurno);
        panelSuperior.add(lblEstado);
        panelDerecho.add(panelSuperior, BorderLayout.NORTH);

        areaHistorial = new JTextArea();
        areaHistorial.setEditable(false);
        areaHistorial.setFont(new Font("Monospaced", Font.PLAIN, 13));
        panelDerecho.add(new JScrollPane(areaHistorial), BorderLayout.CENTER);

        JPanel panelBotones = new JPanel(new GridLayout(4, 1, 5, 5));
        JButton btnDeshacer = new JButton("Deshacer Jugada (Pila)");
        JButton btnRehacer = new JButton("Rehacer Jugada (Pila)");
        JButton btnRepetir = new JButton("Repetir Partida (Cola)");
        JButton btnPiezas = new JButton("Piezas Vivas (Lista Doble)");

        btnDeshacer.addActionListener(e -> {
            if (bloqueadoPorRepeticion) return;
            if (juego.deshacerJugada()) {
                filaSeleccionada = -1;
                colSeleccionada = -1;
                actualizarVistaCompleta();
            } else {
                JOptionPane.showMessageDialog(this, "No hay jugadas en la pila para deshacer.");
            }
        });

        btnRehacer.addActionListener(e -> {
            if (bloqueadoPorRepeticion) return;
            if (juego.rehacerJugada()) {
                filaSeleccionada = -1;
                colSeleccionada = -1;
                actualizarVistaCompleta();
            } else {
                JOptionPane.showMessageDialog(this, "No hay jugadas en la pila para rehacer.");
            }
        });

        btnRepetir.addActionListener(e -> reproducirHistorialDesdeCola());
        btnPiezas.addActionListener(e -> mostrarInfoListaDoble());

        panelBotones.add(btnDeshacer);
        panelBotones.add(btnRehacer);
        panelBotones.add(btnRepetir);
        panelBotones.add(btnPiezas);
        panelDerecho.add(panelBotones, BorderLayout.SOUTH);

        add(panelDerecho, BorderLayout.EAST);
    }

    /**
     * Gestiona la selección de piezas y ejecución de movimientos al hacer clic en una casilla.
     * Qué hace: Selecciona una pieza propia o intenta mover la pieza ya seleccionada al destino.
     * Qué recibe: fila (int) fila clicada, col (int) columna clicada.
     * Qué devuelve: void (no retorna valor).
     */
    private void manejarClicCasilla(int fila, int col) {
        if (bloqueadoPorRepeticion || juego.isPartidaTerminada()) {
            return;
        }

        Pieza piezaClicada = juego.getPieza(fila, col);

        // Si no hay pieza seleccionada, intentar seleccionar una del turno actual
        if (filaSeleccionada == -1) {
            if (piezaClicada != null && piezaClicada.getColor().equals(juego.getTurnoActual())) {
                filaSeleccionada = fila;
                colSeleccionada = col;
                actualizarVistaCompleta();
            }
            return;
        }

        // Si hace clic en otra pieza suya, cambiar la selección
        if (piezaClicada != null && piezaClicada.getColor().equals(juego.getTurnoActual())) {
            filaSeleccionada = fila;
            colSeleccionada = col;
            actualizarVistaCompleta();
            return;
        }

        // Intentar mover la pieza seleccionada a (fila, col)
        boolean movido = juego.moverPieza(filaSeleccionada, colSeleccionada, fila, col);
        filaSeleccionada = -1;
        colSeleccionada = -1;
        actualizarVistaCompleta();

        if (!movido) {
            lblEstado.setText("Movimiento inválido. Intenta otra casilla.");
            return;
        }

        if (juego.isPartidaTerminada()) {
            JOptionPane.showMessageDialog(this, juego.getMensajeEstado(), "Partida Finalizada", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        // Si está activo el modo vs IA y es turno de las negras, responde la IA con su Árbol BST
        if (juego.isVsIA() && juego.getTurnoActual().equals("NEGRAS")) {
            Timer pausaIA = new Timer(350, evt -> {
                juego.ejecutarTurnoIA();
                actualizarVistaCompleta();
                if (juego.isPartidaTerminada()) {
                    JOptionPane.showMessageDialog(this, juego.getMensajeEstado(), "Partida Finalizada", JOptionPane.INFORMATION_MESSAGE);
                }
            });
            pausaIA.setRepeats(false);
            pausaIA.start();
        }
    }

    /**
     * Actualiza las piezas dibujadas, los colores de casillas, el turno y el historial en pantalla.
     * Qué hace: Sincroniza todos los componentes visuales de Swing con el estado actual de JuegoAjedrez.
     * Qué recibe: No recibe parámetros.
     * Qué devuelve: void (no retorna valor).
     */
    private void actualizarVistaCompleta() {
        for (int f = 0; f < 8; f++) {
            for (int c = 0; c < 8; c++) {
                Pieza p = juego.getPieza(f, c);
                casillas[f][c].setText(p != null ? p.getSimbolo() : "");

                Color base = ((f + c) % 2 == 0) ? colorClaro : colorOscuro;
                if (f == filaSeleccionada && c == colSeleccionada) {
                    casillas[f][c].setBackground(colorSeleccion);
                } else if (filaSeleccionada != -1 && juego.esMovimientoLegal(filaSeleccionada, colSeleccionada, f, c, juego.getTurnoActual())) {
                    casillas[f][c].setBackground(colorDestinoValido);
                } else {
                    casillas[f][c].setBackground(base);
                }
            }
        }

        String modo = juego.isVsIA() ? " (vs IA)" : " (2P)";
        lblTurno.setText("Turno: " + juego.getTurnoActual() + modo);
        lblEstado.setText(juego.getMensajeEstado());

        // Mostrar el contenido de la Cola de historial sin vaciarla
        StringBuilder sb = new StringBuilder("Historial de Jugadas (Cola FIFO):\n");
        sb.append("---------------------------------\n");
        ColaHistorial copia = juego.getColaHistorial().clonar();
        int num = 1;
        while (!copia.estaVacia()) {
            Movimiento m = (Movimiento) copia.desencolar();
            sb.append(num++).append(". ").append(m.getNotacion()).append("\n");
        }
        areaHistorial.setText(sb.toString());
    }

    /**
     * Reproduce la partida paso a paso desencolando una copia de ColaHistorial.
     * Qué hace: Limpia el tablero visual y aplica cada movimiento de la cola cada 600 ms.
     * Qué recibe: No recibe parámetros.
     * Qué devuelve: void (no retorna valor).
     */
    private void reproducirHistorialDesdeCola() {
        ColaHistorial colaRepeticion = juego.getColaHistorial().clonar();
        if (colaRepeticion.estaVacia()) {
            JOptionPane.showMessageDialog(this, "Aún no hay jugadas registradas en la cola.");
            return;
        }

        bloqueadoPorRepeticion = true;
        lblEstado.setText("Reproduciendo partida desde la Cola...");

        // Dibujar tablero inicial antes de iniciar animación
        String[][] inicial = {
            {"♜", "♞", "♝", "♛", "♚", "♝", "♞", "♜"},
            {"♟", "♟", "♟", "♟", "♟", "♟", "♟", "♟"},
            {"", "", "", "", "", "", "", ""},
            {"", "", "", "", "", "", "", ""},
            {"", "", "", "", "", "", "", ""},
            {"", "", "", "", "", "", "", ""},
            {"♙", "♙", "♙", "♙", "♙", "♙", "♙", "♙"},
            {"♖", "♘", "♗", "♕", "♔", "♗", "♘", "♖"}
        };
        for (int f = 0; f < 8; f++) {
            for (int c = 0; c < 8; c++) {
                casillas[f][c].setText(inicial[f][c]);
                casillas[f][c].setBackground(((f + c) % 2 == 0) ? colorClaro : colorOscuro);
            }
        }

        Timer timer = new Timer(600, null);
        timer.addActionListener(e -> {
            if (!colaRepeticion.estaVacia()) {
                Movimiento m = (Movimiento) colaRepeticion.desencolar();
                String simbolo = casillas[m.getFilaOrigen()][m.getColOrigen()].getText();
                casillas[m.getFilaOrigen()][m.getColOrigen()].setText("");
                casillas[m.getFilaDestino()][m.getColDestino()].setText(simbolo);
                lblEstado.setText("Repetición: " + m.getNotacion());
            } else {
                timer.stop();
                bloqueadoPorRepeticion = false;
                actualizarVistaCompleta();
                JOptionPane.showMessageDialog(this, "Repetición finalizada.");
            }
        });
        timer.start();
    }

    /**
     * Muestra en un cuadro de diálogo el recorrido hacia adelante y hacia atrás de la Lista Doble.
     * Qué hace: Invoca recorrerAdelante() y recorrerAtras() sobre piezasVivas.
     * Qué recibe: No recibe parámetros.
     * Qué devuelve: void (no retorna valor).
     */
    private void mostrarInfoListaDoble() {
        JTextArea area = new JTextArea(12, 45);
        area.setEditable(false);
        area.setLineWrap(true);
        area.setWrapStyleWord(true);
        area.setText("Total de piezas vivas: " + juego.getPiezasVivas().tamano() + "\n\n"
                + "RECORRIDO HACIA ADELANTE (inicio -> fin):\n"
                + juego.getPiezasVivas().recorrerAdelante() + "\n\n"
                + "RECORRIDO HACIA ATRÁS (fin -> inicio):\n"
                + juego.getPiezasVivas().recorrerAtras());
        JOptionPane.showMessageDialog(this, new JScrollPane(area), "Lista Doblemente Enlazada", JOptionPane.INFORMATION_MESSAGE);
    }

    /**
     * Muestra los recorridos Inorden, Preorden y Posorden del Árbol Binario de Búsqueda de la IA.
     * Qué hace: Consulta inorden(), preorden() y posorden() en ArbolIA.
     * Qué recibe: No recibe parámetros.
     * Qué devuelve: void (no retorna valor).
     */
    private void mostrarInfoArbolIA() {
        JTextArea area = new JTextArea(14, 48);
        area.setEditable(false);
        area.setLineWrap(true);
        area.setWrapStyleWord(true);
        area.setText("Evaluación táctico-posicional del último turno de la IA (Árbol BST):\n"
                + "(Inicia una partida 'vs IA' y haz al menos un movimiento para poblar el árbol)\n\n"
                + "RECORRIDO INORDEN (menor a mayor puntaje):\n" + juego.getArbolEvaluacion().inorden() + "\n\n"
                + "RECORRIDO PREORDEN (Raíz - Izq - Der):\n" + juego.getArbolEvaluacion().preorden() + "\n\n"
                + "RECORRIDO POSORDEN (Izq - Der - Raíz):\n" + juego.getArbolEvaluacion().posorden());
        JOptionPane.showMessageDialog(this, new JScrollPane(area), "Árbol Binario de Búsqueda (IA)", JOptionPane.INFORMATION_MESSAGE);
    }

    /**
     * Reinicia el juego en el modo seleccionado (2 jugadores o contra IA).
     * Qué hace: Restablece selecciones, reinicia el motor y actualiza toda la ventana.
     * Qué recibe: vsIA (boolean) true para jugar contra IA, false para 2 jugadores.
     * Qué devuelve: void (no retorna valor).
     */
    private void reiniciarJuego(boolean vsIA) {
        bloqueadoPorRepeticion = false;
        filaSeleccionada = -1;
        colSeleccionada = -1;
        juego.iniciarNuevaPartida(vsIA);
        actualizarVistaCompleta();
    }
}

package vista;

import estructuras.ColaHistorial;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import modelo.JuegoAjedrez;
import modelo.Movimiento;
import modelo.Pieza;

/**
 * Interfaz gráfica completa del juego de Ajedrez en Java Swing.
 * Conecta el tablero interactivo con el motor JuegoAjedrez y permite visualizar
 * de forma organizada las 5 estructuras de datos implementadas desde cero.
 */
public class VentanaAjedrez extends JFrame {

    private JuegoAjedrez juego;
    private JButton[][] casillas = new JButton[8][8];
    private JLabel lblTurno;
    private JLabel lblEstado;
    private JPanel panelTarjetaTurno;
    private JTextArea areaHistorial;

    private int filaSeleccionada = -1;
    private int colSeleccionada = -1;
    private boolean bloqueadoPorRepeticion = false;
    // Paleta de colores moderna
    private final Color fondoPrincipal = new  Color(38, 36, 33);
    private final Color fondoPanelLateral = new Color(48, 46, 43);
    private final Color fondoHistorial = new Color(33, 31, 29);
    private final Color colorClaro = new Color(238, 238, 210);
    private final Color colorOscuro = new Color(118, 150, 86);
    private final Color colorSeleccion = new Color(246, 246, 105);
    private final Color colorDestinoValido = new Color(186, 202, 68);

    /**
     * Constructor de la ventana principal del juego.
     * Qué hace: Inicia el motor en modo 2 jugadores, construye la interfaz y dibuja el tablero.
     * Qué recibe: No recibe parámetros.
     * Qué devuelve: Una instancia de VentanaAjedrez lista para jugar.
     */
    public VentanaAjedrez() {
        this.juego = new JuegoAjedrez(false);

        setTitle("Ajedrez - Proyecto Final Estructuras de Datos");
        setSize(1020, 740);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        getContentPane().setBackground(fondoPrincipal);
        setLayout(new BorderLayout(14, 14));

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
        barraMenu.setBackground(new Color(30, 28, 26));
        barraMenu.setBorder(new EmptyBorder(4, 8, 4, 8));
        
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
                "Estructura Lista Circular de Turnos:\n\n" + juego.getTurnos().recorrer()
                + "\n\nTurno activo actual: " + juego.getTurnoActual(),
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
        JPanel contenedorMarco = new JPanel(new BorderLayout());
        contenedorMarco.setBackground(fondoPrincipal);
        contenedorMarco.setBorder(new EmptyBorder(12, 14, 12, 4));

        JPanel panelTablero = new JPanel(new GridLayout(8, 8));
        panelTablero.setBorder(new LineBorder(new Color(85, 82, 77), 4, true));
        
        for (int fila = 0; fila < 8; fila++) {
            for (int col = 0; col < 8; col++) {
                final int f = fila;
                final int c = col;
                JButton boton = new JButton();
                boton.setFont(new Font("Serif", Font.PLAIN, 44));
                boton.setFocusPainted(false);
                boton.setOpaque(true);
                boton.setBorderPainted(false);
                boton.setCursor(new Cursor(Cursor.HAND_CURSOR));
                boton.addActionListener(e -> manejarClicCasilla(f, c));

                casillas[fila][col] = boton;
                panelTablero.add(boton);
            }
        }
        contenedorMarco.add(panelTablero, BorderLayout.CENTER);
        add(contenedorMarco, BorderLayout.CENTER);
    }
    /**
     * Construye el panel derecho con tarjeta de turno, historial estilizado y botones coloridos.
     * Qué hace: Diseña la barra lateral con paleta oscura, tarjeta de estado y botones interactivos.
     * Qué recibe: No recibe parámetros.
     * Qué devuelve: void (no retorna valor).
     */

    private void inicializarPanelLateral() {
        JPanel panelDerecho = new JPanel(new BorderLayout(10, 12));
        panelDerecho.setPreferredSize(new Dimension(320, 0));
        panelDerecho.setBackground(fondoPanelLateral);
        panelDerecho.setBorder(BorderFactory.createCompoundBorder(
                new EmptyBorder(12, 4, 12, 14),
                BorderFactory.createCompoundBorder(
                        new LineBorder(new Color(75, 72, 67), 1, true),
                        new EmptyBorder(12, 12, 12, 12)
                )
        ));
        // 1. Tarjeta superior de Turno y Estado
        panelTarjetaTurno = new JPanel(new GridLayout(2, 1, 2, 4));
        panelTarjetaTurno.setBackground(new Color(245, 245, 240));
        panelTarjetaTurno.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(new Color(180, 180, 170), 1, true),
                new EmptyBorder(10, 10, 10, 10)
        ));
        lblTurno = new JLabel("Turno: BLANCAS", SwingConstants.CENTER);
        lblTurno.setFont(new Font("SansSerif", Font.BOLD, 16));

        lblEstado = new JLabel("Partida en curso", SwingConstants.CENTER);
        lblEstado.setFont(new Font("SansSerif", Font.PLAIN, 13));

        panelTarjetaTurno.add(lblTurno);
        panelTarjetaTurno.add(lblEstado);
        panelDerecho.add(panelTarjetaTurno, BorderLayout.NORTH);
        // 2. Sección central de Historial estilizado
        JPanel panelHistorialContenedor = new JPanel(new BorderLayout(0, 6));
        panelHistorialContenedor.setBackground(fondoPanelLateral);

        JLabel lblTituloHistorial = new JLabel("  HISTORIAL DE JUGADAS (COLA FIFO)");
        lblTituloHistorial.setFont(new Font("SansSerif", Font.BOLD, 12));
        lblTituloHistorial.setForeground(new Color(215, 212, 205));
        lblTituloHistorial.setOpaque(true);
        lblTituloHistorial.setBackground(new Color(62, 60, 56));
        lblTituloHistorial.setBorder(new EmptyBorder(7, 6, 7, 6));

        areaHistorial = new JTextArea();
        areaHistorial.setEditable(false);
        areaHistorial.setBackground(fondoHistorial);
        areaHistorial.setForeground(new Color(235, 235, 228));
        areaHistorial.setFont(new Font("Monospaced", Font.PLAIN, 13));
        areaHistorial.setMargin(new Insets(10, 12, 10, 12));

        JScrollPane scrollHistorial = new JScrollPane(areaHistorial);
        scrollHistorial.setBorder(new LineBorder(new Color(75, 72, 67), 1));

        panelHistorialContenedor.add(lblTituloHistorial, BorderLayout.NORTH);
        panelHistorialContenedor.add(scrollHistorial, BorderLayout.CENTER);
        panelDerecho.add(panelHistorialContenedor, BorderLayout.CENTER);

        // 3. Botones inferiores coloridos con efecto hover
        JPanel panelBotones = new JPanel(new GridLayout(4, 1, 0, 8));
        panelBotones.setBackground(fondoPanelLateral);

        JButton btnDeshacer = crearBotonEstilizado("↩  Deshacer Jugada (Pila)", new Color(217, 119, 6), new Color(245, 158, 11));
        JButton btnRehacer = crearBotonEstilizado("↪  Rehacer Jugada (Pila)", new Color(37, 99, 235), new Color(59, 130, 246));
        JButton btnRepetir = crearBotonEstilizado("▶  Repetir Partida (Cola)", new Color(22, 163, 74), new Color(34, 197, 94));
        JButton btnPiezas = crearBotonEstilizado("⇄  Piezas Vivas (Lista Doble)", new Color(124, 58, 237), new Color(139, 92, 246));


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
     * Crea un botón personalizado con color de fondo, texto blanco y efecto hover.
     * Qué hace: Configura propiedades visuales compatibles con macOS para que el botón conserve su color.
     * Qué recibe: texto (String), colorNormal (Color), colorHover (Color).
     * Qué devuelve: JButton estilizado listo para añadir a un panel.
     */
    private JButton crearBotonEstilizado(String texto, Color colorNormal, Color colorHover) {
        JButton boton = new JButton(texto);
        boton.setFont(new Font("SansSerif", Font.BOLD, 13));
        boton.setForeground(Color.WHITE);
        boton.setBackground(colorNormal);
        boton.setOpaque(true);
        boton.setBorderPainted(false);
        boton.setFocusPainted(false);
        boton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        boton.setBorder(new EmptyBorder(10, 14, 10, 14));

        boton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                boton.setBackground(colorHover);
            }

            @Override
            public void mouseExited(MouseEvent e) {
                boton.setBackground(colorNormal);
            }
        });
        return boton;
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

        if (filaSeleccionada == -1) {
            if (piezaClicada != null && piezaClicada.getColor().equals(juego.getTurnoActual())) {
                filaSeleccionada = fila;
                colSeleccionada = col;
                actualizarVistaCompleta();
            }
            return;
        }

        if (piezaClicada != null && piezaClicada.getColor().equals(juego.getTurnoActual())) {
            filaSeleccionada = fila;
            colSeleccionada = col;
            actualizarVistaCompleta();
            return;
        }

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
        // Actualizar tarjeta visual de turno según el bando activo
        boolean esTurnoBlancas = juego.getTurnoActual().equals("BLANCAS");
        if (esTurnoBlancas) {
            panelTarjetaTurno.setBackground(new Color(245, 245, 238));
            lblTurno.setForeground(new Color(30, 30, 30));
            lblEstado.setForeground(new Color(45, 110, 50));
        } else {
            panelTarjetaTurno.setBackground(new Color(32, 32, 32));
            lblTurno.setForeground(new Color(245, 245, 238));
            lblEstado.setForeground(new Color(145, 210, 120));
        }

        String modo = juego.isVsIA() ? " (vs IA)" : " (2P)";
        lblTurno.setText("Turno: " + juego.getTurnoActual() + modo);
        lblEstado.setText(juego.getMensajeEstado());
        // Formatear el historial en columnas limpias de pares de jugadas
        StringBuilder sb = new StringBuilder();
        ColaHistorial copia = juego.getColaHistorial().clonar();
        if (copia.estaVacia()) {
            sb.append("  Aún no hay movimientos.\n  Haz clic en una pieza blanca\n  para comenzar la partida.");
        }else{

            int turnoNum = 1;
        while (!copia.estaVacia()) {
            Movimiento movBlancas = (Movimiento) copia.desencolar();
            String col1 = String.format("%2d. %-11s", turnoNum++, movBlancas.getNotacion());
                String col2 = "";
                if (!copia.estaVacia()) {
                    Movimiento movNegras = (Movimiento) copia.desencolar();
                    col2 = " │  " + movNegras.getNotacion();
                }
                sb.append(col1).append(col2).append("\n");
        }
        
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

        Timer timer = new Timer(1500, null);
        timer.setInitialDelay(800);
        timer.addActionListener(e -> {
        // Restablecer colores de casillas antes de aplicar el siguiente movimiento
             for (int f = 0; f < 8; f++) {
                for (int c = 0; c < 8; c++) {
                    casillas[f][c].setBackground(((f + c) % 2 == 0) ? colorClaro : colorOscuro);
                }
            }
            if (!colaRepeticion.estaVacia()) {
                Movimiento m = (Movimiento) colaRepeticion.desencolar();
                String simbolo = casillas[m.getFilaOrigen()][m.getColOrigen()].getText();
                casillas[m.getFilaOrigen()][m.getColOrigen()].setText("");
                casillas[m.getFilaDestino()][m.getColDestino()].setText(simbolo);
                // Resaltar casilla de origen y destino
                casillas[m.getFilaOrigen()][m.getColOrigen()].setBackground(colorSeleccion);
                casillas[m.getFilaDestino()][m.getColDestino()].setBackground(colorDestinoValido);
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
     * Formatea una cadena de elementos separados por un delimitador en columnas ordenadas.
     * Qué hace: Divide la secuencia en elementos individuales y los alinea en filas de N columnas.
     * Qué recibe: textoCrudo (String) salida de la estructura, separador (String), columnas (int).
     * Qué devuelve: String con los elementos organizados en tabla monoespaciada.
     */
    private String formatearEnColumnas(String textoCrudo, String separador, int columnas) {
        if (textoCrudo.equals("Árbol vacío") || textoCrudo.equals("Sin elementos")) {
            return textoCrudo;
        }
        String[] items = textoCrudo.split(separador);
        StringBuilder sb = new StringBuilder();
        int contador = 0;
        for (String item : items) {
            String limpio = item.trim();
            if (!limpio.isEmpty() && !limpio.equals("NULL")) {
                sb.append(String.format("%-20s", limpio));
                contador++;
                if (contador % columnas == 0) {
                    sb.append("\n");
                }
            }
        }
        return sb.toString();
    }

    /**
     * Crea un panel con scroll y fuente monoespaciada para mostrar recorridos ordenados.
     * Qué hace: Configura un JTextArea no editable con márgenes limpios dentro de un JScrollPane.
     * Qué recibe: contenido (String) texto formateado a mostrar.
     * Qué devuelve: JScrollPane listo para insertar en pestañas o diálogos.
     */
    private JScrollPane crearPanelTextoOrdenado(String contenido) {
        JTextArea area = new JTextArea(14, 74);
        area.setEditable(false);
        area.setFont(new Font("Monospaced", Font.PLAIN, 12));
        area.setMargin(new Insets(10, 12, 10, 12));
        area.setText(contenido);
        area.setCaretPosition(0);
        return new JScrollPane(area);
    }

    /**
     * Muestra en pestañas ordenadas el recorrido hacia adelante y hacia atrás de la Lista Doble.
     * Qué hace: Organiza en columnas ambos sentidos de recorrido de piezasVivas.
     * Qué recibe: No recibe parámetros.
     * Qué devuelve: void (no retorna valor).
     */
    private void mostrarInfoListaDoble() {
        JTabbedPane pestanas = new JTabbedPane();

        String adelante = "Total de piezas vivas en tablero: " + juego.getPiezasVivas().tamano()
                + "\nSentido: inicio -> fin (usando puntero sig)\n"
                + "============================================================================\n\n"
                + formatearEnColumnas(juego.getPiezasVivas().recorrerAdelante(), "<->", 4);

        String atras = "Total de piezas vivas en tablero: " + juego.getPiezasVivas().tamano()
                + "\nSentido: fin -> inicio (usando puntero ant)\n"
                + "============================================================================\n\n"
                + formatearEnColumnas(juego.getPiezasVivas().recorrerAtras(), "<->", 4);

        pestanas.addTab("Hacia Adelante (Inicio -> Fin)", crearPanelTextoOrdenado(adelante));
        pestanas.addTab("Hacia Atrás (Fin -> Inicio)", crearPanelTextoOrdenado(atras));

        JOptionPane.showMessageDialog(this, pestanas, "Lista Doblemente Enlazada - Piezas Vivas", JOptionPane.INFORMATION_MESSAGE);
    }

    /**
     * Muestra en pestañas separadas los recorridos Inorden, Preorden y Posorden del Árbol IA.
     * Qué hace: Presenta cada recorrido en una pestaña dedicada con nodos alineados en columnas.
     * Qué recibe: No recibe parámetros.
     * Qué devuelve: void (no retorna valor).
     */
    private void mostrarInfoArbolIA() {
        JTabbedPane pestanas = new JTabbedPane();

        String txtInorden = "RECORRIDO INORDEN (Izquierda - Raíz - Derecha)\n"
                + "Ordena las jugadas evaluadas de MENOR a MAYOR puntaje táctico:\n"
                + "============================================================================\n\n"
                + formatearEnColumnas(juego.getArbolEvaluacion().inorden(), "(?<=\\])\\s+", 3);

        String txtPreorden = "RECORRIDO PREORDEN (Raíz - Izquierda - Derecha)\n"
                + "Visita primero la raíz y luego sus subárboles:\n"
                + "============================================================================\n\n"
                + formatearEnColumnas(juego.getArbolEvaluacion().preorden(), "(?<=\\])\\s+", 3);

        String txtPosorden = "RECORRIDO POSORDEN (Izquierda - Derecha - Raíz)\n"
                + "Visita primero los nodos hijos y al final la raíz:\n"
                + "============================================================================\n\n"
                + formatearEnColumnas(juego.getArbolEvaluacion().posorden(), "(?<=\\])\\s+", 3);

        pestanas.addTab("Inorden (Ordenado)", crearPanelTextoOrdenado(txtInorden));
        pestanas.addTab("Preorden (Raíz-Izq-Der)", crearPanelTextoOrdenado(txtPreorden));
        pestanas.addTab("Posorden (Izq-Der-Raíz)", crearPanelTextoOrdenado(txtPosorden));

        JOptionPane.showMessageDialog(this, pestanas, "Árbol Binario de Búsqueda (IA)", JOptionPane.INFORMATION_MESSAGE);
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

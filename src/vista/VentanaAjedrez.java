package vista;

import estructuras.ColaHistorial;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.*;
import javax.swing.border.BevelBorder;
import javax.swing.border.CompoundBorder;
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
    private Casilla3D[][] casillas = new Casilla3D[8][8];
    private JLabel lblTurno;
    private JLabel lblEstado;
    private JPanel panelTarjetaTurno;
    private JTextArea areaHistorial;

    private int filaSeleccionada = -1;
    private int colSeleccionada = -1;
    private boolean bloqueadoPorRepeticion = false;
    // Paleta de colores moderna
    private final Color fondoPrincipal = new  Color(244, 240, 232);
    private final Color fondoPanelLateral = new Color(252, 250, 245);
    private final Color fondoHistorial = new Color(248, 246, 240);
    private final Color colorClaro = new Color(238, 238, 210);
    private final Color colorOscuro = new Color(118, 150, 105);
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

        setTitle("Ajedrez  3D - Proyecto Final Estructuras de Datos");
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
        barraMenu.setBackground(new Color(235, 229, 218));
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
     * Construye la cuadrícula interactiva de 8x8 del tablero con marco y casillas 3D.
     * Qué hace: Crea las 64 casillas 3D y les asigna el evento de clic para seleccionar y mover piezas.
     * Qué recibe: No recibe parámetros.
     * Qué devuelve: void (no retorna valor).
     */

    private void inicializarTablero() {
        JPanel contenedorMarco = new JPanel(new BorderLayout());
        contenedorMarco.setBackground(fondoPrincipal);
        contenedorMarco.setBorder(new EmptyBorder(12, 14, 12, 4));
         // Marco exterior con efecto 3D madera suave
        JPanel marcoMadera3D = new JPanel(new BorderLayout());
        marcoMadera3D.setBackground(new Color(176, 145, 110));
        marcoMadera3D.setBorder(new CompoundBorder(
                BorderFactory.createBevelBorder(BevelBorder.RAISED, new Color(214, 188, 156), new Color(120, 95, 68)),
                new EmptyBorder(8, 8, 8, 8)
        ));


        JPanel panelTablero = new JPanel(new GridLayout(8, 8));
        panelTablero.setBorder(BorderFactory.createBevelBorder(BevelBorder.LOWERED));
        for (int fila = 0; fila < 8; fila++) {
            for (int col = 0; col < 8; col++) {
                final int f = fila;
                final int c = col;

                Casilla3D boton = new Casilla3D();
                boton.addActionListener(e -> manejarClicCasilla(f, c));

                casillas[fila][col] = boton;
                panelTablero.add(boton);
            }
        }
        marcoMadera3D.add(panelTablero, BorderLayout.CENTER);
        contenedorMarco.add(marcoMadera3D, BorderLayout.CENTER);
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
        panelDerecho.setBackground(fondoPrincipal);
        panelDerecho.setBorder(new EmptyBorder(12, 4, 12, 14));

        JPanel tarjetaContenedora = new JPanel(new BorderLayout(10, 12));
        tarjetaContenedora.setBackground(fondoPanelLateral);
        tarjetaContenedora.setBorder(new CompoundBorder(
                BorderFactory.createBevelBorder(BevelBorder.RAISED, Color.WHITE, new Color(210, 205, 195)),
                new EmptyBorder(12, 12, 12, 12)
        ));

        // 1. Tarjeta superior de Turno y Estado
        panelTarjetaTurno = new JPanel(new GridLayout(2, 1, 2, 4));
        panelTarjetaTurno.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createBevelBorder(BevelBorder.RAISED, Color.WHITE, new Color(195, 192, 184)),
                new EmptyBorder(10, 10, 10, 10)
        ));

        lblTurno = new JLabel("Turno: BLANCAS", SwingConstants.CENTER);
        lblTurno.setFont(new Font("SansSerif", Font.BOLD, 16));

        lblEstado = new JLabel("Partida en curso", SwingConstants.CENTER);
        lblEstado.setFont(new Font("SansSerif", Font.PLAIN, 13));

        panelTarjetaTurno.add(lblTurno);
        panelTarjetaTurno.add(lblEstado);
        tarjetaContenedora.add(panelTarjetaTurno, BorderLayout.NORTH);

        // 2. Sección central de Historial estilizado
        JPanel panelHistorialContenedor = new JPanel(new BorderLayout(0, 6));
        panelHistorialContenedor.setBackground(fondoPanelLateral);

        JLabel lblTituloHistorial = new JLabel("  HISTORIAL DE JUGADAS (COLA FIFO)");
        lblTituloHistorial.setFont(new Font("SansSerif", Font.BOLD, 12));
        lblTituloHistorial.setForeground(new Color(75, 72, 66));
        lblTituloHistorial.setOpaque(true);
        lblTituloHistorial.setBackground(new Color(232, 228, 218));
        lblTituloHistorial.setBorder(new CompoundBorder(
                new LineBorder(new Color(205, 200, 190), 1, true),
                new EmptyBorder(7, 6, 7, 6)
        ));

        areaHistorial = new JTextArea();
        areaHistorial.setEditable(false);
        areaHistorial.setBackground(fondoHistorial);
        areaHistorial.setForeground(new Color(55, 52, 48));
        areaHistorial.setFont(new Font("Monospaced", Font.PLAIN, 13));
        areaHistorial.setMargin(new Insets(10, 12, 10, 12));

        JScrollPane scrollHistorial = new JScrollPane(areaHistorial);
        scrollHistorial.setBorder(BorderFactory.createBevelBorder(BevelBorder.LOWERED));

        panelHistorialContenedor.add(lblTituloHistorial, BorderLayout.NORTH);
        panelHistorialContenedor.add(scrollHistorial, BorderLayout.CENTER);
        tarjetaContenedora.add(panelHistorialContenedor, BorderLayout.CENTER);

        // 3. Botones inferiores en colores PASTEL con efecto 3D elevado
        JPanel panelBotones = new JPanel(new GridLayout(4, 1, 0, 8));
        panelBotones.setBackground(fondoPanelLateral);

        JButton btnDeshacer = crearBotonEstilizado("↩  Deshacer Jugada (Pila)", new Color(253, 224, 186), new Color(250, 206, 152));
        JButton btnRehacer = crearBotonEstilizado("↪  Rehacer Jugada (Pila)", new Color(196, 226, 250), new Color(168, 210, 245));
        JButton btnRepetir = crearBotonEstilizado("▶  Repetir Partida (Cola)", new Color(194, 237, 206), new Color(165, 224, 182));
        JButton btnPiezas = crearBotonEstilizado("⇄  Piezas Vivas (Lista Doble)", new Color(225, 208, 242), new Color(210, 186, 235));

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
        tarjetaContenedora.add(panelBotones, BorderLayout.SOUTH);

        panelDerecho.add(tarjetaContenedora, BorderLayout.CENTER);
        add(panelDerecho, BorderLayout.EAST);
    }


   /**
     * Crea un botón en tono pastel con borde biselado 3D y texto oscuro legible.
     * Qué hace: Configura relieve 3D (RAISED al reposar, LOWERED al hacer clic) y efecto hover suave.
     * Qué recibe: texto (String), colorPastel (Color), colorHover (Color).
     * Qué devuelve: JButton estilizado con apariencia 3D pastel.
     */
    private JButton crearBotonEstilizado(String texto, Color colorPastel, Color colorHover) {
        JButton boton = new JButton(texto);
        boton.setFont(new Font("SansSerif", Font.BOLD, 13));
        boton.setForeground(new Color(48, 45, 42));
        boton.setBackground(colorPastel);
        boton.setOpaque(true);
        boton.setFocusPainted(false);
        boton.setCursor(new Cursor(Cursor.HAND_CURSOR));

        CompoundBorder bordeElevado = new CompoundBorder(
                BorderFactory.createBevelBorder(BevelBorder.RAISED, colorPastel.brighter(), colorPastel.darker()),
                new EmptyBorder(8, 12, 8, 12)
        );
        CompoundBorder bordePresionado = new CompoundBorder(
                BorderFactory.createBevelBorder(BevelBorder.LOWERED, colorPastel.brighter(), colorPastel.darker()),
                new EmptyBorder(8, 12, 8, 12)
        );
        boton.setBorder(bordeElevado);
        boton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                boton.setBackground(colorHover);
            }

            @Override
            public void mouseExited(MouseEvent e) {
                boton.setBackground(colorPastel);
                boton.setBorder(bordeElevado);
            }
            @Override
            public void mousePressed(MouseEvent e) {
                boton.setBorder(bordePresionado);
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
            Timer pausaIA = new Timer(450, evt -> {
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
     * Actualiza las piezas dibujadas en 3D, los colores de casillas, el turno y el historial en pantalla.
     * Qué hace: Sincroniza todos los componentes visuales de Swing con el estado actual de JuegoAjedrez.
     * Qué recibe: No recibe parámetros.
     * Qué devuelve: void (no retorna valor).
     */

    private void actualizarVistaCompleta() {
        for (int f = 0; f < 8; f++) {
            for (int c = 0; c < 8; c++) {
                                Pieza p = juego.getPieza(f, c);
                casillas[f][c].setSimboloPieza(p != null ? p.getSimbolo() : "");

                Color base = ((f + c) % 2 == 0) ? colorClaro : colorOscuro;
                if (f == filaSeleccionada && c == colSeleccionada) {
                    casillas[f][c].setColorCasilla(colorSeleccion);
                } else if (filaSeleccionada != -1 && juego.esMovimientoLegal(filaSeleccionada, colSeleccionada, f, c, juego.getTurnoActual())) {
                    casillas[f][c].setColorCasilla(colorDestinoValido);
                } else {
                    casillas[f][c].setColorCasilla(base);
                }

            }
        }
         // Tarjeta visual de turno en tonos pastel suaves
        boolean esTurnoBlancas = juego.getTurnoActual().equals("BLANCAS");
        if (esTurnoBlancas) {
            panelTarjetaTurno.setBackground(new Color(252, 250, 242));
            lblTurno.setForeground(new Color(45, 45, 42));
            lblEstado.setForeground(new Color(65, 125, 70));
        } else {
            panelTarjetaTurno.setBackground(new Color(224, 232, 222));
            lblTurno.setForeground(new Color(35, 50, 38));
            lblEstado.setForeground(new Color(55, 105, 65));
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
     * Qué hace: Limpia el tablero visual, resalta origen/destino y aplica cada jugada cada 1.5 segundos.
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
                casillas[f][c].setSimboloPieza(inicial[f][c]);
                casillas[f][c].setColorCasilla(((f + c) % 2 == 0) ? colorClaro : colorOscuro);
            }
        }

        Timer timer = new Timer(1500, null);
        timer.setInitialDelay(800);
        timer.addActionListener(e -> {
        // Restablecer colores de casillas antes de aplicar el siguiente movimiento
             for (int f = 0; f < 8; f++) {
                for (int c = 0; c < 8; c++) {
                casillas[f][c].setColorCasilla(((f + c) % 2 == 0) ? colorClaro : colorOscuro);

                }
            }
            if (!colaRepeticion.estaVacia()) {
                Movimiento m = (Movimiento) colaRepeticion.desencolar();
                                String simbolo = casillas[m.getFilaOrigen()][m.getColOrigen()].getSimboloPieza();
                casillas[m.getFilaOrigen()][m.getColOrigen()].setSimboloPieza("");
                casillas[m.getFilaDestino()][m.getColDestino()].setSimboloPieza(simbolo);
                // Resaltar casilla de origen y destino
                casillas[m.getFilaOrigen()][m.getColOrigen()].setColorCasilla(colorSeleccion);
                casillas[m.getFilaDestino()][m.getColDestino()].setColorCasilla(colorDestinoValido);

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
    /**
     * Componente gráfico personalizado que dibuja cada casilla y pieza con efecto de volumen 3D.
     */
    private static class Casilla3D extends JButton {
        private Color colorCasilla = Color.WHITE;
        private String simboloPieza = "";

        public Casilla3D() {
            setFocusPainted(false);
            setBorderPainted(false);
            setContentAreaFilled(false);
            setCursor(new Cursor(Cursor.HAND_CURSOR));
        }

        public void setColorCasilla(Color colorCasilla) {
            this.colorCasilla = colorCasilla;
            repaint();
        }

        public void setSimboloPieza(String simboloPieza) {
            this.simboloPieza = simboloPieza;
            repaint();
        }

        public String getSimboloPieza() {
            return simboloPieza;
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

            int w = getWidth();
            int h = getHeight();

            // 1. Degradado sutil en la casilla para profundidad 3D
            GradientPaint grad = new GradientPaint(0, 0, colorCasilla.brighter(), 0, h, colorCasilla.darker());
            g2.setPaint(grad);
            g2.fillRect(0, 0, w, h);

            // 2. Bisel 3D (borde claro arriba/izquierda, borde sombra abajo/derecha)
            g2.setColor(new Color(255, 255, 255, 95));
            g2.drawLine(0, 0, w - 1, 0);
            g2.drawLine(0, 0, 0, h - 1);

            g2.setColor(new Color(0, 0, 0, 45));
            g2.drawLine(0, h - 1, w - 1, h - 1);
            g2.drawLine(w - 1, 0, w - 1, h - 1);

            // 3. Dibujar pieza con sombra proyectada 3D
            if (simboloPieza != null && !simboloPieza.isEmpty()) {
                Font fuente = new Font("Serif", Font.PLAIN, Math.min(w, h) - 16);
                g2.setFont(fuente);
                FontMetrics fm = g2.getFontMetrics();
                int x = (w - fm.stringWidth(simboloPieza)) / 2;
                int y = (h - fm.getHeight()) / 2 + fm.getAscent();

                // Sombra proyectada sobre el tablero
                g2.setColor(new Color(0, 0, 0, 80));
                g2.drawString(simboloPieza, x + 3, y + 4);

                // Cuerpo principal de la pieza
                g2.setColor(new Color(30, 30, 30));
                g2.drawString(simboloPieza, x, y);
            }
            g2.dispose();
        }
    }

}

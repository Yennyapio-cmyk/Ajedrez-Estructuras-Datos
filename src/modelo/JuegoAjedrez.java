package modelo;

import estructuras.*;

/**
 * Motor principal del juego de Ajedrez. Controla el tablero 8x8, valida las reglas oficiales,
 * garantiza el fin de partida (sin bucles infinitos) e integra las 5 estructuras de datos propias.
 */
public class JuegoAjedrez {
    private Pieza[][] tablero;
    private ListaCircularTurnos turnos;
    private PilaJugadas pilaDeshacer;
    private PilaJugadas pilaRehacer;
    private ColaHistorial colaHistorial;
    private ListaDoblePiezas piezasVivas;
    private ArbolIA arbolEvaluacion;

    private boolean vsIA;
    private boolean partidaTerminada;
    private String mensajeEstado;
    private int contadorRegla50Movimientos;

    /**
     * Constructor del motor de juego.
     * Qué hace: Inicializa las 5 estructuras de datos y prepara una nueva partida.
     * Qué recibe: vsIA (boolean) true si se juega contra la IA del árbol, false para 2 jugadores.
     * Qué devuelve: Una instancia de JuegoAjedrez lista para jugar.
     */
    public JuegoAjedrez(boolean vsIA) {
        this.tablero = new Pieza[8][8];
        this.turnos = new ListaCircularTurnos();
        this.pilaDeshacer = new PilaJugadas();
        this.pilaRehacer = new PilaJugadas();
        this.colaHistorial = new ColaHistorial();
        this.piezasVivas = new ListaDoblePiezas();
        this.arbolEvaluacion = new ArbolIA();
        iniciarNuevaPartida(vsIA);
    }

    /**
     * Reinicia el tablero, los turnos y todas las estructuras de datos para empezar de cero.
     * Qué hace: Coloca las 32 piezas iniciales, configura la lista circular y limpia pilas/colas.
     * Qué recibe: vsIA (boolean) modo de juego seleccionado.
     * Qué devuelve: void (no retorna valor).
     */
    public void iniciarNuevaPartida(boolean vsIA) {
        this.vsIA = vsIA;
        this.partidaTerminada = false;
        this.mensajeEstado = "Partida en curso";
        this.contadorRegla50Movimientos = 0;

        tablero = new Pieza[8][8];
        pilaDeshacer.limpiar();
        pilaRehacer.limpiar();
        colaHistorial = new ColaHistorial();
        piezasVivas.limpiar();
        arbolEvaluacion = new ArbolIA();

        // Configurar Lista Circular de turnos
        turnos = new ListaCircularTurnos();
        turnos.insertarFinal("BLANCAS");
        turnos.insertarFinal("NEGRAS");

        // Colocar piezas negras (filas 0 y 1) y blancas (filas 6 y 7)
        String[] ordenMayores = {"TORRE", "CABALLO", "ALFIL", "DAMA", "REY", "ALFIL", "CABALLO", "TORRE"};
        for (int col = 0; col < 8; col++) {
            tablero[0][col] = new Pieza(ordenMayores[col], "NEGRAS");
            tablero[1][col] = new Pieza("PEON", "NEGRAS");
            tablero[6][col] = new Pieza("PEON", "BLANCAS");
            tablero[7][col] = new Pieza(ordenMayores[col], "BLANCAS");
        }

        actualizarListaDoblePiezas();
    }

    /**
     * Sincroniza la Lista Doblemente Enlazada con todas las piezas vivas actuales en el tablero.
     * Qué hace: Vacía la lista doble e inserta cada pieza activa encontrada en el tablero.
     * Qué recibe: No recibe parámetros.
     * Qué devuelve: void (no retorna valor).
     */
    private void actualizarListaDoblePiezas() {
        piezasVivas.limpiar();
        for (int f = 0; f < 8; f++) {
            for (int c = 0; c < 8; c++) {
                if (tablero[f][c] != null) {
                    piezasVivas.insertarFinal(tablero[f][c].toString());
                }
            }
        }
    }

    /**
     * Intenta ejecutar un movimiento desde (fOrig, cOrig) hasta (fDest, cDest).
     * Qué hace: Valida turno, legalidad del movimiento, actualiza tablero, pilas, cola, turnos y estado final.
     * Qué recibe: fOrig, cOrig, fDest, cDest (int) coordenadas de origen y destino.
     * Qué devuelve: boolean (true si el movimiento fue legal y se realizó, false si fue inválido).
     */
    public boolean moverPieza(int fOrig, int cOrig, int fDest, int cDest) {
        if (partidaTerminada) {
            return false;
        }
        if (!esCoordenadaValida(fOrig, cOrig) || !esCoordenadaValida(fDest, cDest)) {
            return false;
        }
        Pieza pieza = tablero[fOrig][cOrig];
        String turnoActual = (String) turnos.obtenerTurnoActual();

        if (pieza == null || !pieza.getColor().equals(turnoActual)) {
            return false;
        }
        if (!esMovimientoLegal(fOrig, cOrig, fDest, cDest, turnoActual)) {
            return false;
        }

        // Guardar estado previo para la Pila de Deshacer
        Pieza[][] copiaTablero = clonarTablero(tablero);
        Pieza capturada = tablero[fDest][cDest];
        Movimiento mov = new Movimiento(fOrig, cOrig, fDest, cDest, pieza.clonar(), capturada, copiaTablero);

        // Ejecutar el movimiento en el tablero
        tablero[fDest][cDest] = pieza;
        tablero[fOrig][cOrig] = null;

        // Coronación automática del peón al llegar al extremo opuesto
        if (pieza.getTipo().equals("PEON") && (fDest == 0 || fDest == 7)) {
            pieza.setTipo("DAMA");
        }

        // Actualizar estructuras de datos
        pilaDeshacer.apilar(mov);
        pilaRehacer.limpiar();
        colaHistorial.encolar(mov);
        actualizarListaDoblePiezas();

        // Actualizar regla de 50 movimientos (evita partidas infinitas)
        if (pieza.getTipo().equals("PEON") || capturada != null) {
            contadorRegla50Movimientos = 0;
        } else {
            contadorRegla50Movimientos++;
        }

        // Cambiar turno en la Lista Circular y verificar condiciones de fin de juego
        turnos.siguienteTurno();
        verificarFinDePartida();
        return true;
    }

    /**
     * Ejecuta el turno de la Inteligencia Artificial usando el Árbol Binario de Búsqueda (ArbolIA).
     * Qué hace: Evalúa todas las jugadas legales de las negras, las inserta en ArbolIA por puntaje
     *           y ejecuta la de mayor valor táctico.
     * Qué recibe: No recibe parámetros.
     * Qué devuelve: boolean (true si la IA realizó una jugada, false si no pudo mover).
     */
    public boolean ejecutarTurnoIA() {
        if (partidaTerminada || !vsIA || !turnos.obtenerTurnoActual().equals("NEGRAS")) {
            return false;
        }

        arbolEvaluacion = new ArbolIA();
        Movimiento[] posibles = new Movimiento[256];
        int totalPosibles = 0;
        int mejorPuntaje = -9999;
        Movimiento mejorMov = null;

        for (int fO = 0; fO < 8; fO++) {
            for (int cO = 0; cO < 8; cO++) {
                Pieza p = tablero[fO][cO];
                if (p != null && p.getColor().equals("NEGRAS")) {
                    for (int fD = 0; fD < 8; fD++) {
                        for (int cD = 0; cD < 8; cD++) {
                            if (esMovimientoLegal(fO, cO, fD, cD, "NEGRAS")) {
                                Pieza objetivo = tablero[fD][cD];
                                int puntaje = (objetivo != null) ? objetivo.getValorTactico() * 10 : 0;
                                // Bonificación posicional por acercarse al centro del tablero
                                puntaje += (4 - Math.abs(3 - fD)) + (4 - Math.abs(3 - cD));

                                Movimiento candidato = new Movimiento(fO, cO, fD, cD, p, objetivo, null);
                                arbolEvaluacion.insertar(puntaje, candidato.getNotacion());
                                posibles[totalPosibles++] = candidato;

                                if (puntaje > mejorPuntaje) {
                                    mejorPuntaje = puntaje;
                                    mejorMov = candidato;
                                }
                            }
                        }
                    }
                }
            }
        }

        if (mejorMov != null) {
            return moverPieza(mejorMov.getFilaOrigen(), mejorMov.getColOrigen(),
                              mejorMov.getFilaDestino(), mejorMov.getColDestino());
        }
        return false;
    }

    /**
     * Deshace el último movimiento realizado utilizando la Pila (LIFO).
     * Qué hace: Extrae el movimiento de pilaDeshacer, lo pasa a pilaRehacer y restaura el tablero.
     * Qué recibe: No recibe parámetros.
     * Qué devuelve: boolean (true si se deshizo la jugada, false si la pila estaba vacía).
     */
    public boolean deshacerJugada() {
        if (pilaDeshacer.estaVacia()) {
            return false;
        }
        Movimiento ultimo = (Movimiento) pilaDeshacer.desapilar();
        Pieza[][] tableroAntesDeDeshacer = clonarTablero(tablero);
        Movimiento paraRehacer = new Movimiento(
            ultimo.getFilaOrigen(), ultimo.getColOrigen(),
            ultimo.getFilaDestino(), ultimo.getColDestino(),
            ultimo.getPiezaMovida(), ultimo.getPiezaCapturada(), tableroAntesDeDeshacer
        );
        pilaRehacer.apilar(paraRehacer);

        tablero = clonarTablero(ultimo.getEstadoTableroAnterior());
        turnos.siguienteTurno();
        partidaTerminada = false;
        mensajeEstado = "Jugada deshecha";
        actualizarListaDoblePiezas();
        return true;
    }

    /**
     * Rehace un movimiento previamente deshecho utilizando la Pila de rehacer.
     * Qué hace: Extrae de pilaRehacer, apila de nuevo en pilaDeshacer y reaplica el tablero.
     * Qué recibe: No recibe parámetros.
     * Qué devuelve: boolean (true si se rehizo el movimiento, false si no había jugadas deshechas).
     */
    public boolean rehacerJugada() {
        if (pilaRehacer.estaVacia()) {
            return false;
        }
        Movimiento mov = (Movimiento) pilaRehacer.desapilar();
        Pieza[][] tableroPrevio = clonarTablero(tablero);
        Movimiento paraDeshacer = new Movimiento(
            mov.getFilaOrigen(), mov.getColOrigen(),
            mov.getFilaDestino(), mov.getColDestino(),
            mov.getPiezaMovida(), mov.getPiezaCapturada(), tableroPrevio
        );
        pilaDeshacer.apilar(paraDeshacer);

        tablero = clonarTablero(mov.getEstadoTableroAnterior());
        turnos.siguienteTurno();
        actualizarListaDoblePiezas();
        verificarFinDePartida();
        return true;
    }

    /**
     * Evalúa si la partida ha terminado por Jaque Mate, Ahogado, Material Insuficiente o Regla de 50 jugadas.
     * Qué hace: Comprueba si el jugador en turno tiene movimientos legales; si no tiene, declara ganador o empate.
     * Qué recibe: No recibe parámetros.
     * Qué devuelve: void (no retorna valor).
     */
    private void verificarFinDePartida() {
        String turnoActual = (String) turnos.obtenerTurnoActual();
        boolean tieneMovimientos = tieneAlgunMovimientoLegal(turnoActual);
        boolean enJaque = estaReyEnJaque(turnoActual, tablero);

        if (!tieneMovimientos) {
            partidaTerminada = true;
            if (enJaque) {
                String ganador = turnoActual.equals("BLANCAS") ? "NEGRAS" : "BLANCAS";
                mensajeEstado = "¡JAQUE MATE! Ganan las " + ganador + ".";
            } else {
                mensajeEstado = "¡EMPATE POR REY AHOGADO! No hay movimientos legales.";
            }
            return;
        }

        if (esMaterialInsuficiente()) {
            partidaTerminada = true;
            mensajeEstado = "¡EMPATE! Material insuficiente para dar jaque mate.";
            return;
        }

        if (contadorRegla50Movimientos >= 100) { // 50 jugadas completas por bando
            partidaTerminada = true;
            mensajeEstado = "¡EMPATE! Regla de 50 movimientos sin capturas ni avance de peón.";
            return;
        }

        if (enJaque) {
            mensajeEstado = "¡JAQUE al Rey de las " + turnoActual + "!";
        } else {
            mensajeEstado = "Turno de las " + turnoActual;
        }
    }

    /**
     * Comprueba si solo quedan los dos reyes en el tablero (imposible dar mate).
     * Qué hace: Recorre el tablero contando cuántas piezas activas quedan.
     * Qué recibe: No recibe parámetros.
     * Qué devuelve: boolean (true si solo quedan 2 piezas, false si hay más).
     */
    private boolean esMaterialInsuficiente() {
        int conteo = 0;
        for (int f = 0; f < 8; f++) {
            for (int c = 0; c < 8; c++) {
                if (tablero[f][c] != null) conteo++;
            }
        }
        return conteo <= 2;
    }

    /**
     * Verifica si un bando posee al menos un movimiento legal disponible.
     * Qué hace: Prueba todas las combinaciones origen-destino para las piezas del color indicado.
     * Qué recibe: color (String) "BLANCAS" o "NEGRAS".
     * Qué devuelve: boolean (true si puede mover, false si está bloqueado).
     */
    private boolean tieneAlgunMovimientoLegal(String color) {
        for (int fO = 0; fO < 8; fO++) {
            for (int cO = 0; cO < 8; cO++) {
                if (tablero[fO][cO] != null && tablero[fO][cO].getColor().equals(color)) {
                    for (int fD = 0; fD < 8; fD++) {
                        for (int cD = 0; cD < 8; cD++) {
                            if (esMovimientoLegal(fO, cO, fD, cD, color)) {
                                return true;
                            }
                        }
                    }
                }
            }
        }
        return false;
    }

    /**
     * Comprueba si un movimiento cumple la geometría de la pieza y no deja al propio Rey en jaque.
     * Qué hace: Valida reglas de desplazamiento y simula el tablero para verificar seguridad del Rey.
     * Qué recibe: fO, cO, fD, cD (int) coordenadas, color (String) bando que mueve.
     * Qué devuelve: boolean (true si es completamente legal, false si viola alguna regla).
     */
    public boolean esMovimientoLegal(int fO, int cO, int fD, int cD, String color) {
        if (!esMovimientoGeometricoValido(fO, cO, fD, cD, tablero)) {
            return false;
        }
        // Simular el movimiento en un tablero temporal para verificar que el Rey no quede en jaque
        Pieza[][] simulado = clonarTablero(tablero);
        simulado[fD][cD] = simulado[fO][cO];
        simulado[fO][cO] = null;
        return !estaReyEnJaque(color, simulado);
    }

    /**
     * Determina si el Rey de un color está siendo atacado en un estado de tablero dado.
     * Qué hace: Localiza al Rey y revisa si alguna pieza enemiga puede capturar su casilla.
     * Qué recibe: colorRey (String) bando a evaluar, estadoTablero (Pieza[][]) matriz del tablero.
     * Qué devuelve: boolean (true si está en jaque, false si está seguro).
     */
    public boolean estaReyEnJaque(String colorRey, Pieza[][] estadoTablero) {
        int filaRey = -1, colRey = -1;
        for (int f = 0; f < 8; f++) {
            for (int c = 0; c < 8; c++) {
                Pieza p = estadoTablero[f][c];
                if (p != null && p.getTipo().equals("REY") && p.getColor().equals(colorRey)) {
                    filaRey = f;
                    colRey = c;
                    break;
                }
            }
        }
        if (filaRey == -1) return true;

        String enemigo = colorRey.equals("BLANCAS") ? "NEGRAS" : "BLANCAS";
        for (int f = 0; f < 8; f++) {
            for (int c = 0; c < 8; c++) {
                Pieza p = estadoTablero[f][c];
                if (p != null && p.getColor().equals(enemigo)) {
                    if (esMovimientoGeometricoValido(f, c, filaRey, colRey, estadoTablero)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    /**
     * Valida las reglas básicas de movimiento de cada pieza sin considerar jaque al propio Rey.
     * Qué hace: Evalúa trayectoria, obstáculos y capturas según el tipo de pieza.
     * Qué recibe: fO, cO, fD, cD (int) coordenadas, estado (Pieza[][]) tablero evaluado.
     * Qué devuelve: boolean (true si la pieza puede desplazarse a esa casilla, false si no).
     */
    private boolean esMovimientoGeometricoValido(int fO, int cO, int fD, int cD, Pieza[][] estado) {
        if (fO == fD && cO == cD) return false;
        Pieza origen = estado[fO][cO];
        Pieza destino = estado[fD][cD];
        if (origen == null) return false;
        if (destino != null && destino.getColor().equals(origen.getColor())) return false;

        int df = fD - fO;
        int dc = cD - cO;
        int absF = Math.abs(df);
        int absC = Math.abs(dc);

        switch (origen.getTipo()) {
            case "PEON":
                int dir = origen.getColor().equals("BLANCAS") ? -1 : 1;
                int filaInicial = origen.getColor().equals("BLANCAS") ? 6 : 1;
                // Avance simple
                if (dc == 0 && df == dir && destino == null) return true;
                // Avance doble desde fila inicial
                if (dc == 0 && fO == filaInicial && df == 2 * dir && destino == null && estado[fO + dir][cO] == null) {
                    return true;
                }
                // Captura diagonal
                if (absC == 1 && df == dir && destino != null) return true;
                return false;

            case "TORRE":
                if (df == 0 || dc == 0) return caminoDespejado(fO, cO, fD, cD, estado);
                return false;

            case "ALFIL":
                if (absF == absC) return caminoDespejado(fO, cO, fD, cD, estado);
                return false;

            case "DAMA":
                if (df == 0 || dc == 0 || absF == absC) return caminoDespejado(fO, cO, fD, cD, estado);
                return false;

            case "CABALLO":
                return (absF == 2 && absC == 1) || (absF == 1 && absC == 2);

            case "REY":
                return absF <= 1 && absC <= 1;
        }
        return false;
    }

    /**
     * Comprueba que no haya piezas intermedias bloqueando una trayectoria recta o diagonal.
     * Qué hace: Avanza paso a paso desde origen hasta antes de destino verificando casillas vacías.
     * Qué recibe: fO, cO, fD, cD (int) coordenadas, estado (Pieza[][]) matriz del tablero.
     * Qué devuelve: boolean (true si el camino está libre, false si hay bloqueo).
     */
    private boolean caminoDespejado(int fO, int cO, int fD, int cD, Pieza[][] estado) {
        int pasoF = Integer.compare(fD, fO);
        int pasoC = Integer.compare(cD, cO);
        int f = fO + pasoF;
        int c = cO + pasoC;
        while (f != fD || c != cD) {
            if (estado[f][c] != null) return false;
            f += pasoF;
            c += pasoC;
        }
        return true;
    }

    /**
     * Crea una copia independiente de una matriz de tablero 8x8.
     * Qué hace: Clona cada pieza en una nueva matriz para evitar referencias compartidas.
     * Qué recibe: original (Pieza[][]) matriz a clonar.
     * Qué devuelve: Pieza[][] nueva matriz clonada.
     */
    private Pieza[][] clonarTablero(Pieza[][] original) {
        Pieza[][] copia = new Pieza[8][8];
        for (int f = 0; f < 8; f++) {
            for (int c = 0; c < 8; c++) {
                if (original[f][c] != null) {
                    copia[f][c] = original[f][c].clonar();
                }
            }
        }
        return copia;
    }

    /**
     * Verifica que una coordenada esté dentro de los límites 0..7 del tablero.
     * Qué hace: Comprueba rango válido de fila y columna.
     * Qué recibe: fila (int), col (int).
     * Qué devuelve: boolean (true si está dentro del tablero, false si está fuera).
     */
    private boolean esCoordenadaValida(int fila, int col) {
        return fila >= 0 && fila < 8 && col >= 0 && col < 8;
    }

    /**
     * Finaliza la partida manualmente por rendición del jugador actual.
     * Qué hace: Marca la partida como terminada y anuncia al ganador.
     * Qué recibe: No recibe parámetros.
     * Qué devuelve: void (no retorna valor).
     */
    public void rendirse() {
        if (!partidaTerminada) {
            partidaTerminada = true;
            String turno = (String) turnos.obtenerTurnoActual();
            String ganador = turno.equals("BLANCAS") ? "NEGRAS" : "BLANCAS";
            mensajeEstado = "Las " + turno + " se rindieron. ¡Ganan las " + ganador + "!";
        }
    }

    // Getters para la interfaz gráfica
    public Pieza getPieza(int fila, int col) { return tablero[fila][col]; }
    public String getTurnoActual() { return (String) turnos.obtenerTurnoActual(); }
    public boolean isVsIA() { return vsIA; }
    public boolean isPartidaTerminada() { return partidaTerminada; }
    public String getMensajeEstado() { return mensajeEstado; }
    public ColaHistorial getColaHistorial() { return colaHistorial; }
    public ListaDoblePiezas getPiezasVivas() { return piezasVivas; }
    public ArbolIA getArbolEvaluacion() { return arbolEvaluacion; }
    public ListaCircularTurnos getTurnos() { return turnos; }
    public PilaJugadas getPilaDeshacer() { return pilaDeshacer; }
}


package modelo;

/**
 * Encapsula toda la información de una jugada realizada en el tablero.
 * Permite apilar estados para Deshacer/Rehacer, encolar el historial y evaluar jugadas en la IA.
 */
public class Movimiento {
    private int filaOrigen;
    private int colOrigen;
    private int filaDestino;
    private int colDestino;
    private Pieza piezaMovida;
    private Pieza piezaCapturada;
    private Pieza[][] estadoTableroAnterior;
    private String notacion;

    /**
     * Constructor del movimiento.
     * Qué hace: Guarda coordenadas, piezas involucradas, copia del tablero previo y notación.
     * Qué recibe: filaOrigen, colOrigen, filaDestino, colDestino (int), piezaMovida, piezaCapturada (Pieza),
     *             tableroAnterior (Pieza[][]).
     * Qué devuelve: Una instancia de Movimiento.
     */
    public Movimiento(int filaOrigen, int colOrigen, int filaDestino, int colDestino,
                      Pieza piezaMovida, Pieza piezaCapturada, Pieza[][] tableroAnterior) {
        this.filaOrigen = filaOrigen;
        this.colOrigen = colOrigen;
        this.filaDestino = filaDestino;
        this.colDestino = colDestino;
        this.piezaMovida = piezaMovida;
        this.piezaCapturada = piezaCapturada;
        this.estadoTableroAnterior = tableroAnterior;
        this.notacion = construirNotacion();
    }

    /**
     * Genera la notación algebraica simplificada de la jugada (ej. "♙ e2 -> e4").
     * Qué hace: Convierte índices de matriz (0-7) a columnas (a-h) y filas (8-1).
     * Qué recibe: No recibe parámetros.
     * Qué devuelve: String con el texto legible del movimiento.
     */
    private String construirNotacion() {
        char colIni = (char) ('a' + colOrigen);
        int filaIni = 8 - filaOrigen;
        char colFin = (char) ('a' + colDestino);
        int filaFin = 8 - filaDestino;
        String captura = (piezaCapturada != null) ? " x " + piezaCapturada.getSimbolo() : " -> ";
        return piezaMovida.getSimbolo() + " " + colIni + filaIni + captura + colFin + filaFin;
    }

    public int getFilaOrigen() { return filaOrigen; }
    public int getColOrigen() { return colOrigen; }
    public int getFilaDestino() { return filaDestino; }
    public int getColDestino() { return colDestino; }
    public Pieza getPiezaMovida() { return piezaMovida; }
    public Pieza getPiezaCapturada() { return piezaCapturada; }
    public Pieza[][] getEstadoTableroAnterior() { return estadoTableroAnterior; }
    public String getNotacion() { return notacion; }

    @Override
    public String toString() {
        return notacion;
    }
}


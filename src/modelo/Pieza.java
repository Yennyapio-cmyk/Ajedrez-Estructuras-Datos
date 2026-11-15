package modelo;

/**
 * Representa una pieza individual del tablero de ajedrez con su tipo, color,
 * símbolo gráfico Unicode y valor táctico para la evaluación del árbol de IA.
 */
public class Pieza {
    private String tipo;   // "PEON", "TORRE", "CABALLO", "ALFIL", "DAMA", "REY"
    private String color;  // "BLANCAS", "NEGRAS"

    /**
     * Constructor de la pieza.
     * Qué hace: Inicializa una pieza con su tipo y bando.
     * Qué recibe: tipo (String) nombre de la pieza, color (String) "BLANCAS" o "NEGRAS".
     * Qué devuelve: Una instancia de Pieza.
     */
    public Pieza(String tipo, String color) {
        this.tipo = tipo;
        this.color = color;
    }

    /**
     * Obtiene el tipo de pieza.
     * Qué hace: Retorna el nombre del tipo de pieza.
     * Qué recibe: No recibe parámetros.
     * Qué devuelve: String con el tipo ("PEON", "TORRE", etc.).
     */
    public String getTipo() {
        return tipo;
    }

    /**
     * Modifica el tipo de pieza (usado en la coronación de peones).
     * Qué hace: Actualiza el atributo tipo.
     * Qué recibe: tipo (String) nuevo tipo de pieza.
     * Qué devuelve: void (no retorna valor).
     */
    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    /**
     * Obtiene el color del bando al que pertenece la pieza.
     * Qué hace: Retorna "BLANCAS" o "NEGRAS".
     * Qué recibe: No recibe parámetros.
     * Qué devuelve: String con el color de la pieza.
     */
    public String getColor() {
        return color;
    }

    /**
     * Devuelve el símbolo Unicode correspondiente para dibujar la pieza en Swing.
     * Qué hace: Evalúa el color y el tipo de pieza para retornar su carácter gráfico.
     * Qué recibe: No recibe parámetros.
     * Qué devuelve: String con el símbolo Unicode de ajedrez.
     */
    public String getSimbolo() {
        if (color.equals("BLANCAS")) {
            switch (tipo) {
                case "REY": return "♔";
                case "DAMA": return "♕";
                case "TORRE": return "♖";
                case "ALFIL": return "♗";
                case "CABALLO": return "♘";
                case "PEON": return "♙";
            }
        } else {
            switch (tipo) {
                case "REY": return "♚";
                case "DAMA": return "♛";
                case "TORRE": return "♜";
                case "ALFIL": return "♝";
                case "CABALLO": return "♞";
                case "PEON": return "♟";
            }
        }
        return "";
    }

    /**
     * Devuelve el puntaje material de la pieza para que el ArbolIA evalúe capturas.
     * Qué hace: Asigna un peso numérico estándar según la importancia de la pieza.
     * Qué recibe: No recibe parámetros.
     * Qué devuelve: int con el valor táctico de la pieza.
     */
    public int getValorTactico() {
        switch (tipo) {
            case "PEON": return 10;
            case "CABALLO": return 30;
            case "ALFIL": return 32;
            case "TORRE": return 50;
            case "DAMA": return 90;
            case "REY": return 900;
            default: return 0;
        }
    }

    /**
     * Crea una copia exacta de esta pieza.
     * Qué hace: Instancia una nueva Pieza con los mismos atributos.
     * Qué recibe: No recibe parámetros.
     * Qué devuelve: Pieza clonada.
     */
    public Pieza clonar() {
        return new Pieza(this.tipo, this.color);
    }

    /**
     * Representación legible de la pieza para listas y reportes.
     * Qué hace: Combina el símbolo, tipo y color en un texto corto.
     * Qué recibe: No recibe parámetros.
     * Qué devuelve: String descriptivo de la pieza.
     */
    @Override
    public String toString() {
        return getSimbolo() + " " + tipo + " (" + color.charAt(0) + ")";
    }
}


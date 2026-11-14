package estructuras;

/**
 * Representa un nodo de un Árbol Binario de Búsqueda (BST).
 * Almacena una clave numérica (puntaje táctico), una descripción de jugada y dos hijos (izq y der).
 */
public class NodoArbol {
    private int puntaje;
    private String jugada;
    private NodoArbol izq;
    private NodoArbol der;

    /**
     * Constructor del nodo del árbol binario.
     * Qué hace: Inicializa el nodo con su puntaje clave, descripción y sus hijos en null.
     * Qué recibe: puntaje (int) valor táctico de la jugada, jugada (String) movimiento asociado.
     * Qué devuelve: Una instancia de NodoArbol.
     */
    public NodoArbol(int puntaje, String jugada) {
        this.puntaje = puntaje;
        this.jugada = jugada;
        this.izq = null;
        this.der = null;
    }

    /**
     * Obtiene el puntaje clave del nodo.
     * Qué hace: Retorna el valor entero utilizado para ordenar en el BST.
     * Qué recibe: No recibe parámetros.
     * Qué devuelve: int con el puntaje.
     */
    public int getPuntaje() {
        return puntaje;
    }

    /**
     * Modifica el puntaje del nodo.
     * Qué hace: Actualiza el atributo puntaje.
     * Qué recibe: puntaje (int) nuevo valor táctico.
     * Qué devuelve: void (no retorna valor).
     */
    public void setPuntaje(int puntaje) {
        this.puntaje = puntaje;
    }

    /**
     * Obtiene la descripción de la jugada guardada.
     * Qué hace: Retorna el texto de la jugada asociada al nodo.
     * Qué recibe: No recibe parámetros.
     * Qué devuelve: String con la descripción del movimiento.
     */
    public String getJugada() {
        return jugada;
    }

    /**
     * Modifica la descripción de la jugada guardada.
     * Qué hace: Actualiza el atributo jugada.
     * Qué recibe: jugada (String) nueva descripción.
     * Qué devuelve: void (no retorna valor).
     */
    public void setJugada(String jugada) {
        this.jugada = jugada;
    }

    /**
     * Obtiene el hijo izquierdo del nodo.
     * Qué hace: Devuelve la referencia izq.
     * Qué recibe: No recibe parámetros.
     * Qué devuelve: NodoArbol izquierdo.
     */
    public NodoArbol getIzq() {
        return izq;
    }

    /**
     * Establece el hijo izquierdo del nodo.
     * Qué hace: Asigna la referencia izq.
     * Qué recibe: izq (NodoArbol) subárbol izquierdo.
     * Qué devuelve: void (no retorna valor).
     */
    public void setIzq(NodoArbol izq) {
        this.izq = izq;
    }

    /**
     * Obtiene el hijo derecho del nodo.
     * Qué hace: Devuelve la referencia der.
     * Qué recibe: No recibe parámetros.
     * Qué devuelve: NodoArbol derecho.
     */
    public NodoArbol getDer() {
        return der;
    }

    /**
     * Establece el hijo derecho del nodo.
     * Qué hace: Asigna la referencia der.
     * Qué recibe: der (NodoArbol) subárbol derecho.
     * Qué devuelve: void (no retorna valor).
     */
    public void setDer(NodoArbol der) {
        this.der = der;
    }
}


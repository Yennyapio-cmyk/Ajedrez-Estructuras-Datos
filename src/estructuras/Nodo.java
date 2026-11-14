package estructuras;

/**
 * Representa un nodo simple para estructuras dinámicas (Pila, Cola y Lista Circular).
 * Contiene un dato genérico y un puntero (referencia) al siguiente nodo.
 */
public class Nodo {
    private Object valor;
    private Nodo sig;

    /**
     * Constructor del nodo simple.
     * Qué hace: Inicializa el nodo con un dato y su puntero siguiente en null.
     * Qué recibe: valor (Object) el dato o pieza/jugada a almacenar.
     * Qué devuelve: Una instancia de Nodo.
     */
    public Nodo(Object valor) {
        this.valor = valor;
        this.sig = null;
    }

    /**
     * Obtiene el dato almacenado en el nodo.
     * Qué hace: Retorna el valor guardado.
     * Qué recibe: No recibe parámetros.
     * Qué devuelve: Object con el contenido del nodo.
     */
    public Object getValor() {
        return valor;
    }

    /**
     * Modifica el dato almacenado en el nodo.
     * Qué hace: Actualiza el atributo valor.
     * Qué recibe: valor (Object) nuevo dato a guardar.
     * Qué devuelve: void (no retorna valor).
     */
    public void setValor(Object valor) {
        this.valor = valor;
    }

    /**
     * Obtiene la referencia al siguiente nodo.
     * Qué hace: Devuelve el apuntador sig.
     * Qué recibe: No recibe parámetros.
     * Qué devuelve: Nodo siguiente en la estructura (o null si es el último).
     */
    public Nodo getSig() {
        return sig;
    }

    /**
     * Establece la referencia al siguiente nodo.
     * Qué hace: Enlaza este nodo con otro nodo siguiente.
     * Qué recibe: sig (Nodo) referencia al próximo nodo.
     * Qué devuelve: void (no retorna valor).
     */
    public void setSig(Nodo sig) {
        this.sig = sig;
    }
}

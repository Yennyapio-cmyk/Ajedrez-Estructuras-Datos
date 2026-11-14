package estructuras;

/**
 * Representa un nodo para una Lista Doblemente Enlazada.
 * Posee un dato y dos apuntadores: uno al nodo siguiente y otro al nodo anterior.
 */
public class NodoDoble {
    private Object valor;
    private NodoDoble sig;
    private NodoDoble ant;

    /**
     * Constructor del nodo doble.
     * Qué hace: Inicializa el valor y deja ambos punteros (sig y ant) en null.
     * Qué recibe: valor (Object) dato a almacenar en el nodo.
     * Qué devuelve: Una instancia de NodoDoble.
     */
    public NodoDoble(Object valor) {
        this.valor = valor;
        this.sig = null;
        this.ant = null;
    }

    /**
     * Obtiene el dato guardado en el nodo.
     * Qué hace: Retorna el atributo valor.
     * Qué recibe: No recibe parámetros.
     * Qué devuelve: Object almacenado.
     */
    public Object getValor() {
        return valor;
    }

    /**
     * Actualiza el dato guardado en el nodo.
     * Qué hace: Asigna un nuevo valor al nodo.
     * Qué recibe: valor (Object) nuevo contenido.
     * Qué devuelve: void (no retorna valor).
     */
    public void setValor(Object valor) {
        this.valor = valor;
    }

    /**
     * Obtiene la referencia al nodo siguiente.
     * Qué hace: Devuelve el puntero sig.
     * Qué recibe: No recibe parámetros.
     * Qué devuelve: NodoDoble siguiente.
     */
    public NodoDoble getSig() {
        return sig;
    }

    /**
     * Establece la referencia al nodo siguiente.
     * Qué hace: Asigna el apuntador sig.
     * Qué recibe: sig (NodoDoble) nodo posterior.
     * Qué devuelve: void (no retorna valor).
     */
    public void setSig(NodoDoble sig) {
        this.sig = sig;
    }

    /**
     * Obtiene la referencia al nodo anterior.
     * Qué hace: Devuelve el puntero ant.
     * Qué recibe: No recibe parámetros.
     * Qué devuelve: NodoDoble anterior.
     */
    public NodoDoble getAnt() {
        return ant;
    }

    /**
     * Establece la referencia al nodo anterior.
     * Qué hace: Asigna el apuntador ant.
     * Qué recibe: ant (NodoDoble) nodo previo.
     * Qué devuelve: void (no retorna valor).
     */
    public void setAnt(NodoDoble ant) {
        this.ant = ant;
    }
}


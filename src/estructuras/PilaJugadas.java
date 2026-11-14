package estructuras;

/**
 * Implementación propia de una Pila (LIFO: Último en entrar, primero en salir)
 * mediante nodos enlazados. Se utiliza en el ajedrez para deshacer y rehacer jugadas.
 */
public class PilaJugadas {
    private Nodo cima;
    private int tamano;

    /**
     * Constructor de la pila.
     * Qué hace: Inicializa la pila vacía con cima en null y tamaño en 0.
     * Qué recibe: No recibe parámetros.
     * Qué devuelve: Una instancia de PilaJugadas vacía.
     */
    public PilaJugadas() {
        this.cima = null;
        this.tamano = 0;
    }

    /**
     * Verifica si la pila no contiene elementos.
     * Qué hace: Comprueba si el puntero cima es igual a null.
     * Qué recibe: No recibe parámetros.
     * Qué devuelve: boolean (true si está vacía, false si tiene elementos).
     */
    public boolean estaVacia() {
        return cima == null;
    }

    /**
     * Inserta un elemento en la parte superior (cima) de la pila (Push).
     * Qué hace: Crea un nuevo nodo, lo enlaza a la cima actual y actualiza cima.
     * Qué recibe: dato (Object) jugada o estado a apilar.
     * Qué devuelve: void (no retorna valor).
     */
    public void apilar(Object dato) {
        Nodo nuevo = new Nodo(dato);
        if (estaVacia()) {
            cima = nuevo;
        } else {
            nuevo.setSig(cima);
            cima = nuevo;
        }
        tamano++;
    }

    /**
     * Extrae y elimina el elemento en la cima de la pila (Pop).
     * Qué hace: Toma el valor de cima, mueve cima al siguiente nodo y reduce el tamaño.
     * Qué recibe: No recibe parámetros.
     * Qué devuelve: Object con el dato extraído, o null si la pila está vacía.
     */
    public Object desapilar() {
        if (estaVacia()) {
            return null;
        }
        Object dato = cima.getValor();
        cima = cima.getSig();
        tamano--;
        return dato;
    }

    /**
     * Consulta el elemento superior de la pila sin eliminarlo (Peek/Top).
     * Qué hace: Retorna el valor almacenado en el nodo cima sin modificar enlaces.
     * Qué recibe: No recibe parámetros.
     * Qué devuelve: Object en la cima, o null si está vacía.
     */
    public Object verTope() {
        if (estaVacia()) {
            return null;
        }
        return cima.getValor();
    }

    /**
     * Devuelve la cantidad de elementos almacenados en la pila.
     * Qué hace: Retorna el contador interno de nodos.
     * Qué recibe: No recibe parámetros.
     * Qué devuelve: int con el número total de elementos en la pila.
     */
    public int tamano() {
        return tamano;
    }

    /**
     * Vacía completamente la pila.
     * Qué hace: Reinicia cima a null y tamaño a 0 para liberar referencias.
     * Qué recibe: No recibe parámetros.
     * Qué devuelve: void (no retorna valor).
     */
    public void limpiar() {
        cima = null;
        tamano = 0;
    }
}

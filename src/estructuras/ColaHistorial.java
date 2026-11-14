package estructuras;

/**
 * Implementación propia de una Cola (FIFO: Primero en entrar, primero en salir)
 * mediante nodos enlazados. Se utiliza para almacenar el historial de jugadas y repetir la partida.
 */
public class ColaHistorial {
    private Nodo frente;
    private Nodo fin;
    private int tamano;

    /**
     * Constructor de la cola.
     * Qué hace: Inicializa los punteros frente y fin en null y tamaño en 0.
     * Qué recibe: No recibe parámetros.
     * Qué devuelve: Una instancia de ColaHistorial vacía.
     */
    public ColaHistorial() {
        this.frente = null;
        this.fin = null;
        this.tamano = 0;
    }

    /**
     * Verifica si la cola no tiene elementos.
     * Qué hace: Comprueba si el puntero frente es null.
     * Qué recibe: No recibe parámetros.
     * Qué devuelve: boolean (true si está vacía, false en caso contrario).
     */
    public boolean estaVacia() {
        return frente == null;
    }

    /**
     * Inserta un nuevo elemento al final de la cola (Enqueue).
     * Qué hace: Crea un nodo y lo enlaza después del nodo fin actual.
     * Qué recibe: dato (Object) jugada registrada para el historial.
     * Qué devuelve: void (no retorna valor).
     */
    public void encolar(Object dato) {
        Nodo nuevo = new Nodo(dato);
        if (estaVacia()) {
            frente = nuevo;
            fin = nuevo;
        } else {
            fin.setSig(nuevo);
            fin = nuevo;
        }
        tamano++;
    }

    /**
     * Extrae y elimina el primer elemento de la cola (Dequeue).
     * Qué hace: Obtiene el dato de frente y avanza el puntero frente al siguiente nodo.
     * Qué recibe: No recibe parámetros.
     * Qué devuelve: Object con la primera jugada en espera, o null si está vacía.
     */
    public Object desencolar() {
        if (estaVacia()) {
            return null;
        }
        Object dato = frente.getValor();
        if (frente == fin) {
            frente = null;
            fin = null;
        } else {
            frente = frente.getSig();
        }
        tamano--;
        return dato;
    }

    /**
     * Consulta el elemento ubicado al frente de la cola sin eliminarlo.
     * Qué hace: Retorna el valor del nodo frente.
     * Qué recibe: No recibe parámetros.
     * Qué devuelve: Object al inicio de la cola, o null si está vacía.
     */
    public Object verFrente() {
        if (estaVacia()) {
            return null;
        }
        return frente.getValor();
    }

    /**
     * Retorna el número de elementos en la cola.
     * Qué hace: Devuelve el valor del atributo tamano.
     * Qué recibe: No recibe parámetros.
     * Qué devuelve: int con la cantidad de jugadas en la cola.
     */
    public int tamano() {
        return tamano;
    }

    /**
     * Genera una copia de la cola actual para reproducir la partida sin perder el historial original.
     * Qué hace: Recorre los nodos desde frente hasta fin y los encola en una nueva ColaHistorial.
     * Qué recibe: No recibe parámetros.
     * Qué devuelve: ColaHistorial duplicada con los mismos elementos en orden.
     */
    public ColaHistorial clonar() {
        ColaHistorial copia = new ColaHistorial();
        Nodo aux = frente;
        while (aux != null) {
            copia.encolar(aux.getValor());
            aux = aux.getSig();
        }
        return copia;
    }
}

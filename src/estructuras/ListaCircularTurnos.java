package estructuras;

/**
 * Implementación propia de una Lista Circular Simplemente Enlazada.
 * Se utiliza en el ajedrez para alternar de forma continua los turnos entre Blancas y Negras.
 */
public class ListaCircularTurnos {
    private Nodo cabecera;
    private Nodo actual;
    private int tamano;

    /**
     * Constructor de la lista circular.
     * Qué hace: Inicializa la cabecera y el puntero actual en null y el tamaño en 0.
     * Qué recibe: No recibe parámetros.
     * Qué devuelve: Una instancia de ListaCircularTurnos vacía.
     */
    public ListaCircularTurnos() {
        this.cabecera = null;
        this.actual = null;
        this.tamano = 0;
    }

    /**
     * Verifica si la lista circular no contiene nodos.
     * Qué hace: Evalúa si cabecera es null.
     * Qué recibe: No recibe parámetros.
     * Qué devuelve: boolean (true si está vacía, false si contiene elementos).
     */
    public boolean estaVacia() {
        return cabecera == null;
    }

    /**
     * Inserta un elemento al inicio de la lista circular.
     * Qué hace: Crea un nodo, enlaza el último nodo al nuevo y actualiza cabecera.
     * Qué recibe: dato (Object) nombre o identificador del jugador/turno.
     * Qué devuelve: void (no retorna valor).
     */
    public void insertarInicio(Object dato) {
        Nodo nuevo = new Nodo(dato);
        if (estaVacia()) {
            cabecera = nuevo;
            nuevo.setSig(cabecera);
            actual = cabecera;
        } else {
            Nodo aux = cabecera;
            while (aux.getSig() != cabecera) {
                aux = aux.getSig();
            }
            aux.setSig(nuevo);
            nuevo.setSig(cabecera);
            cabecera = nuevo;
        }
        tamano++;
    }

    /**
     * Inserta un elemento al final de la lista circular.
     * Qué hace: Recorre hasta el último nodo y enlaza el nuevo nodo apuntando a cabecera.
     * Qué recibe: dato (Object) nombre o identificador del jugador/turno.
     * Qué devuelve: void (no retorna valor).
     */
    public void insertarFinal(Object dato) {
        Nodo nuevo = new Nodo(dato);
        if (estaVacia()) {
            cabecera = nuevo;
            nuevo.setSig(cabecera);
            actual = cabecera;
        } else {
            Nodo aux = cabecera;
            while (aux.getSig() != cabecera) {
                aux = aux.getSig();
            }
            aux.setSig(nuevo);
            nuevo.setSig(cabecera);
        }
        tamano++;
    }

    /**
     * Inserta un elemento en una posición específica (base 0).
     * Qué hace: Ubica el nodo previo a la posición deseada y reacomoda los enlaces circulares.
     * Qué recibe: posicion (int) índice de inserción, dato (Object) elemento a insertar.
     * Qué devuelve: boolean (true si se insertó correctamente, false si la posición es inválida).
     */
    public boolean insertarPosicion(int posicion, Object dato) {
        if (posicion < 0 || posicion > tamano) {
            return false;
        }
        if (posicion == 0) {
            insertarInicio(dato);
            return true;
        }
        if (posicion == tamano) {
            insertarFinal(dato);
            return true;
        }
        Nodo nuevo = new Nodo(dato);
        Nodo aux = cabecera;
        for (int i = 0; i < posicion - 1; i++) {
            aux = aux.getSig();
        }
        nuevo.setSig(aux.getSig());
        aux.setSig(nuevo);
        tamano++;
        return true;
    }

    /**
     * Elimina el primer nodo cuyo valor coincida con el dato buscado.
     * Qué hace: Localiza el nodo y actualiza el puntero del nodo anterior manteniendo el ciclo.
     * Qué recibe: dato (Object) valor a eliminar.
     * Qué devuelve: boolean (true si se eliminó, false si no existía).
     */
    public boolean eliminar(Object dato) {
        if (estaVacia()) {
            return false;
        }
        Nodo aux = cabecera;
        Nodo ant = null;
        do {
            if (aux.getValor().equals(dato)) {
                if (tamano == 1) {
                    cabecera = null;
                    actual = null;
                } else if (aux == cabecera) {
                    Nodo ultimo = cabecera;
                    while (ultimo.getSig() != cabecera) {
                        ultimo = ultimo.getSig();
                    }
                    cabecera = cabecera.getSig();
                    ultimo.setSig(cabecera);
                    if (actual == aux) actual = cabecera;
                } else {
                    ant.setSig(aux.getSig());
                    if (actual == aux) actual = aux.getSig();
                }
                tamano--;
                return true;
            }
            ant = aux;
            aux = aux.getSig();
        } while (aux != cabecera);
        return false;
    }

    /**
     * Busca si un elemento existe dentro de la lista circular.
     * Qué hace: Recorre el ciclo completo comparando cada nodo con el dato buscado.
     * Qué recibe: dato (Object) valor a buscar.
     * Qué devuelve: boolean (true si lo encuentra, false en caso contrario).
     */
    public boolean buscar(Object dato) {
        if (estaVacia()) {
            return false;
        }
        Nodo aux = cabecera;
        do {
            if (aux.getValor().equals(dato)) {
                return true;
            }
            aux = aux.getSig();
        } while (aux != cabecera);
        return false;
    }

    /**
     * Recorre toda la lista circular una vez y devuelve su contenido en texto.
     * Qué hace: Concatena los valores desde cabecera hasta volver a cabecera.
     * Qué recibe: No recibe parámetros.
     * Qué devuelve: String con todos los elementos separados por flechas.
     */
    public String recorrer() {
        if (estaVacia()) {
            return "Lista vacía";
        }
        StringBuilder sb = new StringBuilder();
        Nodo aux = cabecera;
        do {
            sb.append(aux.getValor()).append(" -> ");
            aux = aux.getSig();
        } while (aux != cabecera);
        sb.append("(vuelve a ").append(cabecera.getValor()).append(")");
        return sb.toString();
    }

    /**
     * Obtiene el jugador del turno actual sin avanzar el puntero.
     * Qué hace: Retorna el valor almacenado en el nodo actual.
     * Qué recibe: No recibe parámetros.
     * Qué devuelve: Object correspondiente al turno vigente.
     */
    public Object obtenerTurnoActual() {
        return (actual != null) ? actual.getValor() : null;
    }

    /**
     * Avanza al siguiente turno en el ciclo.
     * Qué hace: Mueve el puntero actual al siguiente nodo de la lista circular.
     * Qué recibe: No recibe parámetros.
     * Qué devuelve: Object correspondiente al nuevo turno activo.
     */
    public Object siguienteTurno() {
        if (actual != null) {
            actual = actual.getSig();
            return actual.getValor();
        }
        return null;
    }

    /**
     * Reinicia el turno al primer elemento de la lista circular.
     * Qué hace: Posiciona el puntero actual nuevamente en cabecera.
     * Qué recibe: No recibe parámetros.
     * Qué devuelve: void (no retorna valor).
     */
    public void reiniciarTurnos() {
        actual = cabecera;
    }
}


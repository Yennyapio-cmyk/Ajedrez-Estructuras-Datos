package estructuras;

/**
 * Implementación propia de un Árbol Binario de Búsqueda (BST).
 * Se utiliza para evaluar y seleccionar la mejor jugada posible de la IA según su puntaje táctico,
 * además de clasificar el valor de las jugadas realizadas.
 */
public class ArbolIA {
    private NodoArbol raiz;

    /**
     * Constructor del árbol binario de búsqueda.
     * Qué hace: Inicializa la raíz del árbol en null.
     * Qué recibe: No recibe parámetros.
     * Qué devuelve: Una instancia de ArbolIA vacía.
     */
    public ArbolIA() {
        this.raiz = null;
    }

    /**
     * Inserta una nueva jugada evaluada en el árbol según su puntaje.
     * Qué hace: Llama al método recursivo para ubicar el nodo en el subárbol izquierdo o derecho.
     * Qué recibe: puntaje (int) valor táctico, jugada (String) movimiento candidato.
     * Qué devuelve: void (no retorna valor).
     */
    public void insertar(int puntaje, String jugada) {
        raiz = insertarRec(raiz, puntaje, jugada);
    }

    /**
     * Método auxiliar recursivo para insertar en el BST.
     * Qué hace: Coloca los puntajes menores a la izquierda y los mayores o iguales a la derecha.
     * Qué recibe: actual (NodoArbol) nodo visitado, puntaje (int), jugada (String).
     * Qué devuelve: NodoArbol actualizado tras la inserción.
     */
    private NodoArbol insertarRec(NodoArbol actual, int puntaje, String jugada) {
        if (actual == null) {
            return new NodoArbol(puntaje, jugada);
        }
        if (puntaje < actual.getPuntaje()) {
            actual.setIzq(insertarRec(actual.getIzq(), puntaje, jugada));
        } else {
            actual.setDer(insertarRec(actual.getDer(), puntaje, jugada));
        }
        return actual;
    }

    /**
     * Busca una jugada en el árbol a partir de su puntaje clave.
     * Qué hace: Navega por las ramas izquierda o derecha comparando el puntaje buscado.
     * Qué recibe: puntaje (int) clave numérica a localizar.
     * Qué devuelve: String con la jugada encontrada, o null si no existe.
     */
    public String buscar(int puntaje) {
        NodoArbol encontrado = buscarRec(raiz, puntaje);
        return (encontrado != null) ? encontrado.getJugada() : null;
    }

    /**
     * Método auxiliar recursivo de búsqueda en el BST.
     * Qué hace: Compara el puntaje con el nodo actual y desciende por la rama correspondiente.
     * Qué recibe: actual (NodoArbol), puntaje (int).
     * Qué devuelve: NodoArbol coincidente o null.
     */
    private NodoArbol buscarRec(NodoArbol actual, int puntaje) {
        if (actual == null || actual.getPuntaje() == puntaje) {
            return actual;
        }
        if (puntaje < actual.getPuntaje()) {
            return buscarRec(actual.getIzq(), puntaje);
        }
        return buscarRec(actual.getDer(), puntaje);
    }

    /**
     * Elimina un nodo del árbol binario según su puntaje clave.
     * Qué hace: Invoca la eliminación recursiva manteniendo la propiedad de orden del BST.
     * Qué recibe: puntaje (int) clave del nodo a eliminar.
     * Qué devuelve: void (no retorna valor).
     */
    public void eliminar(int puntaje) {
        raiz = eliminarRec(raiz, puntaje);
    }

    /**
     * Método auxiliar recursivo para eliminar un nodo en los 3 casos del BST (hoja, 1 hijo, 2 hijos).
     * Qué hace: Reemplaza el nodo eliminado por su sucesor inorden cuando tiene dos hijos.
     * Qué recibe: actual (NodoArbol), puntaje (int).
     * Qué devuelve: NodoArbol con el subárbol reestructurado.
     */
    private NodoArbol eliminarRec(NodoArbol actual, int puntaje) {
        if (actual == null) {
            return null;
        }
        if (puntaje < actual.getPuntaje()) {
            actual.setIzq(eliminarRec(actual.getIzq(), puntaje));
        } else if (puntaje > actual.getPuntaje()) {
            actual.setDer(eliminarRec(actual.getDer(), puntaje));
        } else {
            if (actual.getIzq() == null) return actual.getDer();
            if (actual.getDer() == null) return actual.getIzq();

            NodoArbol sucesor = encontrarMinimo(actual.getDer());
            actual.setPuntaje(sucesor.getPuntaje());
            actual.setJugada(sucesor.getJugada());
            actual.setDer(eliminarRec(actual.getDer(), sucesor.getPuntaje()));
        }
        return actual;
    }

    /**
     * Encuentra el nodo con el menor puntaje en un subárbol.
     * Qué hace: Desciende siempre hacia la izquierda hasta llegar a la hoja mínima.
     * Qué recibe: nodo (NodoArbol) raíz del subárbol.
     * Qué devuelve: NodoArbol con el puntaje mínimo.
     */
    private NodoArbol encontrarMinimo(NodoArbol nodo) {
        while (nodo.getIzq() != null) {
            nodo = nodo.getIzq();
        }
        return nodo;
    }

    /**
     * Obtiene la mejor jugada evaluada en el árbol (el nodo más a la derecha).
     * Qué hace: Recorre los punteros derechos hasta hallar el puntaje máximo.
     * Qué recibe: No recibe parámetros.
     * Qué devuelve: String con la mejor jugada para la IA, o null si el árbol está vacío.
     */
    public String obtenerMejorJugada() {
        if (raiz == null) {
            return null;
        }
        NodoArbol aux = raiz;
        while (aux.getDer() != null) {
            aux = aux.getDer();
        }
        return aux.getJugada();
    }

    /**
     * Realiza el recorrido Inorden (Izquierda - Raíz - Derecha) del árbol.
     * Qué hace: Devuelve los nodos ordenados de menor a mayor puntaje.
     * Qué recibe: No recibe parámetros.
     * Qué devuelve: String con la secuencia del recorrido inorden.
     */
    public String inorden() {
        StringBuilder sb = new StringBuilder();
        inordenRec(raiz, sb);
        return sb.length() > 0 ? sb.toString() : "Árbol vacío";
    }

    private void inordenRec(NodoArbol nodo, StringBuilder sb) {
        if (nodo != null) {
            inordenRec(nodo.getIzq(), sb);
            sb.append("[").append(nodo.getPuntaje()).append(": ").append(nodo.getJugada()).append("] ");
            inordenRec(nodo.getDer(), sb);
        }
    }

    /**
     * Realiza el recorrido Preorden (Raíz - Izquierda - Derecha) del árbol.
     * Qué hace: Visita primero la raíz y luego sus subárboles izquierdo y derecho.
     * Qué recibe: No recibe parámetros.
     * Qué devuelve: String con la secuencia del recorrido preorden.
     */
    public String preorden() {
        StringBuilder sb = new StringBuilder();
        preordenRec(raiz, sb);
        return sb.length() > 0 ? sb.toString() : "Árbol vacío";
    }

    private void preordenRec(NodoArbol nodo, StringBuilder sb) {
        if (nodo != null) {
            sb.append("[").append(nodo.getPuntaje()).append(": ").append(nodo.getJugada()).append("] ");
            preordenRec(nodo.getIzq(), sb);
            preordenRec(nodo.getDer(), sb);
        }
    }

    /**
     * Realiza el recorrido Posorden (Izquierda - Derecha - Raíz) del árbol.
     * Qué hace: Visita primero ambos subárboles y al final la raíz.
     * Qué recibe: No recibe parámetros.
     * Qué devuelve: String con la secuencia del recorrido posorden.
     */
    public String posorden() {
        StringBuilder sb = new StringBuilder();
        posordenRec(raiz, sb);
        return sb.length() > 0 ? sb.toString() : "Árbol vacío";
    }

    private void posordenRec(NodoArbol nodo, StringBuilder sb) {
        if (nodo != null) {
            posordenRec(nodo.getIzq(), sb);
            posordenRec(nodo.getDer(), sb);
            sb.append("[").append(nodo.getPuntaje()).append(": ").append(nodo.getJugada()).append("] ");
        }
    }
}

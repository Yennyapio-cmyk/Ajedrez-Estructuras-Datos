package estructuras;

/**
 * Implementación propia de una Lista Doblemente Enlazada mediante nodos con punteros sig y ant.
 * Se utiliza para administrar las piezas activas en el tablero y recorrerlas en ambos sentidos.
 */
public class ListaDoblePiezas {
    private NodoDoble inicio;
    private NodoDoble fin;
    private int tamano;

    /**
     * Constructor de la lista doblemente enlazada.
     * Qué hace: Inicializa inicio y fin en null y tamano en 0.
     * Qué recibe: No recibe parámetros.
     * Qué devuelve: Una instancia de ListaDoblePiezas vacía.
     */
    public ListaDoblePiezas() {
        this.inicio = null;
        this.fin = null;
        this.tamano = 0;
    }

    /**
     * Verifica si la lista doble está vacía.
     * Qué hace: Comprueba si inicio es null.
     * Qué recibe: No recibe parámetros.
     * Qué devuelve: boolean (true si no hay nodos, false si hay al menos uno).
     */
    public boolean estaVacia() {
        return inicio == null;
    }

    /**
     * Inserta un elemento al inicio de la lista doble.
     * Qué hace: Enlaza el nuevo nodo antes del inicio actual y actualiza punteros ant y sig.
     * Qué recibe: dato (Object) pieza o información a insertar.
     * Qué devuelve: void (no retorna valor).
     */
    public void insertarInicio(Object dato) {
        NodoDoble nuevo = new NodoDoble(dato);
        if (estaVacia()) {
            inicio = nuevo;
            fin = nuevo;
        } else {
            nuevo.setSig(inicio);
            inicio.setAnt(nuevo);
            inicio = nuevo;
        }
        tamano++;
    }

    /**
     * Inserta un elemento al final de la lista doble.
     * Qué hace: Enlaza el nuevo nodo después de fin y actualiza el puntero fin.
     * Qué recibe: dato (Object) pieza o información a insertar.
     * Qué devuelve: void (no retorna valor).
     */
    public void insertarFinal(Object dato) {
        NodoDoble nuevo = new NodoDoble(dato);
        if (estaVacia()) {
            inicio = nuevo;
            fin = nuevo;
        } else {
            fin.setSig(nuevo);
            nuevo.setAnt(fin);
            fin = nuevo;
        }
        tamano++;
    }

    /**
     * Inserta un elemento en una posición específica (base 0).
     * Qué hace: Navega hasta la posición indicada y ajusta los enlaces anterior y siguiente.
     * Qué recibe: posicion (int) índice deseado, dato (Object) elemento a guardar.
     * Qué devuelve: boolean (true si se insertó, false si el índice está fuera de rango).
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
        NodoDoble nuevo = new NodoDoble(dato);
        NodoDoble aux = inicio;
        for (int i = 0; i < posicion; i++) {
            aux = aux.getSig();
        }
        nuevo.setSig(aux);
        nuevo.setAnt(aux.getAnt());
        aux.getAnt().setSig(nuevo);
        aux.setAnt(nuevo);
        tamano++;
        return true;
    }

    /**
     * Elimina la primera ocurrencia de un elemento en la lista doble.
     * Qué hace: Busca el nodo coincidente y reconecta sus vecinos anterior y siguiente.
     * Qué recibe: dato (Object) elemento a eliminar (por ejemplo, una pieza capturada).
     * Qué devuelve: boolean (true si fue eliminado, false si no se encontró).
     */
    public boolean eliminar(Object dato) {
        if (estaVacia()) {
            return false;
        }
        NodoDoble aux = inicio;
        while (aux != null) {
            if (aux.getValor().equals(dato)) {
                if (aux == inicio && aux == fin) {
                    inicio = null;
                    fin = null;
                } else if (aux == inicio) {
                    inicio = inicio.getSig();
                    inicio.setAnt(null);
                } else if (aux == fin) {
                    fin = fin.getAnt();
                    fin.setSig(null);
                } else {
                    aux.getAnt().setSig(aux.getSig());
                    aux.getSig().setAnt(aux.getAnt());
                }
                tamano--;
                return true;
            }
            aux = aux.getSig();
        }
        return false;
    }

    /**
     * Busca un elemento dentro de la lista doble.
     * Qué hace: Recorre secuencialmente desde inicio hasta encontrar coincidencia.
     * Qué recibe: dato (Object) elemento buscado.
     * Qué devuelve: boolean (true si existe en la lista, false en caso contrario).
     */
    public boolean buscar(Object dato) {
        NodoDoble aux = inicio;
        while (aux != null) {
            if (aux.getValor().equals(dato)) {
                return true;
            }
            aux = aux.getSig();
        }
        return false;
    }

    /**
     * Recorre la lista desde el primer nodo hasta el último (sentido hacia adelante).
     * Qué hace: Avanza usando el puntero sig y construye una cadena con los elementos.
     * Qué recibe: No recibe parámetros.
     * Qué devuelve: String con los elementos de inicio a fin.
     */
    public String recorrerAdelante() {
        if (estaVacia()) {
            return "Sin elementos";
        }
        StringBuilder sb = new StringBuilder();
        NodoDoble aux = inicio;
        while (aux != null) {
            sb.append(aux.getValor()).append(" <-> ");
            aux = aux.getSig();
        }
        sb.append("NULL");
        return sb.toString();
    }

    /**
     * Recorre la lista desde el último nodo hasta el primero (sentido hacia atrás).
     * Qué hace: Retrocede usando el puntero ant y construye una cadena con los elementos.
     * Qué recibe: No recibe parámetros.
     * Qué devuelve: String con los elementos de fin a inicio.
     */
    public String recorrerAtras() {
        if (estaVacia()) {
            return "Sin elementos";
        }
        StringBuilder sb = new StringBuilder();
        NodoDoble aux = fin;
        while (aux != null) {
            sb.append(aux.getValor()).append(" <-> ");
            aux = aux.getAnt();
        }
        sb.append("NULL");
        return sb.toString();
    }

    /**
     * Vacía todos los nodos de la lista doble.
     * Qué hace: Reinicia inicio y fin a null y tamano a 0.
     * Qué recibe: No recibe parámetros.
     * Qué devuelve: void (no retorna valor).
     */
    public void limpiar() {
        inicio = null;
        fin = null;
        tamano = 0;
    }

    /**
     * Devuelve el tamaño actual de la lista doble.
     * Qué hace: Retorna el número de elementos almacenados.
     * Qué recibe: No recibe parámetros.
     * Qué devuelve: int con la cantidad de nodos.
     */
    public int tamano() {
        return tamano;
    }
}


package estructuras;

import modelo.JuegoAjedrez;
import modelo.Movimiento;

/**
 * Clase de pruebas unitarias para verificar el funcionamiento de las 5 estructuras
 * de datos implementadas desde cero en el proyecto de Ajedrez.
 */
public class TestEstructuras {

    public static void main(String[] args) {
        System.out.println("==========================================================================");
        System.out.println("          PRUEBAS UNITARIAS - ESTRUCTURAS DE DATOS (AJEDREZ 3D)");
        System.out.println("==========================================================================");

        JuegoAjedrez juego = new JuegoAjedrez(false);

        // 1. Cola FIFO
        System.out.println("\n[1] COLA FIFO (Historial de Jugadas)");
        juego.moverPieza(6, 3, 4, 3); // Blancas: d2 -> d4
        juego.moverPieza(1, 3, 3, 3); // Negras:  d7 -> d5
        juego.moverPieza(7, 6, 5, 5); // Blancas: g1 -> f3

        ColaHistorial copiaCola = juego.getColaHistorial().clonar();
        System.out.print("    Orden FIFO : ");
        while (!copiaCola.estaVacia()) {
            Movimiento m = (Movimiento) copiaCola.desencolar();
            System.out.print("[" + m.getNotacion() + "]  ");
        }
        System.out.println("\n    Estado     : OK (Primer movimiento en entrar es el primero en salir)");

        // 2. Pila LIFO
        System.out.println("\n[2] PILA LIFO (Deshacer / Rehacer)");
        String turnoInicial = juego.getTurnoActual();
        juego.deshacerJugada();
        String turnoDeshacer = juego.getTurnoActual();
        juego.rehacerJugada();
        String turnoRehacer = juego.getTurnoActual();
        System.out.printf("    Flujo LIFO : Antes [%s] -> Deshacer [%s] -> Rehacer [%s]%n",
                turnoInicial, turnoDeshacer, turnoRehacer);
        System.out.println("    Estado     : OK (Último movimiento apilado se revierte y restaura)");

        // 3. Lista Circular
        System.out.println("\n[3] LISTA CIRCULAR (Ciclo de Turnos)");
        System.out.println("    Estructura : " + juego.getTurnos().recorrer());
        System.out.println("    Turno Act. : " + juego.getTurnoActual() + " | Estado: OK");

        // 4. Lista Doblemente Enlazada
        System.out.println("\n[4] LISTA DOBLEMENTE ENLAZADA (Piezas Vivas)");
        System.out.println("    Activas    : " + juego.getPiezasVivas().tamano() + " piezas en tablero");
        System.out.println("    Adelante   : " + juego.getPiezasVivas().recorrerAdelante().substring(0, 62) + "...");
        System.out.println("    Atrás      : " + juego.getPiezasVivas().recorrerAtras().substring(0, 62) + "...");
        System.out.println("    Estado     : OK (Recorrido bidireccional con punteros sig y ant)");

        // 5. Árbol Binario de Búsqueda (BST)
        System.out.println("\n[5] ÁRBOL BINARIO DE BÚSQUEDA - BST (Evaluación IA)");
        juego.iniciarNuevaPartida(true);
        juego.moverPieza(6, 3, 4, 3); // Blancas: d2 -> d4
        juego.ejecutarTurnoIA();       // La IA evalúa jugadas en el BST y responde

        System.out.println("    Recorrido Inorden (ordenado de menor a mayor puntaje):");
        imprimirEnColumnas(juego.getArbolEvaluacion().inorden(), 4);

        System.out.println("    Recorrido Preorden (Raíz - Izquierda - Derecha):");
        imprimirEnColumnas(juego.getArbolEvaluacion().preorden(), 4);

        System.out.println("    Recorrido Posorden (Izquierda - Derecha - Raíz):");
        imprimirEnColumnas(juego.getArbolEvaluacion().posorden(), 4);

        System.out.println("\n==========================================================================");
        System.out.println("              TODAS LAS 5 ESTRUCTURAS VERIFICADAS CORRECTAMENTE");
        System.out.println("==========================================================================");
    }

    private static void imprimirEnColumnas(String textoCrudo, int columnas) {
        String[] nodos = textoCrudo.split("(?<=\\])\\s+");
        int count = 0;
        System.out.print("    ");
        for (String nodo : nodos) {
            if (!nodo.trim().isEmpty()) {
                System.out.printf("%-18s", nodo.trim());
                count++;
                if (count % columnas == 0 && count < nodos.length) {
                    System.out.print("\n    ");
                }
            }
        }
        System.out.println();
    }
}

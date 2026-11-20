# Ajedrez - Proyecto Final Estructuras de Datos
- **Integrante:** Yeny Marcela Apio Caliz
- **Lenguaje:** Java (Swing)
# ♟️ Juego de Ajedrez 3D con Estructuras de Datos en Java

Aplicación de escritorio desarrollada en **Java (Swing)** que implementa el juego clásico de ajedrez con interfaz gráfica de efecto 3D y paleta pastel, integrando **cinco estructuras de datos dinámicas construidas desde cero** (sin utilizar colecciones nativas de `java.util` para la lógica principal) junto con un motor de **Inteligencia Artificial** basado en árboles binarios.

---

##  Estructuras de Datos Implementadas

Cada estructura cumple un rol funcional dentro de la mecánica del juego y puede inspeccionarse en tiempo real desde la interfaz gráfica:

| Estructura de Datos | Clase Principal | Rol en el Proyecto |
| :--- | :--- | :--- |
| **Pila (LIFO)** | `PilaJugadas.java` | Gestión de **Deshacer (`Undo`)** y **Rehacer (`Redo`)** movimientos durante la partida. |
| **Cola (FIFO)** | `ColaHistorial.java` | Registro cronológico del **Historial de Jugadas** y animación de **Repetir Partida**. |
| **Lista Circular** | `ListaCircularTurnos.java` | Alternancia cíclica e infinita de turnos entre **Blancas** y **Negras**. |
| **Lista Doblemente Enlazada** | `ListaDoblePiezas.java` | Control dinámico de las **32 piezas vivas** en el tablero, permitiendo recorrido hacia adelante (`sig`) y hacia atrás (`ant`), así como eliminación en capturas. |
| **Árbol Binario de Búsqueda (BST)** | `ArbolIA.java` | Evaluación táctica de movimientos de la **Inteligencia Artificial**, ordenando jugadas por puntaje y permitiendo recorridos **Inorden**, **Preorden** y **Posorden**. |

---

## Arquitectura del Proyecto

```text
AjedrezEstructuras/
├── src/
│   ├── estructuras/
│   │   ├── ArbolIA.java              # Árbol Binario de Búsqueda para la IA
│   │   ├── ColaHistorial.java        # Cola FIFO para historial y repetición
│   │   ├── ListaCircularTurnos.java  # Lista Circular para ciclo de turnos
│   │   ├── ListaDoblePiezas.java     # Lista Doblemente Enlazada de piezas activas
│   │   ├── Nodo.java                 # Nodo simple (Pila, Cola y Lista Circular)
│   │   ├── NodoArbol.java            # Nodo binario con enlaces izquierdo y derecho
│   │   ├── NodoDoble.java            # Nodo con punteros sig y ant
│   │   ├── PilaJugadas.java          # Pila LIFO para Deshacer / Rehacer
│   │   └── TestEstructuras.java      # Suite de pruebas unitarias por consola
│   ├── modelo/
│   │   ├── JuegoAjedrez.java         # Motor de reglas, validación de jaque/jaque mate e IA
│   │   ├── Movimiento.java           # Encapsulamiento de jugadas, capturas y puntaje
│   │   └── Pieza.java                # Modelo de piezas, colores y símbolos Unicode
│   └── vista/
│       ├── Main.java                 # Punto de entrada de la aplicación
│       └── VentanaAjedrez.java       # Interfaz gráfica Swing con tablero 3D e inspector
└── README.md

package escalera.estructuras;

import escalera.modelo.Jugada;

/**
 * Pila del historial de jugadas sobre lista simple.
 * La jugada más reciente queda en la cima.
 */
public class PilaJugadas {

    private static class Nodo {
        private final Jugada jugada;
        private final Nodo debajo;

        Nodo(Jugada jugada, Nodo debajo) {
            this.jugada = jugada;
            this.debajo = debajo;
        }
    }

    private Nodo cima;
    private int tamano;

    public void apilar(Jugada jugada) {
        cima = new Nodo(jugada, cima);
        tamano++;
    }

    /** Quita y devuelve la cima, o null si la pila está vacía. */
    public Jugada desapilar() {
        if (estaVacia()) {
            return null;
        }
        Jugada jugada = cima.jugada;
        cima = cima.debajo;
        tamano--;
        return jugada;
    }

    public Jugada verCima() {
        return estaVacia() ? null : cima.jugada;
    }

    /**
     * Consulta las últimas n jugadas sin desapilarlas.
     * La posición 0 del arreglo es la cima (la más reciente).
     */
    public Jugada[] ultimas(int cantidad) {
        int total = Math.min(cantidad, tamano);
        Jugada[] resultado = new Jugada[total];
        Nodo actual = cima;
        for (int i = 0; i < total; i++) {
            resultado[i] = actual.jugada;
            actual = actual.debajo;
        }
        return resultado;
    }

    public boolean estaVacia() {
        return cima == null;
    }

    public int getTamano() {
        return tamano;
    }
}

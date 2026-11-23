package escalera.estructuras;

import escalera.modelo.Jugador;

/**
 * Cierre opcional: segundo árbol binario de búsqueda ordenado por aciertos
 * (y por nombre si empatan). El inorden inverso da el ranking de mayor a menor.
 */
public class ArbolRanking {

    private static class Nodo {
        private final Jugador jugador;
        private Nodo izquierdo;
        private Nodo derecho;

        Nodo(Jugador jugador) {
            this.jugador = jugador;
        }
    }

    private Nodo raiz;

    public void insertar(Jugador jugador) {
        raiz = insertarDesde(raiz, jugador);
    }

    private Nodo insertarDesde(Nodo nodo, Jugador jugador) {
        if (nodo == null) {
            return new Nodo(jugador);
        }
        if (comparar(jugador, nodo.jugador) < 0) {
            nodo.izquierdo = insertarDesde(nodo.izquierdo, jugador);
        } else {
            nodo.derecho = insertarDesde(nodo.derecho, jugador);
        }
        return nodo;
    }

    /** Menos aciertos va a la izquierda; con empate, el nombre alfabéticamente mayor. */
    private int comparar(Jugador a, Jugador b) {
        if (a.getAciertos() != b.getAciertos()) {
            return Integer.compare(a.getAciertos(), b.getAciertos());
        }
        return b.getNombre().compareTo(a.getNombre());
    }

    public String describirRanking() {
        StringBuilder texto = new StringBuilder();
        recorrerInversoDesde(raiz, texto, new int[]{1});
        return texto.length() == 0 ? "(sin jugadores)" : texto.toString();
    }

    private void recorrerInversoDesde(Nodo nodo, StringBuilder texto, int[] puesto) {
        if (nodo == null) {
            return;
        }
        recorrerInversoDesde(nodo.derecho, texto, puesto);
        texto.append(String.format("%d. %s — %d aciertos, %d fallos, casilla %d%n",
                puesto[0]++, nodo.jugador.getNombre(), nodo.jugador.getAciertos(),
                nodo.jugador.getFallos(), nodo.jugador.getPosicion()));
        recorrerInversoDesde(nodo.izquierdo, texto, puesto);
    }
}

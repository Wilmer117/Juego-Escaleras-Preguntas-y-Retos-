package escalera.estructuras;

import escalera.modelo.Arista;

/**
 * Grafo dirigido de escaleras y serpientes con listas de adyacencia propias.
 * Este grafo es lo único que sabe que 3 lleva a 11; la lista doble solo sabe que después de 3 viene 4.
 */
public class GrafoSaltos {

    private static class NodoAdyacencia {
        private final Arista arista;
        private final NodoAdyacencia siguiente;

        NodoAdyacencia(Arista arista, NodoAdyacencia siguiente) {
            this.arista = arista;
            this.siguiente = siguiente;
        }
    }

    private final NodoAdyacencia[] adyacencia;
    private final Arista[] aristasEnOrden;
    private int cantidadAristas;

    /** @param maximoVertice número de la última casilla (los vértices van de 1 a maximoVertice). */
    public GrafoSaltos(int maximoVertice) {
        adyacencia = new NodoAdyacencia[maximoVertice + 1];
        aristasEnOrden = new Arista[maximoVertice];
    }

    /**
     * Agrega un salto dirigido. Una casilla solo puede tener un salto de salida.
     * @return false si el salto es inválido o la casilla ya tenía uno.
     */
    public boolean agregarSalto(int origen, int destino) {
        if (!esVerticeValido(origen) || !esVerticeValido(destino) || origen == destino || tieneSalto(origen)) {
            return false;
        }
        Arista arista = new Arista(origen, destino);
        adyacencia[origen] = new NodoAdyacencia(arista, adyacencia[origen]);
        aristasEnOrden[cantidadAristas++] = arista;
        return true;
    }

    public boolean tieneSalto(int casilla) {
        return esVerticeValido(casilla) && adyacencia[casilla] != null;
    }

    /** Destino del salto que sale de la casilla, o -1 si no hay. */
    public int destinoDe(int casilla) {
        return tieneSalto(casilla) ? adyacencia[casilla].arista.getDestino() : -1;
    }

    public Arista aristaDesde(int casilla) {
        return tieneSalto(casilla) ? adyacencia[casilla].arista : null;
    }

    /** Copia de las aristas en el orden en que se agregaron. */
    public Arista[] aristas() {
        Arista[] copia = new Arista[cantidadAristas];
        for (int i = 0; i < cantidadAristas; i++) {
            copia[i] = aristasEnOrden[i];
        }
        return copia;
    }

    public int getMaximoVertice() {
        return adyacencia.length - 1;
    }

    private boolean esVerticeValido(int casilla) {
        return casilla >= 1 && casilla < adyacencia.length;
    }

    public String describir() {
        StringBuilder texto = new StringBuilder();
        for (int i = 0; i < cantidadAristas; i++) {
            Arista arista = aristasEnOrden[i];
            texto.append(arista.comoSalto())
                 .append(arista.esEscalera() ? "  escalera, sube" : "  serpiente, baja")
                 .append('\n');
        }
        return texto.toString();
    }
}

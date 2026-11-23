package escalera.estructuras;

import escalera.modelo.Casilla;

/**
 * Nodo de la lista doble del tablero.
 * Tiene "anterior" y "siguiente": la casilla contigua, nunca el destino de un salto.
 */
public class NodoCasilla {

    private final Casilla casilla;
    private NodoCasilla anterior;
    private NodoCasilla siguiente;

    public NodoCasilla(Casilla casilla) {
        this.casilla = casilla;
    }

    public Casilla getCasilla() {
        return casilla;
    }

    public NodoCasilla getAnterior() {
        return anterior;
    }

    public void setAnterior(NodoCasilla anterior) {
        this.anterior = anterior;
    }

    public NodoCasilla getSiguiente() {
        return siguiente;
    }

    public void setSiguiente(NodoCasilla siguiente) {
        this.siguiente = siguiente;
    }
}

package escalera.estructuras;

import escalera.modelo.Casilla;

/**
 * Lista doblemente enlazada con las casillas del tablero (orden físico).
 * Permite recorrer de ida (1..n) y de regreso (n..1).
 */
public class ListaDobleTablero {

    private NodoCasilla cabeza;
    private NodoCasilla cola;
    private int tamano;

    /** Enlaza una casilla nueva al final de la lista. */
    public void agregarAlFinal(Casilla casilla) {
        NodoCasilla nuevo = new NodoCasilla(casilla);
        if (estaVacia()) {
            cabeza = nuevo;
        } else {
            cola.setSiguiente(nuevo);
            nuevo.setAnterior(cola);
        }
        cola = nuevo;
        tamano++;
    }

    /**
     * Busca un nodo por número. Arranca por el extremo más cercano,
     * aprovechando que la lista es doble.
     */
    public NodoCasilla buscar(int numero) {
        if (numero < 1 || numero > tamano) {
            return null;
        }
        boolean desdeElInicio = numero <= tamano / 2;
        NodoCasilla actual = desdeElInicio ? cabeza : cola;
        while (actual != null && actual.getCasilla().getNumero() != numero) {
            actual = desdeElInicio ? actual.getSiguiente() : actual.getAnterior();
        }
        return actual;
    }

    /** Número de la casilla contigua siguiente, o -1 si es la última. */
    public int numeroSiguiente(int numero) {
        NodoCasilla nodo = buscar(numero);
        if (nodo == null || nodo.getSiguiente() == null) {
            return -1;
        }
        return nodo.getSiguiente().getCasilla().getNumero();
    }

    /** Número de la casilla contigua anterior, o -1 si es la primera. */
    public int numeroAnterior(int numero) {
        NodoCasilla nodo = buscar(numero);
        if (nodo == null || nodo.getAnterior() == null) {
            return -1;
        }
        return nodo.getAnterior().getCasilla().getNumero();
    }

    public String recorrerIda() {
        StringBuilder texto = new StringBuilder();
        for (NodoCasilla actual = cabeza; actual != null; actual = actual.getSiguiente()) {
            texto.append(actual.getCasilla());
            if (actual.getSiguiente() != null) {
                texto.append(' ');
            }
        }
        return texto.toString();
    }

    public String recorrerRegreso() {
        StringBuilder texto = new StringBuilder();
        for (NodoCasilla actual = cola; actual != null; actual = actual.getAnterior()) {
            texto.append(actual.getCasilla());
            if (actual.getAnterior() != null) {
                texto.append(' ');
            }
        }
        return texto.toString();
    }

    public NodoCasilla getCabeza() {
        return cabeza;
    }

    public int getTamano() {
        return tamano;
    }

    public boolean estaVacia() {
        return tamano == 0;
    }
}

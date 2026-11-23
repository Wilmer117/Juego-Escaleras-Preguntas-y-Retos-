package escalera.estructuras;

/**
 * Cola simple de turnos sobre nodos propios.
 * Quien juega sale del frente y, si no ganó, vuelve a entrar al final.
 */
public class ColaTurnos {

    /** Nodo de lista simple con el nombre del jugador. */
    private static class Nodo {
        private final String nombre;
        private Nodo siguiente;

        Nodo(String nombre) {
            this.nombre = nombre;
        }
    }

    private Nodo frente;
    private Nodo fin;
    private int tamano;

    public void encolar(String nombre) {
        Nodo nuevo = new Nodo(nombre);
        if (estaVacia()) {
            frente = nuevo;
        } else {
            fin.siguiente = nuevo;
        }
        fin = nuevo;
        tamano++;
    }

    /**
     * Saca al jugador del frente.
     * @return el nombre, o null si la cola está vacía (quien llama debe avisar).
     */
    public String desencolar() {
        if (estaVacia()) {
            return null;
        }
        String nombre = frente.nombre;
        frente = frente.siguiente;
        if (frente == null) {
            fin = null;
        }
        tamano--;
        return nombre;
    }

    /** Mira el frente sin sacarlo. Devuelve null si está vacía. */
    public String verFrente() {
        return estaVacia() ? null : frente.nombre;
    }

    public boolean estaVacia() {
        return frente == null;
    }

    public int getTamano() {
        return tamano;
    }

    /** Orden actual de la cola, del frente al final. */
    public String describir() {
        if (estaVacia()) {
            return "(cola vacía)";
        }
        StringBuilder texto = new StringBuilder();
        for (Nodo actual = frente; actual != null; actual = actual.siguiente) {
            texto.append(actual.nombre);
            if (actual.siguiente != null) {
                texto.append(" <- ");
            }
        }
        return texto.toString();
    }
}

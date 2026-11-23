package escalera.estructuras;

import escalera.modelo.Jugador;

/**
 * Tabla hash propia: arreglo de cubetas con una lista simple en cada una.
 * Cubeta = suma de los códigos de los caracteres del nombre, módulo número de cubetas.
 */
public class TablaJugadores {

    private static class NodoCubeta {
        private final Jugador jugador;
        private NodoCubeta siguiente;

        NodoCubeta(Jugador jugador) {
            this.jugador = jugador;
        }
    }

    private final NodoCubeta[] cubetas;
    private int cantidad;

    public TablaJugadores(int numeroCubetas) {
        cubetas = new NodoCubeta[numeroCubetas];
    }

    /** Suma de códigos del nombre (Ana = 65 + 110 + 97 = 272). */
    public static int sumaCodigos(String nombre) {
        int suma = 0;
        for (int i = 0; i < nombre.length(); i++) {
            suma += nombre.charAt(i);
        }
        return suma;
    }

    public int indiceCubeta(String nombre) {
        return sumaCodigos(nombre) % cubetas.length;
    }

    /**
     * Registra un jugador al final de su cubeta.
     * @return false si ya existía un jugador con ese nombre.
     */
    public boolean registrar(Jugador jugador) {
        if (buscar(jugador.getNombre()) != null) {
            return false;
        }
        int indice = indiceCubeta(jugador.getNombre());
        NodoCubeta nuevo = new NodoCubeta(jugador);
        if (cubetas[indice] == null) {
            cubetas[indice] = nuevo;
        } else {
            NodoCubeta actual = cubetas[indice];
            while (actual.siguiente != null) {
                actual = actual.siguiente;
            }
            actual.siguiente = nuevo;
        }
        cantidad++;
        return true;
    }

    /** Busca recorriendo solo la lista de la cubeta. Devuelve null si no existe. */
    public Jugador buscar(String nombre) {
        if (nombre == null) {
            return null;
        }
        for (NodoCubeta actual = cubetas[indiceCubeta(nombre)]; actual != null; actual = actual.siguiente) {
            if (actual.jugador.getNombre().equals(nombre)) {
                return actual.jugador;
            }
        }
        return null;
    }

    /** Explica qué nodos se visitaron al buscar (para mostrar la colisión). */
    public String describirBusqueda(String nombre) {
        int indice = indiceCubeta(nombre);
        StringBuilder texto = new StringBuilder();
        texto.append(String.format("Buscar %s: suma %d, %d mod %d = cubeta %d. Recorrido: ",
                nombre, sumaCodigos(nombre), sumaCodigos(nombre), cubetas.length, indice));
        boolean encontrado = false;
        if (cubetas[indice] == null) {
            texto.append("(cubeta vacía)");
        }
        for (NodoCubeta actual = cubetas[indice]; actual != null; actual = actual.siguiente) {
            texto.append(actual.jugador.getNombre());
            if (actual.jugador.getNombre().equals(nombre)) {
                encontrado = true;
                break;
            }
            texto.append(" -> ");
        }
        texto.append(encontrado ? " (encontrado)" : " (no registrado)");
        return texto.toString();
    }

    /** Todos los jugadores, cubeta por cubeta. */
    public Jugador[] todos() {
        Jugador[] resultado = new Jugador[cantidad];
        int i = 0;
        for (NodoCubeta cabeza : cubetas) {
            for (NodoCubeta actual = cabeza; actual != null; actual = actual.siguiente) {
                resultado[i++] = actual.jugador;
            }
        }
        return resultado;
    }

    public String describir() {
        StringBuilder texto = new StringBuilder();
        for (int i = 0; i < cubetas.length; i++) {
            texto.append("cubeta ").append(i).append(": ");
            if (cubetas[i] == null) {
                texto.append("(vacía)");
            }
            for (NodoCubeta actual = cubetas[i]; actual != null; actual = actual.siguiente) {
                texto.append(actual.jugador);
                if (actual.siguiente != null) {
                    texto.append(" -> ");
                }
            }
            texto.append('\n');
        }
        return texto.toString();
    }

    public int getCantidad() {
        return cantidad;
    }

    public int getNumeroCubetas() {
        return cubetas.length;
    }
}

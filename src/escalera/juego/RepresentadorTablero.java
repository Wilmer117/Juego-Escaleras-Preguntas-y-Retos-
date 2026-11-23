package escalera.juego;

import escalera.estructuras.ListaDobleTablero;
import escalera.estructuras.NodoCasilla;
import escalera.modelo.Jugador;

/**
 * Dibuja el tablero en texto: 10 columnas por 5 filas, en zigzag como un tablero real
 * (la fila de abajo va de 1 a 10, la siguiente de 20 a 11, ...). Se recorre la lista doble.
 */
public final class RepresentadorTablero {

    private RepresentadorTablero() {
    }

    public static String dibujar(ListaDobleTablero tablero, Jugador[] jugadores) {
        int columnas = ConfiguracionTablero.COLUMNAS_TABLERO;
        int filas = (tablero.getTamano() + columnas - 1) / columnas;
        String[][] celdas = new String[filas][columnas];

        for (NodoCasilla nodo = tablero.getCabeza(); nodo != null; nodo = nodo.getSiguiente()) {
            int numero = nodo.getCasilla().getNumero();
            int filaDesdeAbajo = (numero - 1) / columnas;
            int desplazamiento = (numero - 1) % columnas;
            int columna = filaDesdeAbajo % 2 == 0 ? desplazamiento : columnas - 1 - desplazamiento;
            celdas[filas - 1 - filaDesdeAbajo][columna] = String.format("[%2d%c%-3s]",
                    numero, nodo.getCasilla().getTipo().getSimbolo(), inicialesEn(numero, jugadores));
        }

        StringBuilder texto = new StringBuilder();
        for (String[] fila : celdas) {
            for (String celda : fila) {
                texto.append(celda == null ? "         " : celda);
            }
            texto.append('\n');
        }
        texto.append("Fuera del tablero: ").append(inicialesEn(0, jugadores).isEmpty() ? "nadie" : inicialesEn(0, jugadores));
        texto.append("   (^ escalera, v serpiente, ? reto, * meta)\n");
        return texto.toString();
    }

    private static String inicialesEn(int casilla, Jugador[] jugadores) {
        StringBuilder iniciales = new StringBuilder();
        for (Jugador jugador : jugadores) {
            if (jugador != null && jugador.getPosicion() == casilla) {
                iniciales.append(jugador.getInicial());
            }
        }
        return iniciales.toString();
    }
}

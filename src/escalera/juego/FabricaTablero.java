package escalera.juego;

import escalera.estructuras.GrafoSaltos;
import escalera.estructuras.ListaDobleTablero;
import escalera.modelo.Casilla;
import escalera.modelo.TipoCasilla;

/** Construye y extiende la lista doble del tablero. El tipo de casilla se consulta al grafo. */
public final class FabricaTablero {

    private FabricaTablero() {
    }

    /** Grafo con los cinco saltos reales del enunciado. */
    public static GrafoSaltos crearGrafoDeSaltos(int maximoVertice) {
        GrafoSaltos grafo = new GrafoSaltos(maximoVertice);
        for (int[] salto : ConfiguracionTablero.SALTOS) {
            grafo.agregarSalto(salto[0], salto[1]);
        }
        return grafo;
    }

    public static ListaDobleTablero crearTablero(int ultimaCasilla, GrafoSaltos grafo) {
        ListaDobleTablero tablero = new ListaDobleTablero();
        extenderHasta(tablero, ultimaCasilla, grafo);
        return tablero;
    }

    /** Alarga la lista desde su última casilla hasta la indicada, conservando lo que ya había. */
    public static void extenderHasta(ListaDobleTablero tablero, int ultimaCasilla, GrafoSaltos grafo) {
        for (int numero = tablero.getTamano() + 1; numero <= ultimaCasilla; numero++) {
            tablero.agregarAlFinal(new Casilla(numero, tipoDe(numero, grafo)));
        }
    }

    private static TipoCasilla tipoDe(int numero, GrafoSaltos grafo) {
        if (ConfiguracionTablero.esReto(numero)) {
            return TipoCasilla.RETO;
        }
        if (grafo.tieneSalto(numero)) {
            return grafo.destinoDe(numero) > numero ? TipoCasilla.ESCALERA : TipoCasilla.SERPIENTE;
        }
        return numero == ConfiguracionTablero.META ? TipoCasilla.META : TipoCasilla.NORMAL;
    }
}

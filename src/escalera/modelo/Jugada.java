package escalera.modelo;

/**
 * Registro de una jugada para la pila del historial:
 * quién tiró, qué sacó y en qué casilla terminó.
 */
public class Jugada {

    private final String jugador;
    private final int dado;
    private final int origen;
    private final int casillaAlcanzada;
    private final String accion;

    public Jugada(String jugador, int dado, int origen, int casillaAlcanzada, String accion) {
        this.jugador = jugador;
        this.dado = dado;
        this.origen = origen;
        this.casillaAlcanzada = casillaAlcanzada;
        this.accion = accion;
    }

    public String getJugador() {
        return jugador;
    }

    public int getDado() {
        return dado;
    }

    public int getOrigen() {
        return origen;
    }

    public int getCasillaAlcanzada() {
        return casillaAlcanzada;
    }

    public String getAccion() {
        return accion;
    }

    @Override
    public String toString() {
        return String.format("%s: dado %d, %d -> %d (%s)", jugador, dado, origen, casillaAlcanzada, accion);
    }
}

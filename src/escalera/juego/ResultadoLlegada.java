package escalera.juego;

import escalera.decision.Accion;
import escalera.modelo.Pregunta;

/** Lo que pasó en una tirada: datos del movimiento, acción del árbol y su recorrido. */
public class ResultadoLlegada {

    private final String jugador;
    private final int dado;
    private final int origen;
    private final int casillaDeLlegada;
    private final int posicionFinal;
    private final Accion accion;
    private final String trazaDecision;
    private final Pregunta pregunta;
    private final String respuesta;

    public ResultadoLlegada(String jugador, int dado, int origen, int casillaDeLlegada, int posicionFinal,
                            Accion accion, String trazaDecision, Pregunta pregunta, String respuesta) {
        this.jugador = jugador;
        this.dado = dado;
        this.origen = origen;
        this.casillaDeLlegada = casillaDeLlegada;
        this.posicionFinal = posicionFinal;
        this.accion = accion;
        this.trazaDecision = trazaDecision;
        this.pregunta = pregunta;
        this.respuesta = respuesta;
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

    /** Casilla a la que lleva el dado (antes de seguir un salto). */
    public int getCasillaDeLlegada() {
        return casillaDeLlegada;
    }

    public int getPosicionFinal() {
        return posicionFinal;
    }

    public Accion getAccion() {
        return accion;
    }

    public String getTrazaDecision() {
        return trazaDecision;
    }

    public Pregunta getPregunta() {
        return pregunta;
    }

    public String getRespuesta() {
        return respuesta;
    }

    /** Frase corta en el estilo de la tabla de la corrida de verificación. */
    public String resumen() {
        switch (accion) {
            case NO_SE_MUEVE:
                return String.format("%s está en %d y saca %d: pasaría de %d, no se mueve",
                        jugador, origen, dado, ConfiguracionTablero.META);
            case SIGUE_ARISTA:
                return String.format("%s cae en %d y %s a %d", jugador, casillaDeLlegada,
                        posicionFinal > casillaDeLlegada ? "sube" : "baja", posicionFinal);
            case ACIERTA_Y_REPITE:
            case FALLA_Y_PIERDE_TURNO:
                if (pregunta == null) {
                    return String.format("%s pasa de %d a %d. Reto sin pregunta disponible: cuenta como fallo",
                            jugador, origen, casillaDeLlegada);
                }
                return String.format("%s pasa de %d a %d. Reto de dificultad %d, pregunta %d. Responde \"%s\": %s",
                        jugador, origen, casillaDeLlegada, pregunta.getDificultad(), pregunta.getClave(),
                        respuesta, accion == Accion.ACIERTA_Y_REPITE ? "acierta, juega otra vez"
                                : "falla, pierde el turno siguiente");
            default:
                return String.format("%s pasa de %d a %d y se queda", jugador, origen, casillaDeLlegada);
        }
    }
}

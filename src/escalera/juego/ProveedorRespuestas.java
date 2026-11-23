package escalera.juego;

import escalera.modelo.Jugador;
import escalera.modelo.Pregunta;

/** Quien contesta un reto: respuestas fijas, un diálogo en la GUI o el jugador IA. */
@FunctionalInterface
public interface ProveedorRespuestas {
    String responder(Jugador jugador, Pregunta pregunta);
}

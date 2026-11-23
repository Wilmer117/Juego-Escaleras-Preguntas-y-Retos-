package escalera.juego;

import escalera.modelo.Jugador;
import escalera.modelo.Pregunta;

/** Respuestas guardadas de antemano, entregadas en orden. Si se acaban, responde vacío. */
public class RespuestasFijas implements ProveedorRespuestas {

    private final String[] respuestas;
    private int siguiente;

    public RespuestasFijas(String... respuestas) {
        this.respuestas = respuestas.clone();
    }

    @Override
    public String responder(Jugador jugador, Pregunta pregunta) {
        return siguiente < respuestas.length ? respuestas[siguiente++] : "";
    }
}

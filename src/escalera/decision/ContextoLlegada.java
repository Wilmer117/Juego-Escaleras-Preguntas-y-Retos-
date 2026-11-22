package escalera.decision;

import escalera.estructuras.ArbolPreguntas;
import escalera.estructuras.GrafoSaltos;
import escalera.juego.ConfiguracionTablero;
import escalera.juego.ProveedorRespuestas;
import escalera.modelo.Jugador;
import escalera.modelo.Pregunta;

/**
 * Datos que consulta el árbol de decisión en una llegada.
 * La pregunta del reto se formula solo si el árbol llega a ese nodo (evaluación perezosa).
 */
public class ContextoLlegada {

    private final Jugador jugador;
    private final int valorDado;
    private final int meta;
    private final GrafoSaltos grafo;
    private final ArbolPreguntas preguntas;
    private final ProveedorRespuestas proveedorRespuestas;

    private Pregunta preguntaFormulada;
    private String respuestaDada;
    private Boolean respuestaFueCorrecta;

    public ContextoLlegada(Jugador jugador, int valorDado, int meta, GrafoSaltos grafo,
                           ArbolPreguntas preguntas, ProveedorRespuestas proveedorRespuestas) {
        this.jugador = jugador;
        this.valorDado = valorDado;
        this.meta = meta;
        this.grafo = grafo;
        this.preguntas = preguntas;
        this.proveedorRespuestas = proveedorRespuestas;
    }

    public int casillaDeLlegada() {
        return jugador.getPosicion() + valorDado;
    }

    public boolean pasaDeLaMeta() {
        return casillaDeLlegada() > meta;
    }

    public boolean llegadaEsReto() {
        return ConfiguracionTablero.esReto(casillaDeLlegada());
    }

    public boolean haySaltoEnLlegada() {
        return grafo.tieneSalto(casillaDeLlegada());
    }

    /** Busca en el árbol la pregunta de la dificultad de la casilla y valida la respuesta. */
    public boolean respuestaEsCorrecta() {
        if (respuestaFueCorrecta == null) {
            int dificultad = ConfiguracionTablero.dificultadDe(casillaDeLlegada());
            preguntaFormulada = preguntas.preguntaDeDificultad(dificultad);
            if (preguntaFormulada == null) {
                respuestaDada = "(no hay preguntas de dificultad " + dificultad + ")";
                respuestaFueCorrecta = false;
            } else {
                respuestaDada = proveedorRespuestas.responder(jugador, preguntaFormulada);
                respuestaFueCorrecta = preguntaFormulada.esCorrecta(respuestaDada);
            }
        }
        return respuestaFueCorrecta;
    }

    public Jugador getJugador() {
        return jugador;
    }

    public int getValorDado() {
        return valorDado;
    }

    public Pregunta getPreguntaFormulada() {
        return preguntaFormulada;
    }

    public String getRespuestaDada() {
        return respuestaDada;
    }
}

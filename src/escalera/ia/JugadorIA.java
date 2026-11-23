package escalera.ia;

import escalera.juego.ProveedorRespuestas;
import escalera.modelo.Jugador;
import escalera.modelo.Pregunta;

import java.util.Random;

/**
 * Jugador automático. Responde los retos razonando con la base de conocimiento
 * (no lee la respuesta guardada en la pregunta). Para que no sea perfecto,
 * se "distrae" con una probabilidad que crece con la dificultad.
 */
public class JugadorIA implements ProveedorRespuestas {

    /** Probabilidad de equivocarse según la dificultad 1, 2 y 3. */
    private static final double[] PROBABILIDAD_DE_DISTRACCION = {0.0, 0.10, 0.25, 0.40};
    private static final String RESPUESTA_POR_DEFECTO = "sí";

    private final Random azar;

    public JugadorIA() {
        this(new Random());
    }

    public JugadorIA(long semilla) {
        this(new Random(semilla));
    }

    private JugadorIA(Random azar) {
        this.azar = azar;
    }

    @Override
    public String responder(Jugador jugador, Pregunta pregunta) {
        String inferida = BaseConocimiento.inferirRespuesta(pregunta.getEnunciado());
        String respuesta = inferida == null ? RESPUESTA_POR_DEFECTO : inferida;
        int dificultad = Math.min(pregunta.getDificultad(), PROBABILIDAD_DE_DISTRACCION.length - 1);
        if (azar.nextDouble() < PROBABILIDAD_DE_DISTRACCION[dificultad]) {
            return invertir(respuesta);
        }
        return respuesta;
    }

    private String invertir(String respuesta) {
        if (respuesta.equalsIgnoreCase("sí") || respuesta.equalsIgnoreCase("si")) {
            return "no";
        }
        if (respuesta.equalsIgnoreCase("no")) {
            return "sí";
        }
        return "no sé";
    }
}

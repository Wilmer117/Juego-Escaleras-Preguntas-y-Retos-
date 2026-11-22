package escalera.modelo;

import escalera.juego.TextoUtil;

/**
 * Pregunta de un reto. La clave es dificultad * 100 + identificador,
 * por eso el árbol de búsqueda las agrupa por dificultad.
 */
public class Pregunta {

    private final int clave;
    private final String enunciado;
    private final String respuestaEsperada;

    public Pregunta(int clave, String enunciado, String respuestaEsperada) {
        this.clave = clave;
        this.enunciado = enunciado;
        this.respuestaEsperada = respuestaEsperada;
    }

    public int getClave() {
        return clave;
    }

    public int getDificultad() {
        return clave / 100;
    }

    public String getEnunciado() {
        return enunciado;
    }

    public String getRespuestaEsperada() {
        return respuestaEsperada;
    }

    /** Compara sin importar mayúsculas, espacios ni tildes ("si" vale como "sí"). */
    public boolean esCorrecta(String respuesta) {
        if (respuesta == null) {
            return false;
        }
        return TextoUtil.normalizar(respuesta).equals(TextoUtil.normalizar(respuestaEsperada));
    }

    @Override
    public String toString() {
        return clave + " " + enunciado + " (" + respuestaEsperada + ")";
    }
}

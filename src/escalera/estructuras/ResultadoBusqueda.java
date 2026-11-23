package escalera.estructuras;

import escalera.modelo.Pregunta;

/** Lo que informa el árbol al buscar una clave: camino, nivel y grado del nodo. */
public class ResultadoBusqueda {

    private final Pregunta pregunta;
    private final String camino;
    private final int nivel;
    private final int grado;

    public ResultadoBusqueda(Pregunta pregunta, String camino, int nivel, int grado) {
        this.pregunta = pregunta;
        this.camino = camino;
        this.nivel = nivel;
        this.grado = grado;
    }

    public boolean fueEncontrada() {
        return pregunta != null;
    }

    public Pregunta getPregunta() {
        return pregunta;
    }

    public String getCamino() {
        return camino;
    }

    public int getNivel() {
        return nivel;
    }

    public int getGrado() {
        return grado;
    }

    @Override
    public String toString() {
        if (!fueEncontrada()) {
            return "No encontrada. Camino recorrido: " + camino;
        }
        return String.format("camino %s, nivel %d, grado %d", camino, nivel, grado);
    }
}

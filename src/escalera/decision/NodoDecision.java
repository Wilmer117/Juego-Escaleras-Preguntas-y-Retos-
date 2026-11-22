package escalera.decision;

/** Nodo del árbol de decisión: puede ser una pregunta (interno) o una acción (hoja). */
public abstract class NodoDecision {

    /**
     * Baja desde este nodo hasta una hoja.
     * @param traza se van anotando las preguntas y respuestas recorridas
     */
    public abstract Accion decidir(ContextoLlegada contexto, StringBuilder traza);

    /** Dibuja el subárbol con el mismo estilo del enunciado. */
    public abstract void dibujar(String prefijo, StringBuilder texto);
}

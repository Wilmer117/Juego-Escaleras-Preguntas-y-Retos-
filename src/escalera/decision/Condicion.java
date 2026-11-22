package escalera.decision;

/** Pregunta de sí/no que hace un nodo interno del árbol de decisión. */
@FunctionalInterface
public interface Condicion {
    boolean evaluar(ContextoLlegada contexto);
}

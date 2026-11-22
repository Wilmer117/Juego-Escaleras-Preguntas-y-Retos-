package escalera.decision;

/**
 * Árbol binario de decisión que resuelve cada llegada.
 * Se arma explícitamente con nodos (no con una cadena de if) para que la regla se vea:
 *
 * ¿La posición más el dado pasa de 50?
 * ├─ sí → la ficha no se mueve
 * └─ no → ¿la casilla de llegada es un reto?
 *          ├─ sí → ¿la respuesta es correcta?
 *          │        ├─ sí → acierta y juega otra vez
 *          │        └─ no → falla y pierde el turno siguiente
 *          └─ no → ¿hay escalera o serpiente?
 *                   ├─ sí → se sigue esa arista
 *                   └─ no → se queda en la casilla
 */
public class ArbolDecision {

    private final NodoDecision raiz;

    public ArbolDecision(int meta) {
        NodoDecision reglaRespuesta = new NodoPregunta("¿la respuesta es correcta?",
                ContextoLlegada::respuestaEsCorrecta,
                new NodoAccion(Accion.ACIERTA_Y_REPITE),
                new NodoAccion(Accion.FALLA_Y_PIERDE_TURNO));

        NodoDecision reglaSalto = new NodoPregunta("¿hay escalera o serpiente?",
                ContextoLlegada::haySaltoEnLlegada,
                new NodoAccion(Accion.SIGUE_ARISTA),
                new NodoAccion(Accion.SE_QUEDA));

        NodoDecision reglaReto = new NodoPregunta("¿la casilla de llegada es un reto?",
                ContextoLlegada::llegadaEsReto,
                reglaRespuesta,
                reglaSalto);

        raiz = new NodoPregunta("¿la posición más el dado pasa de " + meta + "?",
                ContextoLlegada::pasaDeLaMeta,
                new NodoAccion(Accion.NO_SE_MUEVE),
                reglaReto);
    }

    public Accion decidir(ContextoLlegada contexto, StringBuilder traza) {
        return raiz.decidir(contexto, traza);
    }

    public String dibujar() {
        StringBuilder texto = new StringBuilder();
        raiz.dibujar("", texto);
        return texto.toString();
    }
}

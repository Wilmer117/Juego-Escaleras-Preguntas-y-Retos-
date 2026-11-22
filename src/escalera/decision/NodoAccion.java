package escalera.decision;

/** Hoja del árbol: dice qué acción se aplica. */
public class NodoAccion extends NodoDecision {

    private final Accion accion;

    public NodoAccion(Accion accion) {
        this.accion = accion;
    }

    @Override
    public Accion decidir(ContextoLlegada contexto, StringBuilder traza) {
        traza.append(accion.getDescripcion());
        return accion;
    }

    @Override
    public void dibujar(String prefijo, StringBuilder salida) {
        salida.append(accion.getDescripcion()).append('\n');
    }
}

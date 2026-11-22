package escalera.decision;

/** Nodo interno: hace una pregunta y baja por la rama "sí" o por la rama "no". */
public class NodoPregunta extends NodoDecision {

    private final String texto;
    private final Condicion condicion;
    private final NodoDecision ramaSi;
    private final NodoDecision ramaNo;

    public NodoPregunta(String texto, Condicion condicion, NodoDecision ramaSi, NodoDecision ramaNo) {
        this.texto = texto;
        this.condicion = condicion;
        this.ramaSi = ramaSi;
        this.ramaNo = ramaNo;
    }

    @Override
    public Accion decidir(ContextoLlegada contexto, StringBuilder traza) {
        boolean respuesta = condicion.evaluar(contexto);
        traza.append(texto).append(respuesta ? " sí → " : " no → ");
        return respuesta ? ramaSi.decidir(contexto, traza) : ramaNo.decidir(contexto, traza);
    }

    @Override
    public void dibujar(String prefijo, StringBuilder salida) {
        salida.append(texto).append('\n');
        salida.append(prefijo).append("├─ sí → ");
        ramaSi.dibujar(prefijo + "│       ", salida);
        salida.append(prefijo).append("└─ no → ");
        ramaNo.dibujar(prefijo + "        ", salida);
    }
}

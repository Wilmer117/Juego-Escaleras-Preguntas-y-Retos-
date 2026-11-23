package escalera.estructuras;

import escalera.modelo.Arista;

/** Veredicto de un validador de ciclos sobre una arista. */
public class ResultadoValidacion {

    private final Arista arista;
    private final boolean aceptada;
    private final String motivo;

    public ResultadoValidacion(Arista arista, boolean aceptada, String motivo) {
        this.arista = arista;
        this.aceptada = aceptada;
        this.motivo = motivo;
    }

    public Arista getArista() {
        return arista;
    }

    public boolean fueAceptada() {
        return aceptada;
    }

    public String getMotivo() {
        return motivo;
    }

    @Override
    public String toString() {
        return String.format("%-6s %s  %s", arista.sinDireccion(), aceptada ? "ACEPTADA " : "RECHAZADA", motivo);
    }
}

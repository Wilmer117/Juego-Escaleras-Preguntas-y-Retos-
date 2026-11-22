package escalera.modelo;

/**
 * Arista del grafo de saltos. En la partida se sigue de origen a destino;
 * para validar ciclos se mira sin dirección.
 */
public class Arista {

    private final int origen;
    private final int destino;
    private final int peso;

    public Arista(int origen, int destino) {
        this(origen, destino, 1);
    }

    public Arista(int origen, int destino, int peso) {
        this.origen = origen;
        this.destino = destino;
        this.peso = peso;
    }

    public int getOrigen() {
        return origen;
    }

    public int getDestino() {
        return destino;
    }

    public int getPeso() {
        return peso;
    }

    public boolean esEscalera() {
        return destino > origen;
    }

    /** Representación dirigida, como se usa en la partida. */
    public String comoSalto() {
        return origen + " -> " + destino;
    }

    /** Representación sin dirección, como se usa al validar ciclos. */
    public String sinDireccion() {
        return origen + "—" + destino;
    }

    @Override
    public String toString() {
        return comoSalto();
    }
}

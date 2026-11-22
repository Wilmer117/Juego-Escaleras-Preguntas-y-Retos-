package escalera.modelo;

/**
 * Dato que guarda cada nodo de la lista doble: solo el número y el tipo.
 * La casilla NO conoce el destino de su escalera o serpiente;
 * ese destino vive en el grafo de saltos.
 */
public class Casilla {

    private final int numero;
    private final TipoCasilla tipo;

    public Casilla(int numero, TipoCasilla tipo) {
        this.numero = numero;
        this.tipo = tipo;
    }

    public int getNumero() {
        return numero;
    }

    public TipoCasilla getTipo() {
        return tipo;
    }

    @Override
    public String toString() {
        return numero + (tipo == TipoCasilla.NORMAL ? "" : String.valueOf(tipo.getSimbolo()));
    }
}

package escalera.modelo;

/**
 * Tipos posibles de una casilla del tablero.
 * El símbolo se usa para dibujar el tablero en modo texto.
 */
public enum TipoCasilla {
    NORMAL(' '),
    ESCALERA('^'),
    SERPIENTE('v'),
    RETO('?'),
    META('*');

    private final char simbolo;

    TipoCasilla(char simbolo) {
        this.simbolo = simbolo;
    }

    public char getSimbolo() {
        return simbolo;
    }
}

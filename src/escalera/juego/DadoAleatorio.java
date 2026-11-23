package escalera.juego;

import java.util.Random;

/** Dado de seis caras para el modo de juego con azar. */
public class DadoAleatorio implements Dado {

    private static final int CARAS = 6;
    private final Random generador;

    public DadoAleatorio() {
        this(new Random());
    }

    /** Con semilla, para poder repetir una partida aleatoria. */
    public DadoAleatorio(long semilla) {
        this(new Random(semilla));
    }

    private DadoAleatorio(Random generador) {
        this.generador = generador;
    }

    @Override
    public int tirar() {
        return generador.nextInt(CARAS) + 1;
    }

    @Override
    public boolean quedanTiradas() {
        return true;
    }
}

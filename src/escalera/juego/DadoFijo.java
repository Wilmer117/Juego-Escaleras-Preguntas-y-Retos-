package escalera.juego;

/** Dado no aleatorio: entrega los valores en el orden dado (3, 4, 8, 1, 6 en la corrida). */
public class DadoFijo implements Dado {

    private final int[] valores;
    private int siguiente;

    public DadoFijo(int... valores) {
        this.valores = valores.clone();
    }

    @Override
    public int tirar() {
        if (!quedanTiradas()) {
            throw new IllegalStateException("El dado fijo ya no tiene valores");
        }
        return valores[siguiente++];
    }

    @Override
    public boolean quedanTiradas() {
        return siguiente < valores.length;
    }
}

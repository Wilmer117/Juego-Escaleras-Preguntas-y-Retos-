package escalera.modelo;

/**
 * Estado de un jugador. Vive dentro de la tabla hash de jugadores.
 * Posición 0 significa "todavía fuera del tablero".
 */
public class Jugador {

    private final String nombre;
    private final boolean controladoPorIA;
    private int posicion;
    private int aciertos;
    private int fallos;
    private boolean pierdeTurno;

    public Jugador(String nombre) {
        this(nombre, false);
    }

    public Jugador(String nombre, boolean controladoPorIA) {
        this.nombre = nombre;
        this.controladoPorIA = controladoPorIA;
        this.posicion = 0;
    }

    public String getNombre() {
        return nombre;
    }

    public char getInicial() {
        return Character.toUpperCase(nombre.charAt(0));
    }

    public boolean esControladoPorIA() {
        return controladoPorIA;
    }

    public int getPosicion() {
        return posicion;
    }

    public void moverA(int nuevaPosicion) {
        this.posicion = nuevaPosicion;
    }

    public int getAciertos() {
        return aciertos;
    }

    public int getFallos() {
        return fallos;
    }

    public void registrarAcierto() {
        aciertos++;
    }

    /** Un fallo en un reto se registra como fallo y como castigo de turno. */
    public void registrarFallo() {
        fallos++;
        pierdeTurno = true;
    }

    public boolean tieneCastigo() {
        return pierdeTurno;
    }

    /** Se llama cuando el jugador sale del frente de la cola y paga su castigo. */
    public void consumirCastigo() {
        pierdeTurno = false;
    }

    @Override
    public String toString() {
        return String.format("%s (pos %d, aciertos %d, fallos %d%s)",
                nombre, posicion, aciertos, fallos, pierdeTurno ? ", pierde turno" : "");
    }
}

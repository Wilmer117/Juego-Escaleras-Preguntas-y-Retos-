package escalera.juego;

/** Fuente de valores de dado. Puede ser fija (corrida de verificación) o aleatoria. */
public interface Dado {

    int tirar();

    /** false cuando un dado fijo ya entregó todos sus valores. */
    boolean quedanTiradas();
}

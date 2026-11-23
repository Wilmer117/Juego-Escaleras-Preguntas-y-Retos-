package escalera.juego;

/** Destino de los mensajes del juego (consola, área de texto de la GUI, etc.). */
@FunctionalInterface
public interface Bitacora {

    void registrar(String mensaje);

    /** Bitácora que imprime en la salida estándar. */
    Bitacora CONSOLA = System.out::println;

    /** Bitácora que descarta los mensajes. */
    Bitacora SILENCIOSA = mensaje -> { };
}

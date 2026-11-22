package escalera.decision;

/** Hojas posibles del árbol de decisión de la llegada. */
public enum Accion {
    NO_SE_MUEVE("la ficha no se mueve"),
    ACIERTA_Y_REPITE("acierta y juega otra vez"),
    FALLA_Y_PIERDE_TURNO("falla y pierde el turno siguiente"),
    SIGUE_ARISTA("se sigue esa arista y se actualiza la tabla"),
    SE_QUEDA("se queda en la casilla");

    private final String descripcion;

    Accion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getDescripcion() {
        return descripcion;
    }
}

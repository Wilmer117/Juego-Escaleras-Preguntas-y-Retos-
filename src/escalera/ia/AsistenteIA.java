package escalera.ia;

import escalera.decision.Accion;
import escalera.estructuras.GrafoSaltos;
import escalera.estructuras.ListaDobleTablero;
import escalera.juego.ConfiguracionTablero;
import escalera.juego.Juego;
import escalera.juego.ResultadoLlegada;
import escalera.modelo.Jugador;

/**
 * Asistente inteligente del juego (punto adicional de IA):
 * 1. Explica cada jugada diciendo qué estructura actuó (lista doble vs. arista del grafo).
 * 2. Analiza el riesgo de la próxima tirada simulando los seis valores del dado.
 * 3. Da pistas de los retos con la base de conocimiento.
 */
public class AsistenteIA {

    private static final int CARAS_DEL_DADO = 6;

    /** Explicación en lenguaje natural de la última llegada. */
    public String explicar(ResultadoLlegada llegada, Juego juego) {
        if (llegada == null) {
            return "Todavía no hay jugadas para explicar.";
        }
        ListaDobleTablero tablero = juego.getTablero();
        GrafoSaltos grafo = juego.getGrafo();
        StringBuilder texto = new StringBuilder();
        texto.append(String.format("%s sacó %d estando en la casilla %d.%n",
                llegada.getJugador(), llegada.getDado(), llegada.getOrigen()));

        if (llegada.getAccion() == Accion.NO_SE_MUEVE) {
            texto.append(String.format("%d + %d = %d pasa de %d, así que la posición guardada en la tabla no cambia.%n",
                    llegada.getOrigen(), llegada.getDado(), llegada.getCasillaDeLlegada(), ConfiguracionTablero.META));
        } else {
            texto.append(String.format("LISTA DOBLE: la ficha avanza %d nodo(s) con 'siguiente' y cae en la %d.%n",
                    llegada.getDado(), llegada.getCasillaDeLlegada()));
            int siguienteFisico = tablero.numeroSiguiente(llegada.getCasillaDeLlegada());
            if (llegada.getAccion() == Accion.SIGUE_ARISTA) {
                texto.append(String.format("En la lista, el siguiente de %d es %d; pero el GRAFO tiene la arista %d -> %d (%s), "
                                + "y por eso la ficha termina en %d. El enlace doble y el salto son cosas distintas.%n",
                        llegada.getCasillaDeLlegada(), siguienteFisico, llegada.getCasillaDeLlegada(),
                        llegada.getPosicionFinal(),
                        llegada.getPosicionFinal() > llegada.getCasillaDeLlegada() ? "escalera" : "serpiente",
                        llegada.getPosicionFinal()));
            } else if (llegada.getPregunta() != null) {
                texto.append(String.format("ÁRBOL DE BÚSQUEDA: la casilla %d tiene dificultad %d, se busca la pregunta %d (%s).%n",
                        llegada.getCasillaDeLlegada(), llegada.getPregunta().getDificultad(),
                        llegada.getPregunta().getClave(), juego.getPreguntas().buscar(llegada.getPregunta().getClave())));
                texto.append(llegada.getAccion() == Accion.ACIERTA_Y_REPITE
                        ? "Acertó: suma un acierto en la TABLA y vuelve a tirar antes de regresar a la COLA.\n"
                        : "Falló: la TABLA marca un fallo y el castigo; la próxima vez que salga de la COLA no tira.\n");
            } else if (grafo.tieneSalto(llegada.getCasillaDeLlegada())) {
                texto.append("La casilla tiene salto, pero no se sigue en esta llegada.\n");
            } else {
                texto.append("No hay reto ni arista en el grafo: se queda.\n");
            }
        }
        texto.append("ÁRBOL DE DECISIÓN: ").append(llegada.getTrazaDecision()).append('\n');
        texto.append("PILA: la jugada quedó en la cima del historial.");
        return texto.toString();
    }

    /**
     * Simula los seis valores del dado para el jugador y resume qué puede pasar.
     * No mueve la ficha: solo consulta la lista y el grafo.
     */
    public String analizarRiesgo(Jugador jugador, GrafoSaltos grafo) {
        int escaleras = 0;
        int serpientes = 0;
        int retos = 0;
        int sinMovimiento = 0;
        int sumaDestinos = 0;
        StringBuilder detalle = new StringBuilder();

        for (int valor = 1; valor <= CARAS_DEL_DADO; valor++) {
            int llegada = jugador.getPosicion() + valor;
            int destino;
            String evento;
            if (llegada > ConfiguracionTablero.META) {
                destino = jugador.getPosicion();
                evento = "no se mueve";
                sinMovimiento++;
            } else if (ConfiguracionTablero.esReto(llegada)) {
                destino = llegada;
                evento = "reto dif. " + ConfiguracionTablero.dificultadDe(llegada);
                retos++;
            } else if (grafo.tieneSalto(llegada)) {
                destino = grafo.destinoDe(llegada);
                boolean sube = destino > llegada;
                evento = (sube ? "escalera a " : "serpiente a ") + destino;
                if (sube) {
                    escaleras++;
                } else {
                    serpientes++;
                }
            } else {
                destino = llegada;
                evento = llegada == ConfiguracionTablero.META ? "¡GANA!" : "normal";
            }
            sumaDestinos += destino;
            detalle.append(String.format("  dado %d → %d: %s%n", valor, llegada, evento));
        }

        double esperado = (double) sumaDestinos / CARAS_DEL_DADO;
        return String.format("Análisis para %s (casilla %d):%n%s"
                        + "Probabilidades: escalera %d/6, serpiente %d/6, reto %d/6, sin movimiento %d/6.%n"
                        + "Posición esperada tras tirar: %.2f. %s",
                jugador.getNombre(), jugador.getPosicion(), detalle, escaleras, serpientes, retos, sinMovimiento,
                esperado, consejo(escaleras, serpientes, sinMovimiento));
    }

    private String consejo(int escaleras, int serpientes, int sinMovimiento) {
        if (sinMovimiento > 0) {
            return "Ya cerca de la meta: necesita el valor exacto.";
        }
        if (serpientes > escaleras) {
            return "Zona peligrosa: hay más serpientes que escaleras al alcance.";
        }
        if (escaleras > 0) {
            return "Buena zona: hay escaleras al alcance.";
        }
        return "Zona tranquila.";
    }

    public String pista(String enunciado) {
        return BaseConocimiento.pistaPara(enunciado);
    }
}

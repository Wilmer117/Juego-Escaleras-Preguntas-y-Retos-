package escalera.ia;

import escalera.juego.TextoUtil;

/**
 * Base de conocimiento de la IA: reglas "si el enunciado menciona X, la respuesta es Y",
 * con una pista en palabras para el jugador humano. Es IA simbólica basada en reglas,
 * específica del tema del laboratorio (estructuras de datos).
 */
public final class BaseConocimiento {

    /** {palabra clave, respuesta, pista}. Se revisan en orden: la primera que coincide gana. */
    private static final String[][] REGLAS = {
        {"2 + 2", "4", "Suma dos manzanas y otras dos manzanas."},
        {"inorden", "sí", "Inorden visita izquierda, raíz y derecha; en un ABB eso es de menor a mayor."},
        {"cielo", "sí", "Mira por la ventana un día sin nubes."},
        {"niveles", "no", "BFS cuenta pasos, no suma pesos distintos; para eso está Dijkstra."},
        {"n − 1", "sí", "Si quitas la raíz de n nodos, ¿cuántos quedan?"},
        {"n - 1", "sí", "Si quitas la raíz de n nodos, ¿cuántos quedan?"},
        {"hoja", "no", "Una hoja es el final de una rama: grado cero."},
        {"kruskal", "sí", "Union-Find dice si dos vértices ya están en el mismo conjunto."},
        {"dijkstra", "sí", "Dijkstra elige siempre la distancia acumulada más pequeña."},
        {"completo", "sí", "Según la definición del curso: cada nodo tiene cero o dos hijos."},
        {"cola", "sí", "FIFO: el primero que llega es el primero que sale."},
        {"pila", "no", "LIFO: el último que entra es el primero que sale."},
        {"grafo", "sí", "Piensa en vértices unidos por aristas."}
    };

    private BaseConocimiento() {
    }

    /** Respuesta inferida por las reglas, o null si ninguna regla aplica. */
    public static String inferirRespuesta(String enunciado) {
        String[] regla = buscarRegla(enunciado);
        return regla == null ? null : regla[1];
    }

    /** Pista para el humano, o una pista genérica si no hay regla. */
    public static String pistaPara(String enunciado) {
        String[] regla = buscarRegla(enunciado);
        return regla == null ? "Relee la pregunta: casi todas se responden con sí o no." : regla[2];
    }

    private static String[] buscarRegla(String enunciado) {
        String texto = TextoUtil.normalizar(enunciado);
        for (String[] regla : REGLAS) {
            if (texto.contains(TextoUtil.normalizar(regla[0]))) {
                return regla;
            }
        }
        return null;
    }
}

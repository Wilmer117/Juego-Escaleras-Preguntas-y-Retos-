package escalera.juego;

import escalera.modelo.Pregunta;

/** Datos fijos del enunciado: tamaño, saltos, retos, preguntas y corrida de verificación. */
public final class ConfiguracionTablero {

    public static final int CASILLAS_PRIMERA_PARTE = 20;
    public static final int META = 50;
    public static final int COLUMNAS_TABLERO = 10;
    public static final int NUMERO_CUBETAS = 4;
    public static final int JUGADAS_A_CONSULTAR = 5;
    public static final int MINIMO_JUGADORES = 2;
    public static final int MAXIMO_JUGADORES = 4;

    public static final int[] CASILLAS_RETO = {5, 12, 20, 27, 33, 41};

    /** {origen, destino}: 3 escaleras y 2 serpientes. */
    public static final int[][] SALTOS = {{3, 11}, {8, 15}, {14, 19}, {17, 4}, {18, 6}};

    /** Aristas de prueba para Union-Find: 11—8 se acepta y 15—3 se rechaza. */
    public static final int[][] ARISTAS_DE_PRUEBA = {{11, 8}, {15, 3}};

    public static final String[] JUGADORES_CORRIDA = {"Ana", "Luis", "Marta"};
    public static final int[] DADOS_CORRIDA = {3, 4, 8, 1, 6};
    /** En el reto de la casilla 12, Ana responde 5 a "¿cuánto es 2 + 2?". */
    public static final String[] RESPUESTAS_CORRIDA = {"5"};

    private static final int LIMITE_DIFICULTAD_1 = 15;
    private static final int LIMITE_DIFICULTAD_2 = 35;

    private ConfiguracionTablero() {
    }

    public static boolean esReto(int casilla) {
        for (int reto : CASILLAS_RETO) {
            if (reto == casilla) {
                return true;
            }
        }
        return false;
    }

    /** 1 si la casilla es ≤ 15, 2 si no pasa de 35, 3 en otro caso. */
    public static int dificultadDe(int casilla) {
        if (casilla <= LIMITE_DIFICULTAD_1) {
            return 1;
        }
        return casilla <= LIMITE_DIFICULTAD_2 ? 2 : 3;
    }

    /** Las diez preguntas en el orden de inserción del enunciado (columna izquierda y luego derecha). */
    public static Pregunta[] preguntasPorDefecto() {
        return new Pregunta[]{
            new Pregunta(201, "¿El inorden de un árbol de búsqueda sale ordenado?", "sí"),
            new Pregunta(302, "¿La búsqueda por niveles garantiza el mínimo si los pesos difieren?", "no"),
            new Pregunta(101, "¿Cuánto es 2 + 2?", "4"),
            new Pregunta(203, "¿Kruskal detecta ciclos con Union-Find?", "sí"),
            new Pregunta(303, "¿Un árbol completo pide cero o dos hijos en cada nodo?", "sí"),
            new Pregunta(102, "¿El cielo despejado se ve azul?", "sí"),
            new Pregunta(301, "¿El peso del árbol, sin contar la raíz, es n − 1?", "sí"),
            new Pregunta(104, "¿Una hoja tiene hijos?", "no"),
            new Pregunta(202, "¿Dijkstra tiene en cuenta los pesos?", "sí"),
            new Pregunta(103, "¿El orden de una cola es el de llegada?", "sí")
        };
    }
}

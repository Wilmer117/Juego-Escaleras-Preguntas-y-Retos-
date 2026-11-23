package escalera.estructuras;

import escalera.modelo.Arista;

/**
 * Valida que los saltos, mirados sin dirección, no formen un ciclo.
 * Se ofrecen dos caminos que deben coincidir: Kruskal (con Union-Find) y Prim (pesos = 1).
 * Las aristas de prueba solo se validan: nunca se agregan al grafo de la partida.
 */
public class ValidadorCiclos {

    private final int maximoVertice;

    public ValidadorCiclos(int maximoVertice) {
        this.maximoVertice = maximoVertice;
    }

    /**
     * Kruskal: ordena por peso (orden estable) y acepta una arista
     * solo si une dos conjuntos distintos.
     */
    public ResultadoValidacion[] validarConKruskal(Arista[] aristas) {
        Arista[] ordenadas = ordenarPorPesoEstable(aristas);
        UnionFind conjuntos = new UnionFind(maximoVertice + 1);
        ResultadoValidacion[] resultados = new ResultadoValidacion[ordenadas.length];
        for (int i = 0; i < ordenadas.length; i++) {
            Arista arista = ordenadas[i];
            int a = arista.getOrigen();
            int b = arista.getDestino();
            if (conjuntos.unir(a, b)) {
                resultados[i] = new ResultadoValidacion(arista, true,
                        "une conjuntos separados → {" + conjuntos.miembrosDe(a) + "}");
            } else {
                resultados[i] = new ResultadoValidacion(arista, false,
                        "cerraría un ciclo: " + a + " y " + b + " ya están en {" + conjuntos.miembrosDe(a) + "}");
            }
        }
        return resultados;
    }

    /**
     * Prim sobre el mismo grafo sin dirección, todas las aristas con peso 1.
     * Crece un bosque desde cada vértice no visitado; en empate gana la arista agregada primero.
     * Toda arista que no termina en el árbol de expansión se reporta como rechazada.
     */
    public ResultadoValidacion[] validarConPrim(Arista[] aristas) {
        boolean[] enArbol = new boolean[maximoVertice + 1];
        boolean[] aceptada = new boolean[aristas.length];

        for (int inicio = 1; inicio <= maximoVertice; inicio++) {
            if (enArbol[inicio] || !tocaVertice(aristas, inicio)) {
                continue;
            }
            enArbol[inicio] = true;
            int elegida;
            while ((elegida = aristaMinimaQueSale(aristas, enArbol)) != -1) {
                aceptada[elegida] = true;
                enArbol[aristas[elegida].getOrigen()] = true;
                enArbol[aristas[elegida].getDestino()] = true;
            }
        }

        ResultadoValidacion[] resultados = new ResultadoValidacion[aristas.length];
        for (int i = 0; i < aristas.length; i++) {
            resultados[i] = new ResultadoValidacion(aristas[i], aceptada[i],
                    aceptada[i] ? "entra al árbol de expansión" : "sus dos extremos ya estaban en el árbol");
        }
        return resultados;
    }

    /** Índice de la arista más liviana con un extremo dentro y otro fuera del árbol; -1 si no hay. */
    private int aristaMinimaQueSale(Arista[] aristas, boolean[] enArbol) {
        int mejor = -1;
        for (int i = 0; i < aristas.length; i++) {
            boolean origenDentro = enArbol[aristas[i].getOrigen()];
            boolean destinoDentro = enArbol[aristas[i].getDestino()];
            boolean cruzaLaFrontera = origenDentro != destinoDentro;
            if (cruzaLaFrontera && (mejor == -1 || aristas[i].getPeso() < aristas[mejor].getPeso())) {
                mejor = i;
            }
        }
        return mejor;
    }

    private boolean tocaVertice(Arista[] aristas, int vertice) {
        for (Arista arista : aristas) {
            if (arista.getOrigen() == vertice || arista.getDestino() == vertice) {
                return true;
            }
        }
        return false;
    }

    /** Ordenamiento por inserción (estable): con pesos iguales conserva el orden original. */
    private Arista[] ordenarPorPesoEstable(Arista[] aristas) {
        Arista[] copia = new Arista[aristas.length];
        for (int i = 0; i < aristas.length; i++) {
            copia[i] = aristas[i];
        }
        for (int i = 1; i < copia.length; i++) {
            Arista actual = copia[i];
            int j = i - 1;
            while (j >= 0 && copia[j].getPeso() > actual.getPeso()) {
                copia[j + 1] = copia[j];
                j--;
            }
            copia[j + 1] = actual;
        }
        return copia;
    }
}

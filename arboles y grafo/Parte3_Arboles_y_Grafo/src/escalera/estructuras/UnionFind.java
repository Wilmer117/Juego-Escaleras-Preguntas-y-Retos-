package escalera.estructuras;

/**
 * Conjuntos disjuntos con compresión de camino y unión por rango.
 * Kruskal lo usa para saber si dos casillas ya están conectadas.
 */
public class UnionFind {

    private final int[] padre;
    private final int[] rango;

    public UnionFind(int cantidadElementos) {
        padre = new int[cantidadElementos];
        rango = new int[cantidadElementos];
        for (int i = 0; i < cantidadElementos; i++) {
            padre[i] = i;
        }
    }

    /** Representante del conjunto de x. */
    public int buscar(int x) {
        if (padre[x] != x) {
            padre[x] = buscar(padre[x]);
        }
        return padre[x];
    }

    public boolean estanConectados(int a, int b) {
        return buscar(a) == buscar(b);
    }

    /**
     * Une los conjuntos de a y b.
     * @return false si ya estaban en el mismo conjunto (la arista cerraría un ciclo).
     */
    public boolean unir(int a, int b) {
        int raizA = buscar(a);
        int raizB = buscar(b);
        if (raizA == raizB) {
            return false;
        }
        if (rango[raizA] < rango[raizB]) {
            padre[raizA] = raizB;
        } else if (rango[raizA] > rango[raizB]) {
            padre[raizB] = raizA;
        } else {
            padre[raizB] = raizA;
            rango[raizA]++;
        }
        return true;
    }

    /** Miembros del conjunto de x, en orden creciente, separados por coma. */
    public String miembrosDe(int x) {
        StringBuilder texto = new StringBuilder();
        int raiz = buscar(x);
        for (int i = 0; i < padre.length; i++) {
            if (buscar(i) == raiz) {
                if (texto.length() > 0) {
                    texto.append(", ");
                }
                texto.append(i);
            }
        }
        return texto.toString();
    }
}

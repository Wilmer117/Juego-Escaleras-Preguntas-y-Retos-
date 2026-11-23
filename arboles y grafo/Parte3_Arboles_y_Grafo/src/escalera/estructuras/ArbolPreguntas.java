package escalera.estructuras;

import escalera.modelo.Pregunta;

/**
 * Árbol binario de búsqueda de preguntas. Clave = dificultad * 100 + identificador.
 * El inorden sale ordenado y, por tanto, agrupado por dificultad.
 */
public class ArbolPreguntas {

    private static class Nodo {
        private final Pregunta pregunta;
        private Nodo izquierdo;
        private Nodo derecho;

        Nodo(Pregunta pregunta) {
            this.pregunta = pregunta;
        }

        int clave() {
            return pregunta.getClave();
        }

        int grado() {
            return (izquierdo != null ? 1 : 0) + (derecho != null ? 1 : 0);
        }
    }

    private static final int DIFICULTAD_MAXIMA = 9;

    private Nodo raiz;
    private int tamano;
    /** Cuántas veces se ha pedido una pregunta de cada dificultad (para rotarlas). */
    private final int[] usosPorDificultad = new int[DIFICULTAD_MAXIMA + 1];

    /**
     * Inserta iterativamente. Una clave repetida no se inserta.
     * @return true si se insertó.
     */
    public boolean insertar(Pregunta pregunta) {
        Nodo nuevo = new Nodo(pregunta);
        if (raiz == null) {
            raiz = nuevo;
            tamano++;
            return true;
        }
        Nodo actual = raiz;
        while (true) {
            if (pregunta.getClave() == actual.clave()) {
                return false;
            }
            if (pregunta.getClave() < actual.clave()) {
                if (actual.izquierdo == null) {
                    actual.izquierdo = nuevo;
                    break;
                }
                actual = actual.izquierdo;
            } else {
                if (actual.derecho == null) {
                    actual.derecho = nuevo;
                    break;
                }
                actual = actual.derecho;
            }
        }
        tamano++;
        return true;
    }

    /** Busca una clave e informa camino, nivel (raíz = 0) y grado. */
    public ResultadoBusqueda buscar(int clave) {
        StringBuilder camino = new StringBuilder();
        Nodo actual = raiz;
        int nivel = 0;
        while (actual != null) {
            if (camino.length() > 0) {
                camino.append(" → ");
            }
            camino.append(actual.clave());
            if (clave == actual.clave()) {
                return new ResultadoBusqueda(actual.pregunta, camino.toString(), nivel, actual.grado());
            }
            actual = clave < actual.clave() ? actual.izquierdo : actual.derecho;
            nivel++;
        }
        return new ResultadoBusqueda(null, camino.toString(), -1, -1);
    }

    /** Recorrido inorden (izquierdo, raíz, derecho). */
    public Pregunta[] inorden() {
        Pregunta[] resultado = new Pregunta[tamano];
        llenarInorden(raiz, resultado, new int[]{0});
        return resultado;
    }

    private void llenarInorden(Nodo nodo, Pregunta[] destino, int[] indice) {
        if (nodo == null) {
            return;
        }
        llenarInorden(nodo.izquierdo, destino, indice);
        destino[indice[0]++] = nodo.pregunta;
        llenarInorden(nodo.derecho, destino, indice);
    }

    public String inordenComoTexto() {
        StringBuilder texto = new StringBuilder();
        Pregunta[] preguntas = inorden();
        for (int i = 0; i < preguntas.length; i++) {
            texto.append(preguntas[i].getClave());
            if (i < preguntas.length - 1) {
                texto.append(", ");
            }
        }
        return texto.toString();
    }

    /**
     * Devuelve una pregunta de la dificultad pedida (claves entre d*100 y d*100+99).
     * Las preguntas se entregan en orden de clave y se rotan cuando se acaban.
     * @return la pregunta, o null si no hay ninguna de esa dificultad.
     */
    public Pregunta preguntaDeDificultad(int dificultad) {
        int cantidad = contarEnRango(raiz, dificultad * 100, dificultad * 100 + 99);
        if (cantidad == 0) {
            return null;
        }
        int posicionBuscada = usosPorDificultad[dificultad] % cantidad;
        usosPorDificultad[dificultad]++;
        return buscarEnRango(raiz, dificultad * 100, dificultad * 100 + 99, new int[]{posicionBuscada});
    }

    private int contarEnRango(Nodo nodo, int minimo, int maximo) {
        if (nodo == null) {
            return 0;
        }
        int propio = (nodo.clave() >= minimo && nodo.clave() <= maximo) ? 1 : 0;
        return propio + contarEnRango(nodo.izquierdo, minimo, maximo) + contarEnRango(nodo.derecho, minimo, maximo);
    }

    /** Inorden restringido al rango; se detiene en la k-ésima clave encontrada. */
    private Pregunta buscarEnRango(Nodo nodo, int minimo, int maximo, int[] restantes) {
        if (nodo == null) {
            return null;
        }
        if (nodo.clave() > minimo) {
            Pregunta izquierda = buscarEnRango(nodo.izquierdo, minimo, maximo, restantes);
            if (izquierda != null) {
                return izquierda;
            }
        }
        if (nodo.clave() >= minimo && nodo.clave() <= maximo) {
            if (restantes[0] == 0) {
                return nodo.pregunta;
            }
            restantes[0]--;
        }
        if (nodo.clave() < maximo) {
            return buscarEnRango(nodo.derecho, minimo, maximo, restantes);
        }
        return null;
    }

    /** Peso = número de nodos sin contar la raíz. */
    public int peso() {
        return tamano == 0 ? 0 : tamano - 1;
    }

    /** Altura medida en aristas: un árbol de un solo nodo tiene altura 0. */
    public int altura() {
        return alturaDesde(raiz);
    }

    private int alturaDesde(Nodo nodo) {
        if (nodo == null) {
            return -1;
        }
        return 1 + Math.max(alturaDesde(nodo.izquierdo), alturaDesde(nodo.derecho));
    }

    public int hojas() {
        return contarHojas(raiz);
    }

    private int contarHojas(Nodo nodo) {
        if (nodo == null) {
            return 0;
        }
        if (nodo.grado() == 0) {
            return 1;
        }
        return contarHojas(nodo.izquierdo) + contarHojas(nodo.derecho);
    }

    /** Completo, según el criterio del curso: cada nodo tiene cero o dos hijos. */
    public boolean esCompleto() {
        return esCompletoDesde(raiz);
    }

    private boolean esCompletoDesde(Nodo nodo) {
        if (nodo == null) {
            return true;
        }
        if (nodo.grado() == 1) {
            return false;
        }
        return esCompletoDesde(nodo.izquierdo) && esCompletoDesde(nodo.derecho);
    }

    /** Dibujo del árbol de lado (la raíz a la izquierda) para consola o GUI. */
    public String dibujar() {
        StringBuilder texto = new StringBuilder();
        dibujarDesde(raiz, "", true, texto);
        return texto.toString();
    }

    private void dibujarDesde(Nodo nodo, String prefijo, boolean esUltimo, StringBuilder texto) {
        if (nodo == null) {
            return;
        }
        texto.append(prefijo).append(nodo == raiz ? "" : (esUltimo ? "└─ " : "├─ "))
             .append(nodo.clave()).append('\n');
        String nuevoPrefijo = nodo == raiz ? "" : prefijo + (esUltimo ? "   " : "│  ");
        if (nodo.izquierdo != null) {
            dibujarDesde(nodo.izquierdo, nuevoPrefijo, nodo.derecho == null, texto);
        }
        if (nodo.derecho != null) {
            dibujarDesde(nodo.derecho, nuevoPrefijo, true, texto);
        }
    }

    public int getTamano() {
        return tamano;
    }

    public boolean estaVacio() {
        return raiz == null;
    }
}

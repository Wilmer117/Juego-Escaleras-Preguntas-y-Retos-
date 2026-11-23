package escalera.juego;

import escalera.decision.Accion;
import escalera.decision.ArbolDecision;
import escalera.decision.ContextoLlegada;
import escalera.estructuras.ArbolPreguntas;
import escalera.estructuras.ArbolRanking;
import escalera.estructuras.ColaTurnos;
import escalera.estructuras.GrafoSaltos;
import escalera.estructuras.ListaDobleTablero;
import escalera.estructuras.PilaJugadas;
import escalera.estructuras.TablaJugadores;
import escalera.modelo.Jugada;
import escalera.modelo.Jugador;
import escalera.modelo.Pregunta;

/**
 * Motor del juego. Cada estructura tiene un solo papel:
 * lista doble = orden físico, cola = turno, pila = historial, tabla = estado,
 * árbol de búsqueda = preguntas, árbol de decisión = regla, grafo = saltos.
 */
public class Juego {

    private final ListaDobleTablero tablero;
    private final GrafoSaltos grafo;
    private final ColaTurnos cola;
    private final TablaJugadores tabla;
    private final PilaJugadas historial;
    private final ArbolPreguntas preguntas;
    private final ArbolDecision regla;
    private final String[] ordenDeRegistro;

    private Dado dado;
    private ProveedorRespuestas proveedorRespuestas;
    private Bitacora bitacora;
    private ResultadoLlegada ultimaLlegada;
    private String ganador;

    public Juego(Jugador[] jugadores, Pregunta[] bancoDePreguntas, Dado dado,
                 ProveedorRespuestas proveedorRespuestas, Bitacora bitacora) {
        this.dado = dado;
        this.proveedorRespuestas = proveedorRespuestas;
        this.bitacora = bitacora;
        this.grafo = FabricaTablero.crearGrafoDeSaltos(ConfiguracionTablero.META);
        this.tablero = FabricaTablero.crearTablero(ConfiguracionTablero.META, grafo);
        this.cola = new ColaTurnos();
        this.tabla = new TablaJugadores(ConfiguracionTablero.NUMERO_CUBETAS);
        this.historial = new PilaJugadas();
        this.preguntas = new ArbolPreguntas();
        this.regla = new ArbolDecision(ConfiguracionTablero.META);
        this.ordenDeRegistro = new String[jugadores.length];

        for (Pregunta pregunta : bancoDePreguntas) {
            preguntas.insertar(pregunta);
        }
        for (int i = 0; i < jugadores.length; i++) {
            registrarJugador(jugadores[i]);
            ordenDeRegistro[i] = jugadores[i].getNombre();
        }
    }

    private void registrarJugador(Jugador jugador) {
        if (tabla.registrar(jugador)) {
            cola.encolar(jugador.getNombre());
        } else {
            bitacora.registrar("Aviso: el nombre " + jugador.getNombre() + " ya estaba registrado.");
        }
    }

    /** Pone en la cola un nombre aunque no esté en la tabla (sirve para probar el aviso). */
    public void encolarNombre(String nombre) {
        cola.encolar(nombre);
    }

    /**
     * Juega un turno completo: saca al jugador del frente, aplica castigo o tira,
     * repite si acierta un reto y lo devuelve al final si no ganó.
     * @return false si no se pudo jugar (cola vacía, partida terminada o dado agotado).
     */
    public boolean jugarTurno() {
        if (hayGanador()) {
            bitacora.registrar("Aviso: la partida ya terminó. Ganó " + ganador + ".");
            return false;
        }
        if (cola.estaVacia()) {
            bitacora.registrar("Aviso: la cola de turnos está vacía, no hay quién juegue.");
            return false;
        }
        if (!dado.quedanTiradas()) {
            bitacora.registrar("Aviso: el dado fijo ya no tiene más valores.");
            return false;
        }

        String nombre = cola.desencolar();
        Jugador jugador = tabla.buscar(nombre);
        if (jugador == null) {
            bitacora.registrar("Aviso: " + nombre + " no está registrado en la tabla; no juega.");
            return true;
        }
        if (jugador.tieneCastigo()) {
            jugador.consumirCastigo();
            bitacora.registrar(nombre + " paga su castigo: no tira y vuelve al final de la cola.");
            cola.encolar(nombre);
            return true;
        }

        boolean repite;
        do {
            ResultadoLlegada resultado = tirarYResolver(jugador);
            repite = resultado.getAccion() == Accion.ACIERTA_Y_REPITE && dado.quedanTiradas() && !hayGanador();
            if (repite) {
                bitacora.registrar("   " + nombre + " juega otra vez en el mismo ciclo.");
            }
        } while (repite);

        if (!hayGanador()) {
            cola.encolar(nombre);
        }
        return true;
    }

    private ResultadoLlegada tirarYResolver(Jugador jugador) {
        int valor = dado.tirar();
        ResultadoLlegada resultado = aplicarLlegada(jugador, valor);
        bitacora.registrar("Dado " + valor + ": " + resultado.resumen());
        if (jugador.getPosicion() == ConfiguracionTablero.META) {
            ganador = jugador.getNombre();
            bitacora.registrar("¡" + ganador + " llegó exactamente a la casilla "
                    + ConfiguracionTablero.META + " y gana!");
        }
        return resultado;
    }

    /**
     * Resuelve una llegada bajando por el árbol de decisión y actualiza el jugador
     * (que vive en la tabla). También apila la jugada en el historial.
     */
    public ResultadoLlegada aplicarLlegada(Jugador jugador, int valorDado) {
        int origen = jugador.getPosicion();
        ContextoLlegada contexto = new ContextoLlegada(jugador, valorDado, ConfiguracionTablero.META,
                grafo, preguntas, proveedorRespuestas);
        int llegada = contexto.casillaDeLlegada();
        StringBuilder traza = new StringBuilder();
        Accion accion = regla.decidir(contexto, traza);

        switch (accion) {
            case NO_SE_MUEVE:
                break;
            case ACIERTA_Y_REPITE:
                jugador.moverA(llegada);
                jugador.registrarAcierto();
                break;
            case FALLA_Y_PIERDE_TURNO:
                jugador.moverA(llegada);
                jugador.registrarFallo();
                break;
            case SIGUE_ARISTA:
                jugador.moverA(grafo.destinoDe(llegada));
                break;
            default:
                jugador.moverA(llegada);
        }

        ultimaLlegada = new ResultadoLlegada(jugador.getNombre(), valorDado, origen, llegada,
                jugador.getPosicion(), accion, traza.toString(),
                contexto.getPreguntaFormulada(), contexto.getRespuestaDada());
        historial.apilar(new Jugada(jugador.getNombre(), valorDado, origen, jugador.getPosicion(),
                accion.getDescripcion()));
        return ultimaLlegada;
    }

    /** Juega hasta que alguien gane o se agote el dado. Tiene un tope para no ciclar sin fin. */
    public void jugarHastaGanador(int maximoTurnos) {
        int turnos = 0;
        while (!hayGanador() && turnos < maximoTurnos && jugarTurno()) {
            turnos++;
        }
    }

    /** Ranking final (cierre opcional) usando un segundo árbol ordenado por aciertos. */
    public String ranking() {
        ArbolRanking arbol = new ArbolRanking();
        for (Jugador jugador : tabla.todos()) {
            arbol.insertar(jugador);
        }
        return arbol.describirRanking();
    }

    /** Jugadores en el orden en que se registraron (para la GUI). */
    public Jugador[] jugadoresEnOrden() {
        Jugador[] jugadores = new Jugador[ordenDeRegistro.length];
        for (int i = 0; i < ordenDeRegistro.length; i++) {
            jugadores[i] = tabla.buscar(ordenDeRegistro[i]);
        }
        return jugadores;
    }

    public boolean hayGanador() {
        return ganador != null;
    }

    public String getGanador() {
        return ganador;
    }

    public ResultadoLlegada getUltimaLlegada() {
        return ultimaLlegada;
    }

    public ListaDobleTablero getTablero() {
        return tablero;
    }

    public GrafoSaltos getGrafo() {
        return grafo;
    }

    public ColaTurnos getCola() {
        return cola;
    }

    public TablaJugadores getTabla() {
        return tabla;
    }

    public PilaJugadas getHistorial() {
        return historial;
    }

    public ArbolPreguntas getPreguntas() {
        return preguntas;
    }

    public ArbolDecision getRegla() {
        return regla;
    }

    public Dado getDado() {
        return dado;
    }

    public void setDado(Dado dado) {
        this.dado = dado;
    }

    public void setProveedorRespuestas(ProveedorRespuestas proveedorRespuestas) {
        this.proveedorRespuestas = proveedorRespuestas;
    }

    public void setBitacora(Bitacora bitacora) {
        this.bitacora = bitacora;
    }
}

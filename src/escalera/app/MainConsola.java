package escalera.app;

import escalera.estructuras.ArbolPreguntas;
import escalera.estructuras.ColaTurnos;
import escalera.estructuras.GrafoSaltos;
import escalera.estructuras.ListaDobleTablero;
import escalera.estructuras.ResultadoValidacion;
import escalera.estructuras.TablaJugadores;
import escalera.estructuras.ValidadorCiclos;
import escalera.ia.JugadorIA;
import escalera.juego.Bitacora;
import escalera.juego.ConfiguracionTablero;
import escalera.juego.DadoAleatorio;
import escalera.juego.DadoFijo;
import escalera.juego.FabricaTablero;
import escalera.juego.Juego;
import escalera.juego.RepresentadorTablero;
import escalera.juego.RespuestasFijas;
import escalera.modelo.Arista;
import escalera.modelo.Jugada;
import escalera.modelo.Jugador;
import escalera.modelo.Pregunta;

import java.io.FileDescriptor;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;

/**
 * Programa de consola con la corrida de verificación del enunciado.
 * Siempre da el mismo resultado porque el dado es fijo: 3, 4, 8, 1, 6.
 * Con el argumento "aleatoria" juega además una partida con azar hasta que haya ganador.
 */
public class MainConsola {

    private static final long SEMILLA_PARTIDA_ALEATORIA = 2026L;
    private static final int TOPE_DE_TURNOS = 500;

    public static void main(String[] args) {
        usarSalidaUtf8();
        primeraParte();
        segundaParte();
        if (args.length > 0 && args[0].equalsIgnoreCase("aleatoria")) {
            partidaAleatoria();
        }
    }

    // ───────────────────────────── PRIMERA PARTE ─────────────────────────────

    private static void primeraParte() {
        titulo("PRIMERA PARTE");
        GrafoSaltos grafo = FabricaTablero.crearGrafoDeSaltos(ConfiguracionTablero.META);
        ListaDobleTablero tablero = FabricaTablero.crearTablero(ConfiguracionTablero.CASILLAS_PRIMERA_PARTE, grafo);
        mostrarTablero(tablero, grafo);
        mostrarCola();
        mostrarTabla();
        mostrarArbolDePreguntas();
        mostrarValidacionDeCiclos(grafo);

        subtitulo("Extensión del tablero");
        FabricaTablero.extenderHasta(tablero, ConfiguracionTablero.META, grafo);
        System.out.println("La lista se alarga de 20 a " + tablero.getTamano() + " casillas conservando los saltos.");
        System.out.println("Ida: " + tablero.recorrerIda());
    }

    private static void mostrarTablero(ListaDobleTablero tablero, GrafoSaltos grafo) {
        subtitulo("Tablero (lista doble) y saltos (grafo)");
        System.out.println("Ida:     " + tablero.recorrerIda());
        System.out.println("Regreso: " + tablero.recorrerRegreso());
        System.out.println("El siguiente de 3 es " + tablero.numeroSiguiente(3)
                + " (lista doble); el salto de 3 es " + grafo.destinoDe(3) + " (grafo).");
        System.out.println("El anterior de 4 es " + tablero.numeroAnterior(4) + ".");
        System.out.print(grafo.describir());
    }

    private static void mostrarCola() {
        subtitulo("Cola de turnos: cinco rotaciones");
        ColaTurnos cola = new ColaTurnos();
        for (String nombre : ConfiguracionTablero.JUGADORES_CORRIDA) {
            cola.encolar(nombre);
        }
        System.out.println("Cola inicial: " + cola.describir());
        StringBuilder rotaciones = new StringBuilder();
        for (int i = 0; i < ConfiguracionTablero.DADOS_CORRIDA.length; i++) {
            String nombre = cola.desencolar();
            rotaciones.append(nombre).append(i < ConfiguracionTablero.DADOS_CORRIDA.length - 1 ? ", " : "");
            cola.encolar(nombre);
        }
        System.out.println("Turnos: " + rotaciones);
    }

    private static void mostrarTabla() {
        subtitulo("Tabla hash de jugadores (4 cubetas)");
        TablaJugadores tabla = new TablaJugadores(ConfiguracionTablero.NUMERO_CUBETAS);
        for (String nombre : ConfiguracionTablero.JUGADORES_CORRIDA) {
            tabla.registrar(new Jugador(nombre));
        }
        System.out.print(tabla.describir());
        System.out.println(tabla.describirBusqueda("Luis"));
        System.out.println(tabla.describirBusqueda("Pedro"));
    }

    private static void mostrarArbolDePreguntas() {
        subtitulo("Árbol binario de búsqueda de preguntas");
        ArbolPreguntas arbol = new ArbolPreguntas();
        StringBuilder orden = new StringBuilder();
        for (Pregunta pregunta : ConfiguracionTablero.preguntasPorDefecto()) {
            arbol.insertar(pregunta);
            orden.append(pregunta.getClave()).append(' ');
        }
        System.out.println("Orden de inserción: " + orden.toString().trim());
        System.out.println("Inorden: " + arbol.inordenComoTexto());
        System.out.print(arbol.dibujar());
        System.out.println("Buscar 101: " + arbol.buscar(101));
        System.out.printf("Peso %d, altura %d, hojas %d, completo: %s%n",
                arbol.peso(), arbol.altura(), arbol.hojas(), arbol.esCompleto() ? "sí" : "no");
    }

    private static void mostrarValidacionDeCiclos(GrafoSaltos grafo) {
        subtitulo("Validación de ciclos (saltos sin dirección)");
        Arista[] candidatas = aristasRealesYDePrueba(grafo);
        ValidadorCiclos validador = new ValidadorCiclos(ConfiguracionTablero.META);
        System.out.println("Kruskal + Union-Find:");
        for (ResultadoValidacion resultado : validador.validarConKruskal(candidatas)) {
            System.out.println("  " + resultado);
        }
        System.out.println("Prim (todas las aristas con peso 1):");
        for (ResultadoValidacion resultado : validador.validarConPrim(candidatas)) {
            System.out.println("  " + resultado);
        }
        System.out.println("15—3 no entra al tablero. En la partida, 3 lleva a 11 y no al revés: "
                + "destino de 3 = " + grafo.destinoDe(3) + ", destino de 11 = " + grafo.destinoDe(11) + ".");
    }

    /** Los cinco saltos reales seguidos de las aristas de prueba (que no se agregan al grafo). */
    static Arista[] aristasRealesYDePrueba(GrafoSaltos grafo) {
        Arista[] reales = grafo.aristas();
        Arista[] todas = new Arista[reales.length + ConfiguracionTablero.ARISTAS_DE_PRUEBA.length];
        System.arraycopy(reales, 0, todas, 0, reales.length);
        for (int i = 0; i < ConfiguracionTablero.ARISTAS_DE_PRUEBA.length; i++) {
            int[] prueba = ConfiguracionTablero.ARISTAS_DE_PRUEBA[i];
            todas[reales.length + i] = new Arista(prueba[0], prueba[1]);
        }
        return todas;
    }

    // ───────────────────────────── SEGUNDA PARTE ─────────────────────────────

    private static void segundaParte() {
        titulo("SEGUNDA PARTE — CORRIDA DE VERIFICACIÓN (dados 3, 4, 8, 1, 6)");
        Juego juego = crearJuegoDeVerificacion(Bitacora.CONSOLA);
        System.out.print(juego.getRegla().dibujar());
        System.out.println();

        while (juego.getDado().quedanTiradas()) {
            juego.jugarTurno();
        }

        subtitulo("Estado después de la corrida");
        System.out.print(RepresentadorTablero.dibujar(juego.getTablero(), juego.jugadoresEnOrden()));
        System.out.print(juego.getTabla().describir());
        System.out.println("Cola: " + juego.getCola().describir());
        System.out.println("Últimas jugadas (la primera es la cima, la más reciente):");
        for (Jugada jugada : juego.getHistorial().ultimas(ConfiguracionTablero.JUGADAS_A_CONSULTAR)) {
            System.out.println("  " + jugada);
        }

        probarMeta();
        probarAvisos();
    }

    /** Juego con los datos fijos del enunciado; lo usan la consola y la GUI. */
    public static Juego crearJuegoDeVerificacion(Bitacora bitacora) {
        Jugador[] jugadores = new Jugador[ConfiguracionTablero.JUGADORES_CORRIDA.length];
        for (int i = 0; i < jugadores.length; i++) {
            jugadores[i] = new Jugador(ConfiguracionTablero.JUGADORES_CORRIDA[i]);
        }
        return new Juego(jugadores, ConfiguracionTablero.preguntasPorDefecto(),
                new DadoFijo(ConfiguracionTablero.DADOS_CORRIDA),
                new RespuestasFijas(ConfiguracionTablero.RESPUESTAS_CORRIDA), bitacora);
    }

    private static void probarMeta() {
        subtitulo("Llegada exacta a la meta");
        Juego juegoDePrueba = crearJuegoDeVerificacion(Bitacora.SILENCIOSA);
        Jugador enCuarentaYOcho = new Jugador("Prueba48");
        enCuarentaYOcho.moverA(48);
        System.out.println(juegoDePrueba.aplicarLlegada(enCuarentaYOcho, 5).resumen()
                + " → posición guardada: " + enCuarentaYOcho.getPosicion());
        Jugador enCuarentaYSiete = new Jugador("Prueba47");
        enCuarentaYSiete.moverA(47);
        System.out.println(juegoDePrueba.aplicarLlegada(enCuarentaYSiete, 3).resumen()
                + (enCuarentaYSiete.getPosicion() == ConfiguracionTablero.META ? " → llega a 50 y gana" : ""));
    }

    private static void probarAvisos() {
        subtitulo("Avisos que no detienen el programa");
        Juego juego = new Juego(new Jugador[]{new Jugador("Ana")}, ConfiguracionTablero.preguntasPorDefecto(),
                new DadoFijo(2, 2), new RespuestasFijas(), Bitacora.CONSOLA);
        juego.getCola().desencolar();
        juego.encolarNombre("Pedro");
        juego.jugarTurno();
        juego.jugarTurno();
        System.out.println("El programa sigue funcionando después de los avisos.");
    }

    // ─────────────────────────── PARTIDA ALEATORIA ───────────────────────────

    private static void partidaAleatoria() {
        titulo("PARTIDA ALEATORIA (no reemplaza la corrida de verificación)");
        Jugador[] jugadores = {new Jugador("Ana", true), new Jugador("Luis", true), new Jugador("Marta", true)};
        Juego juego = new Juego(jugadores, ConfiguracionTablero.preguntasPorDefecto(),
                new DadoAleatorio(SEMILLA_PARTIDA_ALEATORIA), new JugadorIA(SEMILLA_PARTIDA_ALEATORIA),
                Bitacora.CONSOLA);
        juego.jugarHastaGanador(TOPE_DE_TURNOS);
        System.out.print(RepresentadorTablero.dibujar(juego.getTablero(), juego.jugadoresEnOrden()));
        System.out.println("Ranking (árbol ordenado por aciertos):");
        System.out.print(juego.ranking());
    }

    // ───────────────────────────── utilidades ─────────────────────────────

    /** Para que tildes y flechas se vean bien en la consola (en Windows, además: chcp 65001). */
    private static void usarSalidaUtf8() {
        System.setOut(new PrintStream(new FileOutputStream(FileDescriptor.out), true, StandardCharsets.UTF_8));
    }

    private static void titulo(String texto) {
        System.out.println();
        System.out.println("════════════════════════════════════════════════════════════════════");
        System.out.println(texto);
        System.out.println("════════════════════════════════════════════════════════════════════");
    }

    private static void subtitulo(String texto) {
        System.out.println();
        System.out.println("── " + texto + " ──");
    }
}

package escalera.app;

import escalera.estructuras.ResultadoValidacion;
import escalera.estructuras.ValidadorCiclos;
import escalera.ia.AsistenteIA;
import escalera.ia.JugadorIA;
import escalera.juego.Bitacora;
import escalera.juego.CargadorPreguntas;
import escalera.juego.ConfiguracionTablero;
import escalera.juego.DadoAleatorio;
import escalera.juego.Juego;
import escalera.juego.ResultadoLlegada;
import escalera.modelo.Jugada;
import escalera.modelo.Jugador;
import escalera.modelo.Pregunta;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.Timer;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;

/**
 * Contenido de la ventana: tablero, controles, bitácora, estructuras y asistente IA.
 * Toda la lógica vive en {@link Juego}; este panel solo la muestra y la dispara.
 */
public class PanelJuego extends JPanel {

    private static final int MILISEGUNDOS_ENTRE_TURNOS = 700;
    private static final int TOPE_DE_TURNOS = 500;
    private static final Font FUENTE_MONO = new Font(Font.MONOSPACED, Font.PLAIN, 13);
    private static final Color COLOR_PANEL = new Color(0xFAFAF9);

    private final PanelTablero panelTablero = new PanelTablero();
    private final JTextArea areaBitacora = crearArea();
    private final JTextArea areaAsistente = crearArea();
    private final JTextArea areaEstructuras = crearArea();
    private final JTextArea areaRanking = crearArea();
    private final JLabel etiquetaModo = new JLabel();
    private final JLabel etiquetaTurno = new JLabel();
    private final JLabel etiquetaDado = new JLabel("–", SwingConstants.CENTER);
    private final JPanel panelMarcador = new JPanel(new GridLayout(0, 1, 0, 4));
    private final JButton botonTirar = new JButton("Tirar dado (jugar turno)");
    private final JButton botonAutomatico = new JButton("Jugar hasta que haya ganador");

    private final AsistenteIA asistente = new AsistenteIA();
    private final JugadorIA jugadorIA = new JugadorIA();
    private final Bitacora bitacora = this::agregarABitacora;
    private final Timer temporizador = new Timer(MILISEGUNDOS_ENTRE_TURNOS, evento -> turnoAutomatico());

    private Juego juego;
    private Pregunta[] bancoDePreguntas = ConfiguracionTablero.preguntasPorDefecto();
    private boolean turnoEnCurso;
    private int turnosAutomaticos;

    public PanelJuego() {
        super(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        add(crearEncabezado(), BorderLayout.NORTH);
        add(panelTablero, BorderLayout.CENTER);
        add(crearPanelDeControles(), BorderLayout.EAST);
        add(crearPestanas(), BorderLayout.SOUTH);
        iniciarCorridaDeVerificacion();
    }

    // ───────────────────────────── construcción de la vista ─────────────────────────────

    private JComponent crearEncabezado() {
        JPanel encabezado = new JPanel(new BorderLayout());
        JLabel titulo = new JLabel("Escalera, preguntas y retos");
        titulo.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 22));
        etiquetaModo.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 14));
        etiquetaModo.setForeground(new Color(0x57534E));
        encabezado.add(titulo, BorderLayout.WEST);
        encabezado.add(etiquetaModo, BorderLayout.EAST);
        return encabezado;
    }

    private JComponent crearPanelDeControles() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(COLOR_PANEL);
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(0xD6D3D1)),
                BorderFactory.createEmptyBorder(10, 10, 10, 10)));
        panel.setPreferredSize(new Dimension(300, 420));

        etiquetaTurno.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 15));
        etiquetaTurno.setPreferredSize(new Dimension(270, 24));
        etiquetaTurno.setMaximumSize(new Dimension(Integer.MAX_VALUE, 24));
        etiquetaDado.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 46));
        etiquetaDado.setBorder(BorderFactory.createLineBorder(new Color(0x44403C), 2, true));
        etiquetaDado.setPreferredSize(new Dimension(80, 72));
        etiquetaDado.setMaximumSize(new Dimension(80, 72));
        panelMarcador.setOpaque(false);

        botonTirar.addActionListener(evento -> jugarUnTurno());
        botonAutomatico.addActionListener(evento -> alternarModoAutomatico());

        agregar(panel, etiquetaTurno);
        panel.add(Box.createVerticalStrut(8));
        agregar(panel, etiquetaDado);
        panel.add(Box.createVerticalStrut(8));
        agregar(panel, panelMarcador);
        panel.add(Box.createVerticalStrut(10));
        agregar(panel, botonTirar);
        agregar(panel, botonAutomatico);
        panel.add(Box.createVerticalStrut(10));
        agregar(panel, crearBoton("Corrida de verificación (dados fijos)", this::iniciarCorridaDeVerificacion));
        agregar(panel, crearBoton("Nueva partida aleatoria…", this::iniciarPartidaAleatoria));
        agregar(panel, crearBoton("Cargar preguntas desde .txt…", this::cargarPreguntas));
        panel.add(Box.createVerticalStrut(10));
        agregar(panel, crearBoton("IA: analizar la próxima tirada", this::analizarRiesgo));
        panel.add(Box.createVerticalGlue());
        return panel;
    }

    private void agregar(JPanel panel, JComponent componente) {
        componente.setAlignmentX(Component.LEFT_ALIGNMENT);
        if (componente instanceof JButton) {
            componente.setMaximumSize(new Dimension(Integer.MAX_VALUE, 32));
        }
        panel.add(componente);
        panel.add(Box.createVerticalStrut(4));
    }

    private JButton crearBoton(String texto, Runnable accion) {
        JButton boton = new JButton(texto);
        boton.addActionListener(evento -> accion.run());
        return boton;
    }

    private JComponent crearPestanas() {
        JTabbedPane pestanas = new JTabbedPane();
        pestanas.addTab("Bitácora", new JScrollPane(areaBitacora));
        pestanas.addTab("Asistente IA", new JScrollPane(areaAsistente));
        pestanas.addTab("Estructuras", new JScrollPane(areaEstructuras));
        pestanas.addTab("Ranking", new JScrollPane(areaRanking));
        pestanas.setPreferredSize(new Dimension(1000, 250));
        return pestanas;
    }

    private static JTextArea crearArea() {
        JTextArea area = new JTextArea();
        area.setEditable(false);
        area.setFont(FUENTE_MONO);
        area.setMargin(new java.awt.Insets(6, 8, 6, 8));
        return area;
    }

    // ───────────────────────────── modos de juego ─────────────────────────────

    /** Partida fija del enunciado: Ana, Luis y Marta con dados 3, 4, 8, 1, 6. */
    public final void iniciarCorridaDeVerificacion() {
        detenerAutomatico();
        areaBitacora.setText("");
        juego = MainConsola.crearJuegoDeVerificacion(bitacora);
        etiquetaModo.setText("Modo: corrida de verificación (dados 3, 4, 8, 1, 6 · Ana responde 5)");
        agregarABitacora("Corrida de verificación lista. Pulsa \"Tirar dado\" cinco veces.");
        alCambiarDeJuego();
    }

    private void iniciarPartidaAleatoria() {
        Jugador[] jugadores = DialogoJugadores.pedir(this);
        if (jugadores == null) {
            return;
        }
        detenerAutomatico();
        areaBitacora.setText("");
        juego = new Juego(jugadores, bancoDePreguntas, new DadoAleatorio(), this::responderReto, bitacora);
        etiquetaModo.setText("Modo: partida aleatoria con " + jugadores.length + " jugadores");
        agregarABitacora("Partida aleatoria iniciada.");
        alCambiarDeJuego();
    }

    private void alCambiarDeJuego() {
        etiquetaDado.setText("–");
        panelTablero.setJuego(juego);
        areaAsistente.setText("El asistente explicará cada jugada aquí.\n\n" + juego.getRegla().dibujar());
        areaRanking.setText("");
        refrescar();
    }

    private void cargarPreguntas() {
        JFileChooser selector = new JFileChooser(".");
        if (selector.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            bancoDePreguntas = CargadorPreguntas.cargar(selector.getSelectedFile().toPath(), bitacora);
            JOptionPane.showMessageDialog(this, "Se usarán " + bancoDePreguntas.length
                    + " preguntas en la próxima partida aleatoria.");
        }
    }

    // ───────────────────────────── turnos ─────────────────────────────

    private void jugarUnTurno() {
        if (turnoEnCurso) {
            return;
        }
        turnoEnCurso = true;
        try {
            boolean seJugo = juego.jugarTurno();
            if (seJugo) {
                mostrarUltimaJugada();
            }
            refrescar();
            if (juego.hayGanador()) {
                anunciarGanador();
            }
        } finally {
            turnoEnCurso = false;
        }
    }

    private void alternarModoAutomatico() {
        if (temporizador.isRunning()) {
            detenerAutomatico();
        } else {
            turnosAutomaticos = 0;
            botonAutomatico.setText("Detener juego automático");
            temporizador.start();
        }
    }

    private void turnoAutomatico() {
        if (turnoEnCurso) {
            return;
        }
        boolean puedeSeguir = !juego.hayGanador() && juego.getDado().quedanTiradas()
                && !juego.getCola().estaVacia() && turnosAutomaticos < TOPE_DE_TURNOS;
        if (!puedeSeguir) {
            detenerAutomatico();
            return;
        }
        turnosAutomaticos++;
        jugarUnTurno();
    }

    private void detenerAutomatico() {
        temporizador.stop();
        botonAutomatico.setText("Jugar hasta que haya ganador");
    }

    /** Quién contesta un reto en la partida aleatoria: la IA o la persona por diálogo. */
    private String responderReto(Jugador jugador, Pregunta pregunta) {
        if (jugador.esControladoPorIA()) {
            String respuesta = jugadorIA.responder(jugador, pregunta);
            agregarABitacora("   [IA] " + jugador.getNombre() + " razona \"" + pregunta.getEnunciado()
                    + "\" y responde: " + respuesta);
            return respuesta;
        }
        return preguntarAPersona(jugador, pregunta);
    }

    private String preguntarAPersona(Jugador jugador, Pregunta pregunta) {
        JTextField campo = new JTextField(12);
        JLabel pista = new JLabel(" ");
        pista.setForeground(new Color(0x1D4ED8));
        JButton botonPista = new JButton("Pedir pista a la IA");
        botonPista.addActionListener(evento -> pista.setText("<html><i>Pista: "
                + asistente.pista(pregunta.getEnunciado()) + "</i></html>"));

        JPanel panel = new JPanel(new GridLayout(0, 1, 4, 4));
        panel.add(new JLabel(jugador.getNombre() + ", reto de dificultad " + pregunta.getDificultad()
                + " (pregunta " + pregunta.getClave() + "):"));
        panel.add(new JLabel("<html><b>" + pregunta.getEnunciado() + "</b></html>"));
        panel.add(campo);
        panel.add(botonPista);
        panel.add(pista);

        int opcion = JOptionPane.showConfirmDialog(this, panel, "Reto en la casilla",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE);
        return opcion == JOptionPane.OK_OPTION ? campo.getText() : "";
    }

    private void mostrarUltimaJugada() {
        ResultadoLlegada ultima = juego.getUltimaLlegada();
        if (ultima != null) {
            etiquetaDado.setText(String.valueOf(ultima.getDado()));
            areaAsistente.setText(asistente.explicar(ultima, juego));
            areaAsistente.setCaretPosition(0);
        }
    }

    private void anunciarGanador() {
        detenerAutomatico();
        areaRanking.setText("Ranking (segundo árbol, ordenado por aciertos):\n\n" + juego.ranking());
        JOptionPane.showMessageDialog(this, "¡" + juego.getGanador() + " llegó exactamente a la casilla "
                + ConfiguracionTablero.META + " y gana!", "Fin de la partida", JOptionPane.INFORMATION_MESSAGE);
    }

    private void analizarRiesgo() {
        String frente = juego.getCola().verFrente();
        Jugador jugador = frente == null ? null : juego.getTabla().buscar(frente);
        if (jugador == null) {
            JOptionPane.showMessageDialog(this, "No hay un jugador registrado al frente de la cola.");
            return;
        }
        areaAsistente.setText(asistente.analizarRiesgo(jugador, juego.getGrafo()));
        areaAsistente.setCaretPosition(0);
    }

    // ───────────────────────────── refresco de la vista ─────────────────────────────

    private void refrescar() {
        actualizarTurno();
        actualizarMarcador();
        areaEstructuras.setText(describirEstructuras());
        areaEstructuras.setCaretPosition(0);
        panelTablero.repaint();
        boolean puedeTirar = !juego.hayGanador() && juego.getDado().quedanTiradas() && !juego.getCola().estaVacia();
        botonTirar.setEnabled(puedeTirar);
        botonAutomatico.setEnabled(puedeTirar || temporizador.isRunning());
    }

    private void actualizarTurno() {
        if (juego.hayGanador()) {
            etiquetaTurno.setText("Ganó " + juego.getGanador());
        } else if (!juego.getDado().quedanTiradas()) {
            etiquetaTurno.setText("Dados fijos agotados");
        } else {
            String frente = juego.getCola().verFrente();
            Jugador jugador = juego.getTabla().buscar(frente);
            boolean castigado = jugador != null && jugador.tieneCastigo();
            etiquetaTurno.setText("Turno de: " + frente + (castigado ? " (castigado)" : ""));
        }
    }

    private void actualizarMarcador() {
        panelMarcador.removeAll();
        Jugador[] jugadores = juego.jugadoresEnOrden();
        for (int i = 0; i < jugadores.length; i++) {
            Jugador jugador = jugadores[i];
            JLabel fila = new JLabel(String.format("<html><b>%s</b>%s · casilla %d · ✔ %d · ✘ %d%s</html>",
                    jugador.getNombre(), jugador.esControladoPorIA() ? " (IA)" : "", jugador.getPosicion(),
                    jugador.getAciertos(), jugador.getFallos(), jugador.tieneCastigo() ? " · pierde turno" : ""));
            fila.setForeground(PanelTablero.COLORES_JUGADORES[i % PanelTablero.COLORES_JUGADORES.length]);
            panelMarcador.add(fila);
        }
        panelMarcador.revalidate();
        panelMarcador.repaint();
    }

    private String describirEstructuras() {
        StringBuilder texto = new StringBuilder();
        texto.append("LISTA DOBLE (ida):    ").append(juego.getTablero().recorrerIda()).append('\n');
        texto.append("LISTA DOBLE (regreso): ").append(juego.getTablero().recorrerRegreso()).append('\n');
        texto.append("Siguiente de 3 = ").append(juego.getTablero().numeroSiguiente(3))
             .append(" (enlace doble) · salto de 3 = ").append(juego.getGrafo().destinoDe(3))
             .append(" (arista del grafo)\n\n");

        texto.append("COLA DE TURNOS (frente → final): ").append(juego.getCola().describir()).append("\n\n");
        texto.append("TABLA HASH (").append(juego.getTabla().getNumeroCubetas()).append(" cubetas):\n")
             .append(juego.getTabla().describir()).append('\n');

        texto.append("PILA – últimas ").append(ConfiguracionTablero.JUGADAS_A_CONSULTAR)
             .append(" jugadas (la primera es la cima):\n");
        Jugada[] ultimas = juego.getHistorial().ultimas(ConfiguracionTablero.JUGADAS_A_CONSULTAR);
        if (ultimas.length == 0) {
            texto.append("  (vacía)\n");
        }
        for (Jugada jugada : ultimas) {
            texto.append("  ").append(jugada).append('\n');
        }

        texto.append("\nÁRBOL DE PREGUNTAS – inorden: ").append(juego.getPreguntas().inordenComoTexto()).append('\n');
        texto.append(juego.getPreguntas().dibujar());
        texto.append(String.format("peso %d · altura %d · hojas %d · completo: %s · buscar 101: %s%n%n",
                juego.getPreguntas().peso(), juego.getPreguntas().altura(), juego.getPreguntas().hojas(),
                juego.getPreguntas().esCompleto() ? "sí" : "no", juego.getPreguntas().buscar(101)));

        texto.append("ÁRBOL DE DECISIÓN:\n").append(juego.getRegla().dibujar()).append('\n');

        texto.append("GRAFO DE SALTOS:\n").append(juego.getGrafo().describir());
        ValidadorCiclos validador = new ValidadorCiclos(ConfiguracionTablero.META);
        texto.append("Kruskal + Union-Find (sin dirección):\n");
        for (ResultadoValidacion resultado : validador.validarConKruskal(MainConsola.aristasRealesYDePrueba(juego.getGrafo()))) {
            texto.append("  ").append(resultado).append('\n');
        }
        texto.append("Prim (peso 1):\n");
        for (ResultadoValidacion resultado : validador.validarConPrim(MainConsola.aristasRealesYDePrueba(juego.getGrafo()))) {
            texto.append("  ").append(resultado).append('\n');
        }
        return texto.toString();
    }

    private void agregarABitacora(String mensaje) {
        areaBitacora.append(mensaje + "\n");
        areaBitacora.setCaretPosition(areaBitacora.getDocument().getLength());
    }
}

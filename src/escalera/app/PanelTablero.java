package escalera.app;

import escalera.estructuras.NodoCasilla;
import escalera.juego.ConfiguracionTablero;
import escalera.juego.Juego;
import escalera.juego.ResultadoLlegada;
import escalera.modelo.Arista;
import escalera.modelo.Jugador;
import escalera.modelo.TipoCasilla;

import javax.swing.JPanel;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.geom.Path2D;

/**
 * Dibuja el tablero de 10 x 5 recorriendo la lista doble.
 * Las escaleras y serpientes se dibujan a partir de las aristas del grafo.
 */
public class PanelTablero extends JPanel {

    static final Color[] COLORES_JUGADORES = {
        new Color(0x2563EB), new Color(0xEA580C), new Color(0x9333EA), new Color(0x0D9488)
    };
    private static final Color FONDO = new Color(0xF8F5EE);
    private static final Color CASILLA_CLARA = new Color(0xFFFDF8);
    private static final Color CASILLA_OSCURA = new Color(0xEFE9DC);
    private static final Color COLOR_RETO = new Color(0xFDE68A);
    private static final Color COLOR_ESCALERA = new Color(0xBBF7D0);
    private static final Color COLOR_SERPIENTE = new Color(0xFECACA);
    private static final Color COLOR_META = new Color(0xFCD34D);
    private static final Color TRAZO_ESCALERA = new Color(21, 128, 61, 185);
    private static final Color TRAZO_SERPIENTE = new Color(220, 38, 38, 200);
    private static final Color BORDE = new Color(0x57534E);
    private static final int MARGEN = 16;
    private static final int ALTO_ZONA_SALIDA = 34;

    private Juego juego;

    public PanelTablero() {
        setBackground(FONDO);
        setPreferredSize(new Dimension(720, 420));
    }

    public void setJuego(Juego juego) {
        this.juego = juego;
        repaint();
    }

    @Override
    protected void paintComponent(Graphics grafico) {
        super.paintComponent(grafico);
        if (juego == null) {
            return;
        }
        Graphics2D g = (Graphics2D) grafico.create();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        dibujarCasillas(g);
        dibujarSaltos(g);
        dibujarFichas(g);
        dibujarZonaDeSalida(g);
        g.dispose();
    }

    private int anchoCelda() {
        return (getWidth() - 2 * MARGEN) / ConfiguracionTablero.COLUMNAS_TABLERO;
    }

    private int altoCelda() {
        return (getHeight() - 2 * MARGEN - ALTO_ZONA_SALIDA) / filas();
    }

    private int filas() {
        int total = juego.getTablero().getTamano();
        return (total + ConfiguracionTablero.COLUMNAS_TABLERO - 1) / ConfiguracionTablero.COLUMNAS_TABLERO;
    }

    /** Esquina superior izquierda de una casilla, en zigzag (fila de abajo de 1 a 10). */
    private Point esquinaDe(int numero) {
        int columnas = ConfiguracionTablero.COLUMNAS_TABLERO;
        int filaDesdeAbajo = (numero - 1) / columnas;
        int desplazamiento = (numero - 1) % columnas;
        int columna = filaDesdeAbajo % 2 == 0 ? desplazamiento : columnas - 1 - desplazamiento;
        int fila = filas() - 1 - filaDesdeAbajo;
        return new Point(MARGEN + columna * anchoCelda(), MARGEN + fila * altoCelda());
    }

    private Point centroDe(int numero) {
        Point esquina = esquinaDe(numero);
        return new Point(esquina.x + anchoCelda() / 2, esquina.y + altoCelda() / 2);
    }

    private void dibujarCasillas(Graphics2D g) {
        ResultadoLlegada ultima = juego.getUltimaLlegada();
        int ancho = anchoCelda();
        int alto = altoCelda();
        for (NodoCasilla nodo = juego.getTablero().getCabeza(); nodo != null; nodo = nodo.getSiguiente()) {
            int numero = nodo.getCasilla().getNumero();
            Point esquina = esquinaDe(numero);
            g.setColor(colorDe(nodo.getCasilla().getTipo(), numero));
            g.fillRect(esquina.x, esquina.y, ancho, alto);
            g.setColor(BORDE);
            g.setStroke(new BasicStroke(1f));
            g.drawRect(esquina.x, esquina.y, ancho, alto);

            if (ultima != null && ultima.getPosicionFinal() == numero) {
                g.setColor(new Color(0x1D4ED8));
                g.setStroke(new BasicStroke(3f));
                g.drawRect(esquina.x + 2, esquina.y + 2, ancho - 4, alto - 4);
            }
            g.setColor(new Color(0x292524));
            g.setFont(new Font(Font.SANS_SERIF, Font.BOLD, Math.max(11, alto / 5)));
            g.drawString(String.valueOf(numero), esquina.x + 5, esquina.y + g.getFontMetrics().getAscent() + 2);
            dibujarEtiquetaDeTipo(g, nodo.getCasilla().getTipo(), esquina, ancho, alto);
        }
    }

    private void dibujarEtiquetaDeTipo(Graphics2D g, TipoCasilla tipo, Point esquina, int ancho, int alto) {
        String etiqueta;
        switch (tipo) {
            case RETO:
                etiqueta = "RETO";
                break;
            case META:
                etiqueta = "META";
                break;
            default:
                return;
        }
        g.setFont(new Font(Font.SANS_SERIF, Font.BOLD, Math.max(9, alto / 7)));
        FontMetrics medidas = g.getFontMetrics();
        g.setColor(new Color(0x78350F));
        g.drawString(etiqueta, esquina.x + ancho - medidas.stringWidth(etiqueta) - 5, esquina.y + alto - 6);
    }

    private Color colorDe(TipoCasilla tipo, int numero) {
        switch (tipo) {
            case RETO:
                return COLOR_RETO;
            case ESCALERA:
                return COLOR_ESCALERA;
            case SERPIENTE:
                return COLOR_SERPIENTE;
            case META:
                return COLOR_META;
            default:
                return numero % 2 == 0 ? CASILLA_OSCURA : CASILLA_CLARA;
        }
    }

    /** Escaleras como rieles verdes y serpientes como curvas rojas: salen del grafo, no de la lista. */
    private void dibujarSaltos(Graphics2D g) {
        for (Arista arista : juego.getGrafo().aristas()) {
            Point desde = centroDe(arista.getOrigen());
            Point hasta = centroDe(arista.getDestino());
            if (arista.esEscalera()) {
                dibujarEscalera(g, desde, hasta);
            } else {
                dibujarSerpiente(g, desde, hasta);
            }
        }
    }

    private void dibujarEscalera(Graphics2D g, Point desde, Point hasta) {
        double dx = hasta.x - desde.x;
        double dy = hasta.y - desde.y;
        double largo = Math.max(1, Math.hypot(dx, dy));
        double nx = -dy / largo * 7;
        double ny = dx / largo * 7;
        g.setColor(TRAZO_ESCALERA);
        g.setStroke(new BasicStroke(3f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        g.drawLine((int) (desde.x + nx), (int) (desde.y + ny), (int) (hasta.x + nx), (int) (hasta.y + ny));
        g.drawLine((int) (desde.x - nx), (int) (desde.y - ny), (int) (hasta.x - nx), (int) (hasta.y - ny));
        int peldanos = (int) (largo / 14);
        g.setStroke(new BasicStroke(2f));
        for (int i = 1; i < peldanos; i++) {
            double t = (double) i / peldanos;
            double x = desde.x + dx * t;
            double y = desde.y + dy * t;
            g.drawLine((int) (x + nx), (int) (y + ny), (int) (x - nx), (int) (y - ny));
        }
    }

    private void dibujarSerpiente(Graphics2D g, Point desde, Point hasta) {
        Path2D cuerpo = new Path2D.Double();
        cuerpo.moveTo(desde.x, desde.y);
        double dx = hasta.x - desde.x;
        double dy = hasta.y - desde.y;
        cuerpo.curveTo(desde.x + dx * 0.25 + 40, desde.y + dy * 0.25,
                       desde.x + dx * 0.75 - 40, desde.y + dy * 0.75,
                       hasta.x, hasta.y);
        g.setColor(TRAZO_SERPIENTE);
        g.setStroke(new BasicStroke(6f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        g.draw(cuerpo);
        g.fillOval(desde.x - 7, desde.y - 7, 14, 14);
        g.setColor(Color.WHITE);
        g.fillOval(desde.x - 4, desde.y - 4, 3, 3);
        g.fillOval(desde.x + 1, desde.y - 4, 3, 3);
    }

    private void dibujarFichas(Graphics2D g) {
        Jugador[] jugadores = juego.jugadoresEnOrden();
        int diametro = Math.max(16, Math.min(anchoCelda(), altoCelda()) / 3);
        for (int i = 0; i < jugadores.length; i++) {
            Jugador jugador = jugadores[i];
            if (jugador == null || jugador.getPosicion() == 0) {
                continue;
            }
            Point centro = centroDe(jugador.getPosicion());
            int desplazamientoX = (i % 2 == 0 ? -1 : 1) * diametro / 2;
            int desplazamientoY = (i < 2 ? -1 : 1) * diametro / 3 + diametro / 4;
            dibujarFicha(g, jugador, i, centro.x + desplazamientoX, centro.y + desplazamientoY, diametro);
        }
    }

    private void dibujarFicha(Graphics2D g, Jugador jugador, int indice, int cx, int cy, int diametro) {
        g.setColor(new Color(0, 0, 0, 60));
        g.fillOval(cx - diametro / 2 + 2, cy - diametro / 2 + 2, diametro, diametro);
        g.setColor(COLORES_JUGADORES[indice % COLORES_JUGADORES.length]);
        g.fillOval(cx - diametro / 2, cy - diametro / 2, diametro, diametro);
        g.setColor(Color.WHITE);
        g.setStroke(new BasicStroke(2f));
        g.drawOval(cx - diametro / 2, cy - diametro / 2, diametro, diametro);
        g.setFont(new Font(Font.SANS_SERIF, Font.BOLD, diametro / 2 + 2));
        FontMetrics medidas = g.getFontMetrics();
        String inicial = String.valueOf(jugador.getInicial());
        g.drawString(inicial, cx - medidas.stringWidth(inicial) / 2, cy + medidas.getAscent() / 2 - 2);
    }

    /** Jugadores con posición 0: todavía fuera del tablero. */
    private void dibujarZonaDeSalida(Graphics2D g) {
        int y = MARGEN + filas() * altoCelda() + 8;
        g.setColor(new Color(0x44403C));
        g.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 13));
        g.drawString("Fuera del tablero (posición 0):", MARGEN, y + 15);
        int x = MARGEN + 210;
        Jugador[] jugadores = juego.jugadoresEnOrden();
        for (int i = 0; i < jugadores.length; i++) {
            if (jugadores[i] != null && jugadores[i].getPosicion() == 0) {
                dibujarFicha(g, jugadores[i], i, x, y + 10, 20);
                x += 28;
            }
        }
    }
}

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
import java.awt.geom.Point2D;
import java.awt.geom.Ellipse2D;

/**
 * Dibuja el tablero (5 columnas x 10 filas, en zigzag) recorriendo la lista doble.
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
    private static final Color BORDE = new Color(0x57534E);
    /**
     * El tablero se dibuja con 5 columnas y 10 filas. Así toda escalera del enunciado
     * (3→11, 8→15, 14→19) queda en una fila más alta y toda serpiente (17→4, 18→6) en una más baja,
     * como en el juego de mesa. Con 10 columnas, 14 y 19 quedarían en la misma fila.
     */
    private static final int COLUMNAS_DIBUJO = 5;
    private static final int MUESTRAS_SERPIENTE = 90;
    private static final Color MADERA = new Color(0xB45309);
    private static final Color MADERA_OSCURA = new Color(0x78350F);
    private static final Color MADERA_CLARA = new Color(0xF59E0B);
    private static final Color[][] PIELES_SERPIENTE = {
        {new Color(0xDC2626), new Color(0x7F1D1D), new Color(0xFDE047)},
        {new Color(0x16A34A), new Color(0x14532D), new Color(0xFACC15)},
        {new Color(0x7C3AED), new Color(0x3B0764), new Color(0xFDE68A)}
    };
    private static final int MARGEN = 16;
    private static final int ALTO_ZONA_SALIDA = 34;

    private Juego juego;
    /** Casilla que se está pintando; la usa la etiqueta de salto. */
    private int numeroEnDibujo;

    public PanelTablero() {
        setBackground(FONDO);
        setPreferredSize(new Dimension(430, 820));
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

        dibujarCasillas(g, true);
        dibujarSaltos(g);
        dibujarCasillas(g, false);
        dibujarFichas(g);
        dibujarZonaDeSalida(g);
        g.dispose();
    }

    /** Lado de cada casilla: cuadrada, lo más grande que quepa en el panel. */
    private int ladoCelda() {
        int porAncho = (getWidth() - 2 * MARGEN) / COLUMNAS_DIBUJO;
        int porAlto = (getHeight() - 2 * MARGEN - ALTO_ZONA_SALIDA) / filas();
        return Math.max(10, Math.min(porAncho, porAlto));
    }

    private int anchoCelda() {
        return ladoCelda();
    }

    private int altoCelda() {
        return ladoCelda();
    }

    /** Margen izquierdo para que el tablero quede centrado en el panel. */
    private int origenX() {
        return Math.max(MARGEN, (getWidth() - COLUMNAS_DIBUJO * ladoCelda()) / 2);
    }

    private int filas() {
        int total = juego.getTablero().getTamano();
        return (total + COLUMNAS_DIBUJO - 1) / COLUMNAS_DIBUJO;
    }

    /** Esquina superior izquierda de una casilla, en zigzag (fila de abajo: 1 a 5 de izquierda a derecha). */
    private Point esquinaDe(int numero) {
        int filaDesdeAbajo = (numero - 1) / COLUMNAS_DIBUJO;
        int desplazamiento = (numero - 1) % COLUMNAS_DIBUJO;
        int columna = filaDesdeAbajo % 2 == 0 ? desplazamiento : COLUMNAS_DIBUJO - 1 - desplazamiento;
        int fila = filas() - 1 - filaDesdeAbajo;
        return new Point(origenX() + columna * ladoCelda(), MARGEN + fila * ladoCelda());
    }

    private Point centroDe(int numero) {
        Point esquina = esquinaDe(numero);
        return new Point(esquina.x + anchoCelda() / 2, esquina.y + altoCelda() / 2);
    }

    /**
     * Pinta las casillas recorriendo la lista doble. Se llama dos veces: primero los fondos
     * (debajo de escaleras y serpientes) y luego números y etiquetas (encima, para que se lean).
     */
    private void dibujarCasillas(Graphics2D g, boolean soloFondos) {
        ResultadoLlegada ultima = juego.getUltimaLlegada();
        int ancho = anchoCelda();
        int alto = altoCelda();
        for (NodoCasilla nodo = juego.getTablero().getCabeza(); nodo != null; nodo = nodo.getSiguiente()) {
            int numero = nodo.getCasilla().getNumero();
            Point esquina = esquinaDe(numero);
            if (soloFondos) {
                g.setColor(colorDe(nodo.getCasilla().getTipo(), numero));
                g.fillRect(esquina.x, esquina.y, ancho, alto);
                g.setColor(BORDE);
                g.setStroke(new BasicStroke(1f));
                g.drawRect(esquina.x, esquina.y, ancho, alto);
                continue;
            }

            if (ultima != null && ultima.getPosicionFinal() == numero) {
                g.setColor(new Color(0x1D4ED8));
                g.setStroke(new BasicStroke(3f));
                g.drawRect(esquina.x + 2, esquina.y + 2, ancho - 4, alto - 4);
            }
            g.setFont(new Font(Font.SANS_SERIF, Font.BOLD, Math.max(11, alto / 5)));
            dibujarTextoConHalo(g, String.valueOf(numero), esquina.x + 5,
                                esquina.y + g.getFontMetrics().getAscent() + 2, new Color(0x292524));
            numeroEnDibujo = numero;
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
            case ESCALERA:
            case SERPIENTE:
                dibujarEtiquetaDeSalto(g, tipo, numeroEnDibujo, esquina, ancho, alto);
                return;
            default:
                return;
        }
        g.setFont(new Font(Font.SANS_SERIF, Font.BOLD, Math.max(9, alto / 7)));
        FontMetrics medidas = g.getFontMetrics();
        dibujarTextoConHalo(g, etiqueta, esquina.x + ancho - medidas.stringWidth(etiqueta) - 5,
                            esquina.y + alto - 6, new Color(0x78350F));
    }

    /** Texto con un borde claro alrededor, para que se lea aunque pase una escalera por debajo. */
    private static void dibujarTextoConHalo(Graphics2D g, String texto, int x, int y, Color color) {
        g.setColor(new Color(255, 253, 248, 230));
        for (int dx = -2; dx <= 2; dx++) {
            for (int dy = -2; dy <= 2; dy++) {
                if (dx != 0 || dy != 0) {
                    g.drawString(texto, x + dx, y + dy);
                }
            }
        }
        g.setColor(color);
        g.drawString(texto, x, y);
    }

    /** Texto "SUBE A 11" o "BAJA A 4" en la casilla de origen, leído del grafo. */
    private void dibujarEtiquetaDeSalto(Graphics2D g, TipoCasilla tipo, int numero, Point esquina, int ancho, int alto) {
        String verbo = tipo == TipoCasilla.ESCALERA ? "SUBE A " : "BAJA A ";
        String etiqueta = verbo + juego.getGrafo().destinoDe(numero);
        g.setFont(new Font(Font.SANS_SERIF, Font.BOLD, Math.max(9, alto / 7)));
        FontMetrics medidas = g.getFontMetrics();
        Color color = tipo == TipoCasilla.ESCALERA ? new Color(0x14532D) : new Color(0x7F1D1D);
        dibujarTextoConHalo(g, etiqueta, esquina.x + ancho - medidas.stringWidth(etiqueta) - 5,
                            esquina.y + alto - 6, color);
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

    /**
     * Escaleras y serpientes: salen del grafo, no de la lista doble.
     * Primero las serpientes y encima las escaleras, para que los peldaños se vean completos.
     */
    private void dibujarSaltos(Graphics2D g) {
        Arista[] aristas = juego.getGrafo().aristas();
        int serpientes = 0;
        for (Arista arista : aristas) {
            if (!arista.esEscalera()) {
                Color[] piel = PIELES_SERPIENTE[serpientes % PIELES_SERPIENTE.length];
                dibujarSerpiente(g, centroDe(arista.getOrigen()), centroDe(arista.getDestino()), piel);
                serpientes++;
            }
        }
        for (Arista arista : aristas) {
            if (arista.esEscalera()) {
                dibujarEscalera(g, centroDe(arista.getOrigen()), centroDe(arista.getDestino()));
            }
        }
    }

    /** Escalera de madera recta: dos largueros y peldaños, con sombra. */
    private void dibujarEscalera(Graphics2D g, Point desde, Point hasta) {
        double dx = hasta.x - desde.x;
        double dy = hasta.y - desde.y;
        double largo = Math.max(1, Math.hypot(dx, dy));
        double ux = dx / largo;
        double uy = dy / largo;
        double extension = ladoCelda() * 0.22;
        double x0 = desde.x - ux * extension;
        double y0 = desde.y - uy * extension;
        double x1 = hasta.x + ux * extension;
        double y1 = hasta.y + uy * extension;
        double medioAncho = ladoCelda() * 0.14;
        double nx = -uy * medioAncho;
        double ny = ux * medioAncho;
        float grosorLarguero = Math.max(3f, ladoCelda() * 0.06f);

        Path2D largueros = new Path2D.Double();
        largueros.moveTo(x0 + nx, y0 + ny);
        largueros.lineTo(x1 + nx, y1 + ny);
        largueros.moveTo(x0 - nx, y0 - ny);
        largueros.lineTo(x1 - nx, y1 - ny);

        Path2D peldanos = new Path2D.Double();
        double largoTotal = largo + 2 * extension;
        int cantidad = Math.max(2, (int) (largoTotal / (ladoCelda() * 0.28)));
        for (int i = 1; i < cantidad; i++) {
            double t = (double) i / cantidad;
            double px = x0 + (x1 - x0) * t;
            double py = y0 + (y1 - y0) * t;
            peldanos.moveTo(px + nx, py + ny);
            peldanos.lineTo(px - nx, py - ny);
        }

        BasicStroke trazoPeldano = new BasicStroke(grosorLarguero * 0.7f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND);
        BasicStroke trazoLarguero = new BasicStroke(grosorLarguero, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND);
        BasicStroke contornoLarguero = new BasicStroke(grosorLarguero + 2.5f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND);
        BasicStroke contornoPeldano = new BasicStroke(grosorLarguero * 0.7f + 2.5f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND);

        Graphics2D sombra = (Graphics2D) g.create();
        sombra.translate(4, 5);
        sombra.setColor(new Color(0, 0, 0, 55));
        sombra.setStroke(contornoPeldano);
        sombra.draw(peldanos);
        sombra.setStroke(contornoLarguero);
        sombra.draw(largueros);
        sombra.dispose();

        g.setColor(MADERA_OSCURA);
        g.setStroke(contornoPeldano);
        g.draw(peldanos);
        g.setColor(MADERA_CLARA);
        g.setStroke(trazoPeldano);
        g.draw(peldanos);
        g.setColor(MADERA_OSCURA);
        g.setStroke(contornoLarguero);
        g.draw(largueros);
        g.setColor(MADERA);
        g.setStroke(trazoLarguero);
        g.draw(largueros);
    }

    /**
     * Serpiente: cuerpo ondulado que se adelgaza de la cabeza (casilla donde se cae)
     * a la cola (casilla a la que se baja), con manchas, ojos y lengua.
     */
    private void dibujarSerpiente(Graphics2D g, Point cabeza, Point cola, Color[] piel) {
        double dx = cola.x - cabeza.x;
        double dy = cola.y - cabeza.y;
        double largo = Math.max(1, Math.hypot(dx, dy));
        double ux = dx / largo;
        double uy = dy / largo;
        double amplitud = ladoCelda() * 0.32;
        double ondas = Math.max(1.5, largo / (ladoCelda() * 1.3));
        double grosorCabeza = ladoCelda() * 0.26;

        double[] xs = new double[MUESTRAS_SERPIENTE + 1];
        double[] ys = new double[MUESTRAS_SERPIENTE + 1];
        for (int i = 0; i <= MUESTRAS_SERPIENTE; i++) {
            double t = (double) i / MUESTRAS_SERPIENTE;
            double atenuacion = Math.sin(Math.PI * Math.min(1, 0.15 + t * 0.95));
            double onda = Math.sin(t * ondas * 2 * Math.PI) * amplitud * atenuacion;
            xs[i] = cabeza.x + dx * t - uy * onda;
            ys[i] = cabeza.y + dy * t + ux * onda;
        }

        Path2D cuerpo = new Path2D.Double();
        double[] bordeX = new double[MUESTRAS_SERPIENTE + 1];
        double[] bordeY = new double[MUESTRAS_SERPIENTE + 1];
        for (int i = 0; i <= MUESTRAS_SERPIENTE; i++) {
            double t = (double) i / MUESTRAS_SERPIENTE;
            int a = Math.max(0, i - 1);
            int b = Math.min(MUESTRAS_SERPIENTE, i + 1);
            double tx = xs[b] - xs[a];
            double ty = ys[b] - ys[a];
            double norma = Math.max(1e-6, Math.hypot(tx, ty));
            double medio = grosorCabeza * (1 - 0.85 * t) / 2;
            double px = -ty / norma * medio;
            double py = tx / norma * medio;
            if (i == 0) {
                cuerpo.moveTo(xs[i] + px, ys[i] + py);
            } else {
                cuerpo.lineTo(xs[i] + px, ys[i] + py);
            }
            bordeX[i] = xs[i] - px;
            bordeY[i] = ys[i] - py;
        }
        for (int i = MUESTRAS_SERPIENTE; i >= 0; i--) {
            cuerpo.lineTo(bordeX[i], bordeY[i]);
        }
        cuerpo.closePath();

        Graphics2D sombra = (Graphics2D) g.create();
        sombra.translate(4, 5);
        sombra.setColor(new Color(0, 0, 0, 50));
        sombra.fill(cuerpo);
        sombra.dispose();

        g.setColor(piel[0]);
        g.fill(cuerpo);
        g.setColor(piel[2]);
        int manchas = (int) (largo / (ladoCelda() * 0.35));
        for (int i = 1; i < manchas; i++) {
            int indice = (int) ((double) i / manchas * MUESTRAS_SERPIENTE);
            double t = (double) indice / MUESTRAS_SERPIENTE;
            double radio = grosorCabeza * (1 - 0.85 * t) * 0.22;
            g.fill(new Ellipse2D.Double(xs[indice] - radio, ys[indice] - radio, 2 * radio, 2 * radio));
        }
        g.setColor(piel[1]);
        g.setStroke(new BasicStroke(1.6f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        g.draw(cuerpo);

        dibujarCabezaDeSerpiente(g, cabeza, -ux, -uy, grosorCabeza, piel);
    }

    /** Cabeza ovalada mirando hacia arriba del salto, con ojos y lengua bífida. */
    private void dibujarCabezaDeSerpiente(Graphics2D g, Point cabeza, double hx, double hy,
                                          double grosor, Color[] piel) {
        double radioLargo = grosor * 0.75;
        double radioCorto = grosor * 0.58;
        double angulo = Math.atan2(hy, hx);
        Graphics2D c = (Graphics2D) g.create();
        c.translate(cabeza.x, cabeza.y);
        c.rotate(angulo);

        Path2D lengua = new Path2D.Double();
        lengua.moveTo(radioLargo * 0.9, 0);
        lengua.lineTo(radioLargo * 1.55, 0);
        lengua.lineTo(radioLargo * 1.85, -radioCorto * 0.35);
        lengua.moveTo(radioLargo * 1.55, 0);
        lengua.lineTo(radioLargo * 1.85, radioCorto * 0.35);
        c.setColor(new Color(0xE11D48));
        c.setStroke(new BasicStroke(2f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        c.draw(lengua);

        Ellipse2D forma = new Ellipse2D.Double(-radioLargo, -radioCorto, 2 * radioLargo, 2 * radioCorto);
        c.setColor(piel[0]);
        c.fill(forma);
        c.setColor(piel[1]);
        c.setStroke(new BasicStroke(1.6f));
        c.draw(forma);

        double ojo = radioCorto * 0.55;
        double pupila = ojo * 0.5;
        double ojoX = radioLargo * 0.25;
        double ojoY = radioCorto * 0.45;
        for (int lado = -1; lado <= 1; lado += 2) {
            c.setColor(Color.WHITE);
            c.fill(new Ellipse2D.Double(ojoX - ojo / 2, lado * ojoY - ojo / 2, ojo, ojo));
            c.setColor(Color.BLACK);
            c.fill(new Ellipse2D.Double(ojoX + ojo * 0.1 - pupila / 2, lado * ojoY - pupila / 2, pupila, pupila));
        }
        c.dispose();
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

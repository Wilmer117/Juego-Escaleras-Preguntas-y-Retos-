package escalera.app;

import javax.swing.JFrame;
import javax.swing.WindowConstants;

/** Ventana principal de la interfaz gráfica. */
public class VentanaJuego extends JFrame {

    public VentanaJuego() {
        super("Laboratorio de Estructuras de Datos · Juego de escalera y retos");
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setContentPane(new PanelJuego());
        setSize(1200, 880);
        setLocationRelativeTo(null);
    }
}

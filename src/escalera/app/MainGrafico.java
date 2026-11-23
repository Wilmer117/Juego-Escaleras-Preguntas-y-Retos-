package escalera.app;

import javax.swing.SwingUtilities;
import javax.swing.UIManager;

/** Punto de entrada de la interfaz gráfica (Swing). */
public class MainGrafico {

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            usarAparienciaDelSistema();
            new VentanaJuego().setVisible(true);
        });
    }

    private static void usarAparienciaDelSistema() {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception error) {
            // Si no se puede, Swing usa su apariencia por defecto; el juego funciona igual.
        }
    }
}

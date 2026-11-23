package escalera.app;

import escalera.juego.ConfiguracionTablero;
import escalera.modelo.Jugador;

import javax.swing.BorderFactory;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;
import java.awt.Component;
import java.awt.GridLayout;

/** Diálogo para configurar una partida nueva de 2 a 4 jugadores, humanos o controlados por la IA. */
public final class DialogoJugadores {

    private static final String[] NOMBRES_SUGERIDOS = {"Ana", "Luis", "Marta", "Carlos"};

    private DialogoJugadores() {
    }

    /** @return los jugadores configurados, o null si el usuario cancela. */
    public static Jugador[] pedir(Component padre) {
        int maximo = ConfiguracionTablero.MAXIMO_JUGADORES;
        JSpinner cantidad = new JSpinner(new SpinnerNumberModel(3, ConfiguracionTablero.MINIMO_JUGADORES, maximo, 1));
        JTextField[] nombres = new JTextField[maximo];
        JCheckBox[] esIA = new JCheckBox[maximo];

        JPanel panel = new JPanel(new GridLayout(0, 2, 8, 6));
        panel.setBorder(BorderFactory.createEmptyBorder(6, 6, 6, 6));
        panel.add(new JLabel("Número de jugadores:"));
        panel.add(cantidad);
        for (int i = 0; i < maximo; i++) {
            nombres[i] = new JTextField(NOMBRES_SUGERIDOS[i], 12);
            esIA[i] = new JCheckBox("Lo controla la IA", i > 0);
            panel.add(nombres[i]);
            panel.add(esIA[i]);
        }
        Runnable actualizarFilas = () -> {
            int activos = (Integer) cantidad.getValue();
            for (int i = 0; i < maximo; i++) {
                nombres[i].setEnabled(i < activos);
                esIA[i].setEnabled(i < activos);
            }
        };
        cantidad.addChangeListener(evento -> actualizarFilas.run());
        actualizarFilas.run();

        while (true) {
            int opcion = JOptionPane.showConfirmDialog(padre, panel, "Nueva partida aleatoria",
                    JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
            if (opcion != JOptionPane.OK_OPTION) {
                return null;
            }
            int activos = (Integer) cantidad.getValue();
            String error = validar(nombres, activos);
            if (error == null) {
                Jugador[] jugadores = new Jugador[activos];
                for (int i = 0; i < activos; i++) {
                    jugadores[i] = new Jugador(nombres[i].getText().trim(), esIA[i].isSelected());
                }
                return jugadores;
            }
            JOptionPane.showMessageDialog(padre, error, "Revisa los nombres", JOptionPane.WARNING_MESSAGE);
        }
    }

    /** Nombres no vacíos, distintos y con iniciales distintas (para leer bien el tablero). */
    private static String validar(JTextField[] nombres, int activos) {
        for (int i = 0; i < activos; i++) {
            String nombre = nombres[i].getText().trim();
            if (nombre.isEmpty()) {
                return "El jugador " + (i + 1) + " no tiene nombre.";
            }
            for (int j = 0; j < i; j++) {
                String otro = nombres[j].getText().trim();
                if (otro.equalsIgnoreCase(nombre)) {
                    return "El nombre " + nombre + " está repetido.";
                }
                if (Character.toUpperCase(otro.charAt(0)) == Character.toUpperCase(nombre.charAt(0))) {
                    return nombre + " y " + otro + " empiezan por la misma letra; en el tablero se confundirían.";
                }
            }
        }
        return null;
    }
}

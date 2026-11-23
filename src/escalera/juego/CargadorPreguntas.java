package escalera.juego;

import escalera.modelo.Pregunta;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Cierre opcional: carga preguntas desde un archivo de texto.
 * Formato por línea: clave|pregunta|respuesta. Las líneas con # son comentarios.
 */
public final class CargadorPreguntas {

    private static final int MAXIMO_PREGUNTAS = 500;
    private static final int CAMPOS_POR_LINEA = 3;

    private CargadorPreguntas() {
    }

    /**
     * Lee el archivo. Las líneas mal formadas se avisan y se saltan.
     * @return las preguntas leídas, o las del enunciado si el archivo no existe o no trae ninguna.
     */
    public static Pregunta[] cargar(Path archivo, Bitacora bitacora) {
        if (archivo == null || !Files.exists(archivo)) {
            bitacora.registrar("Aviso: no se encontró el archivo de preguntas; se usan las del enunciado.");
            return ConfiguracionTablero.preguntasPorDefecto();
        }
        Pregunta[] leidas = new Pregunta[MAXIMO_PREGUNTAS];
        int cantidad = 0;
        try (BufferedReader lector = Files.newBufferedReader(archivo, StandardCharsets.UTF_8)) {
            String linea;
            int numeroLinea = 0;
            while ((linea = lector.readLine()) != null && cantidad < MAXIMO_PREGUNTAS) {
                numeroLinea++;
                Pregunta pregunta = interpretar(linea, numeroLinea, bitacora);
                if (pregunta != null) {
                    leidas[cantidad++] = pregunta;
                }
            }
        } catch (IOException error) {
            bitacora.registrar("Aviso: no se pudo leer el archivo (" + error.getMessage() + ").");
        }
        if (cantidad == 0) {
            bitacora.registrar("Aviso: el archivo no trae preguntas válidas; se usan las del enunciado.");
            return ConfiguracionTablero.preguntasPorDefecto();
        }
        Pregunta[] resultado = new Pregunta[cantidad];
        System.arraycopy(leidas, 0, resultado, 0, cantidad);
        bitacora.registrar("Se cargaron " + cantidad + " preguntas desde " + archivo.getFileName() + ".");
        return resultado;
    }

    private static Pregunta interpretar(String linea, int numeroLinea, Bitacora bitacora) {
        String limpia = linea.trim();
        if (limpia.isEmpty() || limpia.startsWith("#")) {
            return null;
        }
        String[] campos = limpia.split("\\|");
        if (campos.length != CAMPOS_POR_LINEA) {
            bitacora.registrar("Aviso: línea " + numeroLinea + " mal formada, se ignora.");
            return null;
        }
        try {
            return new Pregunta(Integer.parseInt(campos[0].trim()), campos[1].trim(), campos[2].trim());
        } catch (NumberFormatException error) {
            bitacora.registrar("Aviso: línea " + numeroLinea + " tiene una clave que no es número, se ignora.");
            return null;
        }
    }
}

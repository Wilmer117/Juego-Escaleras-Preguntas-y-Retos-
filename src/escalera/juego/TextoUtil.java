package escalera.juego;

import java.text.Normalizer;

/** Utilidades de texto compartidas. */
public final class TextoUtil {

    private TextoUtil() {
    }

    /** Minúsculas, sin espacios sobrantes y sin tildes: "  Sí " → "si". */
    public static String normalizar(String texto) {
        String sinTildes = Normalizer.normalize(texto.trim().toLowerCase(), Normalizer.Form.NFD)
                .replaceAll("\\p{M}", "");
        return sinTildes.replaceAll("\\s+", " ");
    }
}
